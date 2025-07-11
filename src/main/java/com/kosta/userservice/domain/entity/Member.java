package com.kosta.userservice.domain.entity;

import com.kosta.userservice.domain.enums.MemberStatus;
import com.kosta.userservice.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    private String phoneNum;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, updatable = false)
    private String memberId;

    @Column(nullable = false, unique = true)
    private String memberCi;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime personalInfoAgreedAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime mydataConsentedAt;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private Long goalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @PrePersist
    public void assignUUID() {
        if (this.memberId == null) {
            this.memberId = UUID.randomUUID().toString();
        }
    }
}
