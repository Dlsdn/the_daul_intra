package com.the_daul_intra.mini.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.the_daul_intra.mini.dto.entity.Commute;

@Repository
public interface CommuteRepository extends JpaRepository<Commute, Long> {
    /*  
    
    Commute Repository, DB와 통신함. 
    find(), finaAll(), save() 같은 기본적인 함수들은 이미 있으니 아래에는 다른 필요한 함수들만 적을 것

    */
}