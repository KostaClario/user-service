package com.kosta.userservice.service;

import com.kosta.userservice.client.BankClient;
import com.kosta.userservice.domain.entity.Member;
import com.kosta.userservice.domain.enums.MemberStatus;
import com.kosta.userservice.domain.repository.MemberRepository;
import com.kosta.userservice.domain.enums.Role;
import com.kosta.userservice.dto.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BankClient bankClient;

    public MemberServiceImpl(MemberRepository memberRepository,
                             BCryptPasswordEncoder bCryptPasswordEncoder,
                             BankClient bankClient) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.bankClient = bankClient;
    }

    @Override
    public void createMember(JoinRequestDTO request) {
        validatePassword(request);

        Optional<Member> optionalMember = memberRepository.findByEmail(request.getEmail());

        if (optionalMember.isPresent()) {
            ExistingMember(optionalMember.get(), request);
        } else {
            NewMember(request);
        }
    }

    @Override
    public void updateProfile(String email, UpdateProfileRequestDTO request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (request.getName() != null && !request.getName().isBlank()) {
            member.setName(request.getName());
        }

        if (request.getPhoneNum() != null && !request.getPhoneNum().isBlank()) {
            member.setPhoneNum(request.getPhoneNum());
        }

        memberRepository.save(member);
    }

    @Override
    public void removeMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.setStatus(MemberStatus.INACTIVE);

        memberRepository.save(member);
    }

    @Override
    public void resetPassword(String email, ResetPasswordRequestDTO request) {

        validateResetPassword(request);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));

        member.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        memberRepository.save(member);

    }


    private void validateResetPassword(ResetPasswordRequestDTO request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void validatePassword(JoinRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void ExistingMember(Member member, JoinRequestDTO request) {
        if (MemberStatus.ACTIVE.equals(member.getStatus())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        member.setName(request.getName());
        member.setPhoneNum(request.getPhoneNum());
        member.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        member.setStatus(MemberStatus.ACTIVE);
        member.setMydataConsentedAt(LocalDateTime.now());
        member.setPersonalInfoAgreedAt(LocalDateTime.now());

        memberRepository.save(member);
    }

    private void NewMember(JoinRequestDTO request) {

        BankUserInfoResponse bankInfo = bankClient.getUserInfo(
                new BankUserInfoRequest(request.getEmail(), request.getPhoneNum())
        );

        Member newMember = Member.builder()
                .name(request.getName())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .phoneNum(request.getPhoneNum())
                .email(request.getEmail().toLowerCase())
                .totalAmount(0L)
                .goalAmount(0L)
                .status(MemberStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .memberCi(bankInfo.getMemberCi())
                .build();

        memberRepository.save(newMember);
    }


    public boolean checkPassword(String email, PasswordCheckRequestDTO passwordCheckRequest) {

        String rawPassword = passwordCheckRequest.getPassword();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return bCryptPasswordEncoder.matches(rawPassword, member.getPassword());
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
}
