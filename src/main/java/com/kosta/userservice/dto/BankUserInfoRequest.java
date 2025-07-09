package com.kosta.userservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankUserInfoRequest {

    private String email;
    private String phone;
}
