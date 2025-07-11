package com.kosta.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordCheckRequestDTO {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
