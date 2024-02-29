package com.the_daul_intra.mini.controller;

import com.the_daul_intra.mini.dto.response.CommuteListResponse;
import com.the_daul_intra.mini.service.CommuteService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CommuteController {
    // 직원 출퇴근 컨트롤러

    private final CommuteService commuteService;    // 직원 출퇴근 서비스 로직

    @GetMapping("/commute")
    public String commuteList(
            @RequestParam(value = "page", defaultValue = "1") @Min(value = 1, message = "최소 페이지는 1페이지 입니다.") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(required = false) String onOffWorkType,
            Model model) {
        Page<CommuteListResponse> commuteList = commuteService.getCommuteList(page, size, onOffWorkType);   // 서비스 함수 호출
        model.addAttribute("commuteList", commuteList); // 관리자 웹 페이지 측, mvc, model에 저장
        return "commute";   // html 파일 이름, 표시해 두면 자동으로 그 파일을 찾아감
    }
}
