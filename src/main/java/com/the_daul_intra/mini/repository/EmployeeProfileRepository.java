package com.the_daul_intra.mini.repository;

import com.the_daul_intra.mini.dto.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long>, JpaSpecificationExecutor<EmployeeProfile> {
    EmployeeProfile findByEmail(String email);

    List<EmployeeProfile> findByRetirementDateIsNull();
}