package com.the_daul_intra.mini.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    private String email;

    @Column(length = 256)
    private String password;

    @Enumerated(EnumType.STRING)
    private YesNo adminStatus;

    @OneToOne(mappedBy = "employee", fetch = FetchType.LAZY)
    private EmployeeProfile employeeProfile;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private Set<NoticeReadStatus> noticeReadStatuses;
}