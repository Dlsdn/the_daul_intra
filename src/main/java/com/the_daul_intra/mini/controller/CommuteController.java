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
        Page<CommuteListResponse> commuteList = commuteService.getCommuteList(page, size, onOffWorkType);
        model.addAttribute("commuteList", commuteList);
        return "commute";
    }
}
