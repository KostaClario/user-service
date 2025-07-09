package com.kosta.userservice.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    private String password;
    private String confirmPassword;
}
