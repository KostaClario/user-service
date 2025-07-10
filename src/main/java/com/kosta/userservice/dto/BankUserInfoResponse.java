package com.kosta.userservice.dto;


import lombok.Data;

@Data
public class BankUserInfoResponse {
    private String name;
    private String email;
    private String phone;
    private String memberCi;
    private String bankAccount;
}
