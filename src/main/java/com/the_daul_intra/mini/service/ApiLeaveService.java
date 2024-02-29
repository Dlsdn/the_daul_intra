package com.the_daul_intra.mini.service;

import com.the_daul_intra.mini.dto.EmpDetails;
import com.the_daul_intra.mini.dto.entity.DetailsLeaveAbsence;
import com.the_daul_intra.mini.dto.entity.DetailsLeaveDate;
import com.the_daul_intra.mini.dto.entity.Employee;
import com.the_daul_intra.mini.dto.request.ApiLeavePostRequest;
import com.the_daul_intra.mini.dto.request.ApiLeaveSearchRequest;
import com.the_daul_intra.mini.dto.response.ApiOffDetailResponse;
import com.the_daul_intra.mini.dto.response.ApiOffListItemResponse;
import com.the_daul_intra.mini.exception.AppException;
import com.the_daul_intra.mini.exception.ErrorCode;
import com.the_daul_intra.mini.repository.ApiDetailsLeaveAbsenceRepository;
import com.the_daul_intra.mini.repository.ApiDetailsLeaveDateRepository;
import com.the_daul_intra.mini.repository.ApiEmpLoginRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiLeaveService {

    private final ApiDetailsLeaveAbsenceRepository apiDetailsLeaveAbsenceRepository;
    private final ApiEmpLoginRepository apiEmpLoginRepository;
    private final ApiDetailsLeaveDateRepository apiDetailsLeaveDateRepository;
    Long empId = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    //휴가신청서 작성
    @Transactional
    public DetailsLeaveAbsence createLeaveRequest(ApiLeavePostRequest request) {

        //authentication객체에 SecurityContextHolder를 담아서 인증정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //authentication에서 empId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            empId = ((EmpDetails) authentication.getPrincipal()).getEmpId();
        }

        //인증정보를 바탕으로 empId확인 후 없다면 로그인페이지로 이동
        Employee employee = apiEmpLoginRepository.findById(empId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "  로그인 내역이 존재하지 않습니다."));

        // 현재 월과 일자
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        // 입사 일자와 일, 현재 잔여 연차 갯수
        LocalDate joinDate = employee.getEmployeeProfile().getJoinDate().toLocalDate();
        int joinDay = joinDate.getDayOfMonth();
        Long offCount = employee.getEmployeeProfile().getAnnualQuantity();

        // 말일이 현재 월의 말일을 초과한 경우 현재 월의 말일로 변경
        int lastDayOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).getDayOfMonth();
        joinDay = Math.min(joinDay, lastDayOfMonth);

        // 연차발생 시점
        LocalDate annualCreateDate = LocalDate.of(currentYear, currentMonth, joinDay);

        // 연차를 신청한 일 중 가장 빠른 일자와 연차 신청이 가능한 최대 일자
        LocalDate minDate = Arrays.stream(request.getUseDates()).min(LocalDate::compareTo).orElse(null);
        LocalDate minDatePlus30 = minDate.plusDays(30);

        // 연차 사용 개수
        Long requestOffCount = (long) request.getUseDates().length;

        // 요청타입이 연차인지 확인
        boolean isAnnual = request.getType().equals("연차") || request.getType().equals("반차(오전)") || request.getType().equals("반차(오후)") || request.getType().equals("월차");

        //연차를 신청한 날짜와 연차신청일+30 사이에 연차발생일자가 들어가는지 유무 확인
        boolean isBetween = isDateBetween(minDate, minDatePlus30, annualCreateDate);

        // 근속일수가 1년 미만인 경우 발생할 연차를 미리 신청할 수 있게 해준다.
        // 비슷한 예로 1년 이상의 경우도 미리 신청은 가능하게 만들어야 한다
        System.out.println("minDate : " + minDate + "\nminDatePlus30 : " + minDatePlus30 + "\nannualCreateDate : " + annualCreateDate + "\nisBetween : " + isBetween);
        if (ChronoUnit.DAYS.between(joinDate, currentDate) < 365) {
            //신청일 기준 30일 이내 연차가 생길 경우 +1을 하여 계산(실제 추가되지는 않음)
            if(isBetween) {
                offCount++;
            }
        }

        // 신청 갯수보다 사용 가능한 연차가 적을 때
        if (offCount < requestOffCount && isAnnual) {
            throw new AppException(ErrorCode.INVALID_OPERATION, "사용 가능한 연차가 없습니다.");
        }

        DetailsLeaveAbsence leaveRequest = DetailsLeaveAbsence.builder()
                .employee(employee)
                .absenceLeavePeriod(requestOffCount)
                .absenceType(request.getType())
                .applicantComments(request.getReason())
                .applicationDate(LocalDateTime.now())
                .processingStatus("신청")
                .build();
        leaveRequest = apiDetailsLeaveAbsenceRepository.save(leaveRequest);
        for (LocalDate useDate : request.getUseDates()) {

            DetailsLeaveDate leaveDate = DetailsLeaveDate.builder()
                    .leaveRequest(leaveRequest)
                    .employee(employee)
                    .useDate(useDate)
                    .build();

            apiDetailsLeaveDateRepository.save(leaveDate);
        }
        return leaveRequest;
    }


    public List<ApiOffListItemResponse> searchLeavesList(ApiLeaveSearchRequest request) {

        //authentication객체에 SecurityContextHolder를 담아서 인증정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //authentication에서 empId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            empId = ((EmpDetails) authentication.getPrincipal()).getEmpId();
        }

        //검색 조건에 따른 검색 실행
        List<DetailsLeaveAbsence> leaves = apiDetailsLeaveAbsenceRepository.findAll(LeaveSpecifications.withCriteria(request, empId), Sort.by(Sort.Direction.DESC, "applicationDate"));

        //검색 결과 반환
        return leaves.stream().map(leave -> new ApiOffListItemResponse(
                leave.getId(),
                leave.getEmployee().getId(),
                leave.getApplicationDate().format(formatter),
                leave.getProcessingStatus(),
                leave.getAbsenceType()
        )).collect(Collectors.toList());
    }

    public ApiOffDetailResponse getLeaveDetails(Long requestId) {

        //authentication객체에 SecurityContextHolder를 담아서 인증정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //authentication에서 empId 추출
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            empId = ((EmpDetails) authentication.getPrincipal()).getEmpId();
        }

        DetailsLeaveAbsence leaveAbsence = apiDetailsLeaveAbsenceRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, " 해당 신청서가 존재하지 않습니다."));

        Set<DetailsLeaveDate> leaveDates = leaveAbsence.getLeaveDates();
        LocalDate[] useDates = leaveDates.stream()
                .map(DetailsLeaveDate::getUseDate)
                .toArray(LocalDate[]::new);

        Employee employee = leaveAbsence.getEmployee();

        return ApiOffDetailResponse.builder()
                .id(leaveAbsence.getId())
                .writerId(employee.getId())
                .writerName(employee.getEmployeeProfile().getName())
                .requestType(leaveAbsence.getAbsenceType())
                .status(leaveAbsence.getProcessingStatus())
                .leavePeriod(Long.toString(leaveAbsence.getAbsenceLeavePeriod()))
                .useDates(useDates)
                .regDate(leaveAbsence.getApplicationDate() != null ? leaveAbsence.getApplicationDate().format(formatter) : null)
                .receiveDate(leaveAbsence.getReceptionDate() != null ? leaveAbsence.getReceptionDate().format(formatter) : null)
                .confirmDate(leaveAbsence.getProcessedDate() != null ? leaveAbsence.getProcessedDate().format(formatter) : null)
                .receiveAdmin(leaveAbsence.getReceptionAdmin() != null && leaveAbsence.getReceptionAdmin().getEmployeeProfile() != null ? leaveAbsence.getReceptionAdmin().getEmployeeProfile().getName() : null)
                .confirmAdmin(leaveAbsence.getProcessedAdmin() != null && leaveAbsence.getProcessedAdmin().getEmployeeProfile() != null ? leaveAbsence.getProcessedAdmin().getEmployeeProfile().getName() : null)
                .reason(leaveAbsence.getApplicantComments())
                .adminComment(leaveAbsence.getAdminComment())
                .build();
    }

    private static boolean isDateBetween(LocalDate startDate, LocalDate endDate, LocalDate annualCreateDate) {
        // 연차 발생시점이 신청일 이후이고 신청일로부터 30일 이내인지 확인
        return !annualCreateDate.isBefore(startDate) && !annualCreateDate.isAfter(endDate);
    }
}
