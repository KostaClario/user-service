package com.kosta.userservice.service;

import com.kosta.userservice.dto.JoinRequestDTO;
import com.kosta.userservice.dto.ResetPasswordRequestDTO;
import com.kosta.userservice.dto.UpdateProfileRequestDTO;

public interface MemberService {
    void createMember(JoinRequestDTO request);
    void updateProfile(String email, UpdateProfileRequestDTO request);
    void removeMember(String email);
    void resetPassword(String email, ResetPasswordRequestDTO request);
}
