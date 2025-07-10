package com.kosta.userservice.service;

import com.kosta.userservice.client.BankClient;
import com.kosta.userservice.domain.Member;
import com.kosta.userservice.domain.MemberRepository;
import com.kosta.userservice.domain.Role;
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

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            member.setPhone(request.getPhone());
        }

        memberRepository.save(member);
    }

    @Override
    public void removeMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.setActivated(false);
        member.setMyDataAgreedAt(null);

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
        if (member.isActivated()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        member.setActivated(true);
        member.setMyDataAgreedAt(LocalDateTime.now());

        memberRepository.save(member);
    }

    private void NewMember(JoinRequestDTO request) {

        BankUserInfoResponse bankInfo = bankClient.getUserInfo(
                new BankUserInfoRequest(request.getEmail(), request.getPhone())
        );

        Member newMember = Member.builder()
                .name(request.getName())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .provider("google")
                .totalAmount(0L)
                .goalAmount(0L)
                .activated(true)
                .role(Role.ROLE_USER)
                .myDataAgreedAt(LocalDateTime.now())
                .ci(bankInfo.getCi())
                .build();

        memberRepository.save(newMember);
    }


    public boolean checkPassword(String email, String password) {

        return memberRepository.findByEmail(email)
                .map(member -> {
                    boolean result = bCryptPasswordEncoder.matches(password, member.getPassword());
                    return result;
                })
                .orElse(false);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
}
