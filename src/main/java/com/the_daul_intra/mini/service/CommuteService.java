package com.the_daul_intra.mini.service;

import com.the_daul_intra.mini.dto.EmpDetails;
import com.the_daul_intra.mini.dto.entity.Commute;
import com.the_daul_intra.mini.dto.entity.Employee;
import com.the_daul_intra.mini.dto.response.CommuteListResponse;
import com.the_daul_intra.mini.exception.AppException;
import com.the_daul_intra.mini.exception.ErrorCode;
import com.the_daul_intra.mini.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class CommuteService {

    private final EmployeeRepository employeeRepository;
    private final CommuteRepository commuteRepository;

    Long empId = null;
    Employee employee = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | E | HH:mm");

    // 출퇴근 기록 GET 함수
    public Page<CommuteListResponse> getCommuteList(Integer page, Integer size, String onOffWorkType) {
        // authentication 객체에 SecurityContextHolder를 담아 인증 정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // authentication에서 empId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            empId = ((EmpDetails) authentication.getPrincipal()).getEmpId();
            employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "로그인 이력이 없습니다. 로그인 페이지로 돌아갑니다."));
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("onWorkTime").ascending());

        // 출퇴근 기록 조회
        Page<Commute> commutePages = commuteRepository.findAll(pageable);
        // 출퇴근 기록 총 개수
        long totalList = commutePages.getTotalElements();
        // 현재 페이지의 첫 번째 출퇴근 기록 번호
        AtomicInteger startNumber = new AtomicInteger((int) totalList - (page - 1) * size);

        // getCommuteList()는 출퇴근 기록 전체를 리턴
        return commutePages.map(commuteList -> {
            // 출퇴근 시간 FORMAT
            String onWorkTimeStr = commuteList.getOnWorkTime() != null ? commuteList.getOnWorkTime().format(formatter) : null;
            String offWorkTimeStr = commuteList.getOffWorkTime() != null ? commuteList.getOffWorkTime().format(formatter) : null;
            
            // 출퇴근 기록 하나를 리턴, map() 함수를 통해 배열 안의 원소를 전부 꺼내기 때문에, 결국 기록 전체가 리턴되게 된다
            return new CommuteListResponse(
                    commuteList.getId(),
                    (long) startNumber.getAndDecrement(),
                    commuteList.getEmployeeProfile().getName(),
                    onWorkTimeStr,
                    commuteList.getOnWorkLatitude(),
                    commuteList.getOnWorkLongitude(),
                    commuteList.getOnWorkIPAddress(),
                    String.valueOf(commuteList.getOnWorkStatus()),
                    offWorkTimeStr,
                    commuteList.getOffWorkLatitude(),
                    commuteList.getOffWorkLongitude(),
                    commuteList.getOffWorkIPAddress(),
                    String.valueOf(commuteList.getOffWorkStatus())
            );
        });
    }
    
}
