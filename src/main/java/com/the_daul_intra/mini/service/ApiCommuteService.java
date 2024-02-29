package com.the_daul_intra.mini.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.the_daul_intra.mini.dto.EmpDetails;
import com.the_daul_intra.mini.dto.entity.Commute;
import com.the_daul_intra.mini.dto.entity.Employee;
import com.the_daul_intra.mini.dto.entity.EmployeeProfile;
import com.the_daul_intra.mini.dto.request.ApiCommuteRequest;
import com.the_daul_intra.mini.exception.AppException;
import com.the_daul_intra.mini.exception.ErrorCode;
import com.the_daul_intra.mini.repository.CommuteRepository;
import com.the_daul_intra.mini.repository.EmployeeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiCommuteService {

    private final EmployeeRepository employeeRepository;
    private final CommuteRepository commuteRepository;

    Long empId = null;
    Employee employee = null;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    

    // 출퇴근 기록 작성 함수, commuteReq = 프론트엔드에서 받아온 출퇴근 기록 값
    @Transactional
    public void createCommute(ApiCommuteRequest commuteReq) {
        // authentication 객체에 SecurityContextHolder를 담아 인증 정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // authentication에서 empId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            empId = ((EmpDetails) authentication.getPrincipal()).getEmpId();
            employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "로그인 이력이 없습니다. 로그인 페이지로 돌아갑니다."));
        }

        // 출퇴근 테이블의 FK인 employeeProfile에 저장될 값, employee 테이블에 조인된 EmployeeProfile 필드를 가져옴
        EmployeeProfile employeeProfile = employee.getEmployeeProfile();

        // String 값인 출퇴근 시간을 LocalDateTime으로 형 변환
        LocalDateTime onWorkTime = LocalDateTime.parse(commuteReq.getOnWorkTime(), formatter);
        LocalDateTime offWorkTime = LocalDateTime.parse(commuteReq.getOffWorkTime(), formatter);

        // Commute 테이블에 들어갈 필드들의 값을 Build
        Commute commute = Commute.builder()
                .employeeProfile(employeeProfile)
                .onWorkTime(onWorkTime)
                .onWorkLatitude(commuteReq.getOnWorkLatitude())
                .onWorkLongitude(commuteReq.getOnWorkLongitude())
                .onWorkIPAddress(commuteReq.getOnWorkIPAddress())
                .offWorkTime(offWorkTime)
                .offWorkLatitude(commuteReq.getOffWorkLatitude())
                .offWorkLongitude(commuteReq.getOffWorkLongitude())
                .offWorkIPAddress(commuteReq.getOffWorkIPAddress())
                .build();

        // build한 값을 Repository, 즉 DB에 저장
        commuteRepository.save(commute);
    }

}
