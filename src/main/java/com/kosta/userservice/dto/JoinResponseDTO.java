package com.kosta.userservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinResponseDTO {

    private String email;
    private String name;
    private String accessToken;
}
