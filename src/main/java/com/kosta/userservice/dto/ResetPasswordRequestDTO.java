package com.kosta.userservice.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    private String newPassword;
    private String confirmNewPassword;
}
