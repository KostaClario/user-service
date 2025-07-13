package com.kosta.userservice.controller;

import com.kosta.userservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/member")
@RequiredArgsConstructor
public class InternalMemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}/ci")
    public ResponseEntity<String> getMemberCi(@PathVariable String memberId) {
        return memberService.getMemberCiByMemberId(memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
