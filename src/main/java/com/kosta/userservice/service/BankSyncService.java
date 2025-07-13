package com.kosta.userservice.service;


import com.kosta.userservice.client.BankClient;
import com.kosta.userservice.client.BankDetailClient;
import com.kosta.userservice.domain.entity.Member;
import com.kosta.userservice.domain.repository.MemberRepository;
import com.kosta.userservice.dto.BankApiDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankSyncService {

    private final BankClient bankClient;
    private final BankDetailClient bankDetailClient;
    private final MemberRepository memberRepository;

    public BankSyncService(BankClient bankClient, BankDetailClient bankDetailClient, MemberRepository memberRepository) {
        this.bankClient = bankClient;
        this.bankDetailClient = bankDetailClient;
        this.memberRepository = memberRepository;
    }

    public void syncBankTransaction(String memberCi) {

        Member member = memberRepository.findByMemberCi(memberCi)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<BankApiDTO> transaction = bankClient.getTransactions(memberCi);

        List<BankApiDTO> enriched = transaction.stream()
                        .map(dto -> {
                            dto.setMemberId(member.getMemberId().toString());
                            return dto;
                        }).toList();

        bankDetailClient.syncAndSave(member.getMemberId().toString(), enriched);
    }
}
