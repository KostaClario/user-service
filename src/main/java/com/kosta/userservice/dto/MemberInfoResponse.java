package com.kosta.userservice.dto;

import lombok.Data;

@Data
public class MemberInfoResponse {

    private String email;
    private String name;
    private String picture;
}
