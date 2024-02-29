package com.the_daul_intra.mini.dto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "EMPLOYEE_PROFILE")
public class EmployeeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")
    private Employee employee;

    @Column(name = "NAME", nullable = false, length = 20)
    private String name;

    @Column(name = "E_MAIL", nullable = false, length = 200)
    private String email;

    @Column(name = "RESIDENT_REGISTRATION_INT", length = 256)
    private String residentRegistrationNumber;

    @Column(name = "POSITION", length = 10)
    private String position;

    @Column(name = "JOIN_DATE")
    private LocalDateTime joinDate;

    @Column(name = "RETIREMENT_DATE")
    private LocalDateTime retirementDate;

    @Column(name = "CONTACT_INFORMATION", nullable = false, length = 20)
    private String contactInformation;

    @Column(name = "ADDRESS", length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROJECT_STATUS")
    private YesNo projectStatus = YesNo.N;

    @Column(name = "EMPLOYEE_NUMBER", columnDefinition = "TEXT")
    private String employeeNumber;

    @Column(name = "ADMIN_COMMENT", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "ANNUAL_QUANTITY", nullable = false)
    private Long annualQuantity;

    @OneToMany(mappedBy = "employeeProfile", fetch = FetchType.LAZY)
    private Set<Commute> commute;       // 사원 출근 기록
}
