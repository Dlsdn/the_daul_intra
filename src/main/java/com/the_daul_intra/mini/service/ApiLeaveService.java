package com.the_daul_intra.mini.service;

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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiLeaveService {

    private final ApiDetailsLeaveAbsenceRepository apiDetailsLeaveAbsenceRepository;
    private final ApiEmpLoginRepository apiEmpLoginRepository;
    private final ApiDetailsLeaveDateRepository apiDetailsLeaveDateRepository;
    Long empId = 1L;

    //휴가신청서 작성
    @Transactional
    public DetailsLeaveAbsence createLeaveRequest(ApiLeavePostRequest request) {

        //인증정보를 바탕으로 empId확인 후 없다면 로그인페이지로 이동
        Employee employee = apiEmpLoginRepository.findById(empId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"  로그인 내역이 존재하지 않습니다."));

        DetailsLeaveAbsence leaveRequest = DetailsLeaveAbsence.builder()
                .employee(/*writer*/employee)
                .absenceLeavePeriod((long) request.getUseDates().length)
                .absenceType(/*request.getType()*/"오전반차!")
                .applicantComments(/*request.getReason()*/"쉬고싶어요")
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
        //검색 조건에 따른 검색 실행
        List<DetailsLeaveAbsence> leaves = apiDetailsLeaveAbsenceRepository.findAll(LeaveSpecifications.withCriteria(request));

        //검색 결과 반환
        return leaves.stream().map(leave -> new ApiOffListItemResponse(
                leave.getId(),
                leave.getEmployee().getId(),
                leave.getApplicationDate().toString(),
                leave.getProcessingStatus(),
                leave.getAbsenceType()
        )).collect(Collectors.toList());
    }

    public ApiOffDetailResponse getLeaveDetails(Long requestId) {
        DetailsLeaveAbsence leaveAbsence = apiDetailsLeaveAbsenceRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND," 해당 신청서가 존재하지 않습니다."));

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
                .regDate(leaveAbsence.getApplicationDate().toString())
                .receiveDate(leaveAbsence.getReceptionDate().toString())
                .confirmDate(leaveAbsence.getProcessedDate().toString())
                .receiveAdmin(leaveAbsence.getReceptionAdmin().getEmployeeProfile().getName())
                .confirmAdmin(leaveAbsence.getProcessedAdmin().getEmployeeProfile().getName())
                .reason(leaveAbsence.getApplicantComments())
                .adminComment(leaveAbsence.getAdminComment())
                .build();
    }
}