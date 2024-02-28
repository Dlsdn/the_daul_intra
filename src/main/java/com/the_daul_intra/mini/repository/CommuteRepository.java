package com.the_daul_intra.mini.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.the_daul_intra.mini.dto.entity.Commute;

@Repository
public interface CommuteRepository extends JpaRepository<Commute, Long> {
}