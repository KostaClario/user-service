package com.kosta.userservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinRequestDTO {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "제공자는 필수입니다.")
    private String provider;

    @NotNull(message = "총 금액은 필수입니다.")
    private Long totalAmount;

    @NotNull(message = "목표 금액은 필수입니다.")
    private Long goalAmount;
}
