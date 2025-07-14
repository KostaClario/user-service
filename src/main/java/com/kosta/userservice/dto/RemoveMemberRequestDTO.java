package com.kosta.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RemoveMemberRequestDTO {

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;
}
