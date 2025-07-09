package com.kosta.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
    private String phone;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 30)
    private String provider;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private Long goalAmount;

    @Column(nullable = false)
    private boolean activated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column
    private LocalDateTime myDataAgreedAt;

    @PrePersist
    public void prePersist() {
        if (myDataAgreedAt == null) {
            myDataAgreedAt = LocalDateTime.now();
        }
    }

    @Column(length = 100, unique = true)
    private String ci;

}
