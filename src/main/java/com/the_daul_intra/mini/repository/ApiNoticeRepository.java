package com.the_daul_intra.mini.repository;

import com.the_daul_intra.mini.dto.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiNoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {
    Page<Notice> findAll(Specification<Notice> spec, Pageable pageable);
}
