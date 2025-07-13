package com.kosta.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankApiDTO {
    private String memberId;
    private String accountNum;
    private String prodName;
    private String orgName;
    private BigDecimal balanceAmt;
    private BigDecimal transAmt;
    private String transSource;
    private String transType;
    private LocalDateTime transDtime;
}
