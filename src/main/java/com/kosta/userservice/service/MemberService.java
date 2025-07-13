package com.kosta.userservice.service;

import com.kosta.userservice.dto.JoinRequestDTO;
import com.kosta.userservice.dto.ResetPasswordRequestDTO;
import com.kosta.userservice.dto.UpdateProfileRequestDTO;

import java.util.Optional;

public interface MemberService {
    void createMember(JoinRequestDTO request);
    void updateProfile(String email, UpdateProfileRequestDTO request);
    void removeMember(String email);
    void resetPassword(String email, ResetPasswordRequestDTO request);
    Optional<String> getMemberCiByMemberId(String memberId);
}
