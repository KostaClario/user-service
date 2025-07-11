package com.kosta.userservice.service;

import com.kosta.userservice.domain.entity.Member;
import com.kosta.userservice.domain.enums.MemberStatus;
import com.kosta.userservice.domain.repository.MemberRepository;
import com.kosta.userservice.dto.JoinRequestDTO;
import com.kosta.userservice.dto.UpdateProfileRequestDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberServiceImpl memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void testSuccessJoin(){
        // given
        JoinRequestDTO request = new JoinRequestDTO();
        request.setName("손흥민");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setPhoneNum("01012341234");
        request.setEmail("sonny@email.com");
//        request.setProvider("google");
//        request.setTotalAmount(0L);
//        request.setGoalAmount(10000L);

        // when
        memberService.createMember(request);

        // then
        Member save = memberRepository.findByEmail("sonny@email.com").orElse(null);
        assertThat(save).isNotNull();
        assertThat(save.getName()).isEqualTo("손흥민");
    }

    @Test
    @DisplayName("이메일 중복 시 예외")
    void testFailJoin(){
        // given
        String email = "sonny@email.com";

        JoinRequestDTO request1 = new JoinRequestDTO();
        request1.setName("손흥민");
        request1.setPassword("123456");
        request1.setConfirmPassword("123456");
        request1.setPhoneNum("01012341234");
        request1.setEmail("sonny@email.com");
//        request1.setProvider("google");
//        request1.setTotalAmount(0L);
//        request1.setGoalAmount(10000L);
        memberService.createMember(request1);

        JoinRequestDTO request2 = new JoinRequestDTO();
        request2.setName("김민재");
        request2.setPassword("123456");
        request2.setConfirmPassword("123456");
        request2.setPhoneNum("01012331231");
        request2.setEmail("sonny@email.com");
//        request2.setProvider("google");
//        request2.setTotalAmount(0L);
//        request2.setGoalAmount(10000L);

        // when then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(request2);
        });
    }

    @Test
    @DisplayName("비밀번호 확인 실패 시 예외")
    void testFailJoin2(){
        // given
        JoinRequestDTO request = new JoinRequestDTO();
        request.setName("손흥민");
        request.setPassword("222222");
        request.setConfirmPassword("111111");
        request.setPhoneNum("01012341234");
        request.setEmail("sonny@email.com");
//        request.setProvider("google");
//        request.setTotalAmount(0L);
//        request.setGoalAmount(10000L);

        // when then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.createMember(request);
        });
    }

    @Test
    @DisplayName("회원정보수정 성공")
    void testUpdateMember(){

        // given
        String email = "kim@email.com";

        Member member = Member.builder()
                .id(2L)
                .email(email)
                .name("기존이름")
                .phoneNum("010-0000-0000")
                .status(MemberStatus.ACTIVE)
                .password("password")
                .totalAmount(0L)
                .goalAmount(1000L)
                .build();

        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO();
        request.setName("변경이름");
        request.setPhoneNum("010-1111-1111");

        // when
        memberService.updateProfile(member.getEmail(), request);

        // then
        Member update = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않음"));


        assertThat(update.getName()).isEqualTo("변경이름");
        assertThat(update.getPhoneNum()).isEqualTo("010-1111-1111");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 수정시 예외")
    void testUpdateFailMember(){

        // given
        String email = "fail@email.com";

        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO();
        request.setName("변경이름");
        request.setPhoneNum("010-0000-0000");

        // when then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.updateProfile(email, request);
        });
    }
}