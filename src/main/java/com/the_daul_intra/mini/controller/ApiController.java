package com.the_daul_intra.mini.controller;


import com.the_daul_intra.mini.dto.entity.DetailsLeaveAbsence;
import com.the_daul_intra.mini.dto.request.*;
import com.the_daul_intra.mini.dto.response.*;
import com.the_daul_intra.mini.service.ApiCommuteService;
import com.the_daul_intra.mini.service.ApiEmpService;
import com.the_daul_intra.mini.service.ApiLeaveService;
import com.the_daul_intra.mini.service.ApiNoticeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final ApiEmpService apiEmpService;
    private final ApiLeaveService apiLeaveService;
    private final ApiNoticeService apiNoticeService;
    private final ApiCommuteService apiCommuteService;  // 출퇴근 api 서비스

    //로그인 컨트롤러
    @PostMapping("/login")
    public ResponseEntity<ApiLoginResponse> login(@RequestBody ApiLoginPostRequest request) {
        return ResponseEntity.ok(apiEmpService.apiLogin(request));
    }

    @GetMapping("/login")
    public ResponseEntity<ApiLoginResponse> login() {
        return ResponseEntity.ok(apiEmpService.apiGetLogin());
    }

    //휴가신청서 작성 컨트롤러
    @PostMapping("/off")
    public ResponseEntity<?> createLeaveRequest(@RequestBody ApiLeavePostRequest request) {
        apiLeaveService.createLeaveRequest(request);
        return ResponseEntity.ok("목록 페이지로 돌아갑니다.");
    }

    //휴가신청서 리스트
    @GetMapping("/off")
    public ResponseEntity<List<ApiOffListItemResponse>> searchLeaveListRequests(@ModelAttribute ApiLeaveSearchRequest request) {
        List<ApiOffListItemResponse> response = apiLeaveService.searchLeavesList(request);
        return ResponseEntity.ok(response);
    }

    //휴가신청서 상세
    @GetMapping("/off/{id}")
    public ResponseEntity<ApiOffDetailResponse> getLeaveDetail(@PathVariable Long id) {
        ApiOffDetailResponse response = apiLeaveService.getLeaveDetails(id);
        return ResponseEntity.ok(response);
    }

    //공지사항 전체목록
    @GetMapping("/notice")
    public ResponseEntity<List<ApiNoticeListItemResponse>> getNoticesAllList() {
        List<ApiNoticeListItemResponse> notices = apiNoticeService.getNoticeAllList();
        return ResponseEntity.ok(notices);
    }


    //공지사항 최근목록
    @GetMapping("/noticerecent")
    public ResponseEntity<ApiNoticeListResponse> getNoticesPagingList(
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "최소 페이지는 1페이지 입니다.") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size) {
        ApiNoticeListResponse notices = apiNoticeService.getNoticePagingList(page, size);
        return ResponseEntity.ok(notices);
    }

    //공지사항 상세
    @GetMapping("/notice/{id}")
    public ResponseEntity<ApiNoticeResponse> getNotice(@PathVariable Long id) {
        ApiNoticeResponse response = apiNoticeService.getNoticeDetails(id);
        return ResponseEntity.ok(response);
    }

    // 출퇴근 기록 작성 컨트롤러
    @PostMapping("/commute")
    public ResponseEntity<?> postCommute(@RequestBody ApiCommuteRequest commute) {
        apiCommuteService.createCommute(commute);
        return ResponseEntity.ok("출퇴근 완료");
    }
}
