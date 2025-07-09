package com.kosta.userservice.client;


import com.kosta.userservice.dto.BankUserInfoRequest;
import com.kosta.userservice.dto.BankUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient (name = "bank-service")
public interface BankClient {

    @PostMapping("/api/bank/user-info")
    BankUserInfoResponse getUserInfo(@RequestBody BankUserInfoRequest request);
}
