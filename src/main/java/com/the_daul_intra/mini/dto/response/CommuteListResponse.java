package com.the_daul_intra.mini.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommuteListResponse {
    private Long id;                    // 출퇴근 기록 인덱스
    private Long rowNum;                // 출퇴근 기록 번호
    private String employeeName;        // 사원 이름
    private String onWorkTime;   // 츌근 시간
    private String onWorkLatitude;      // 출근 위도
    private String onWorkLongitude;     // 출근 경도
    private String onWorkIPAddress;     // 출근 IP 주소
    private String onWorkStatus;        // 출근 여부 Y/N
    private String offWorkTime;  // 퇴근 시간
    private String offWorkLatitude;     // 퇴근 위도
    private String offWorkLongitude;    // 퇴근 경도
    private String offWorkIPAddress;    // 퇴근 IP 주소
    private String offWorkStatus;       // 퇴근 여부 Y/N
}
