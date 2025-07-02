package com.kosta.userservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequestDTO {
    private String name;
    private String phone;
}
