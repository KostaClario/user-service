package com.kosta.userservice.client;


import com.kosta.userservice.dto.BankApiDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "bankdetail-service")
public interface BankDetailClient {

    @PostMapping("/bank-detail/internal/save/{memberId}")
    void syncAndSave(
            @PathVariable("memberId") String memberId,
            @RequestBody List<BankApiDTO> transaction
    );
}
