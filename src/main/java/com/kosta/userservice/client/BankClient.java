package com.kosta.userservice.client;


import com.kosta.userservice.dto.BankApiDTO;
import com.kosta.userservice.dto.BankUserInfoRequest;
import com.kosta.userservice.dto.BankUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient (name = "bank-service")
public interface BankClient {

    @PostMapping("/api/bank/user-info")
    BankUserInfoResponse getUserInfo(@RequestBody BankUserInfoRequest request);


    @GetMapping("/internal/transactions/{memberCi}")
    List<BankApiDTO> getTransactions(@PathVariable("memberCi") String memberCi);
}
