package com.the_daul_intra.mini.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiCommuteRequest {
    private String onWorkTime;      // 출근 시간
    private String onWorkLatitude;  // 출근 위도
    private String onWorkLongitude; // 출근 경도
    private String onWorkIPAddress; // 출근 IP 주소
    private String offWorkTime;      // 퇴근 시간
    private String offWorkLatitude;  // 퇴근 위도
    private String offWorkLongitude; // 퇴근 경도
    private String offWorkIPAddress; // 퇴근 IP 주소
}
