package com.team1.epilogue.authfix.controller;

import com.team1.epilogue.authfix.dto.RegisterRequest;
import com.team1.epilogue.authfix.dto.MemberResponse;
import com.team1.epilogue.authfix.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * MemberController는 사용자 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 이 클래스는 "/api/members" 경로로 들어오는 요청을 수신하고,
 * 사용자 등록과 관련된 비즈니스 로직을 MemberService에 위임
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    /**
     * [필드 레벨]
     * memberService: 사용자 등록 및 기타 사용자 관련 기능을 수행하는 서비스 클래스
     * 스프링 컨테이너에 의해 자동 주입
     */
    @Autowired
    private MemberService memberService;

    /**
     * [메서드 레벨]
     * registerMember API 엔드포인트는 클라이언트가 JSON 형식의 사용자 등록 요청(RegisterRequest)을 전송하면,
     * 이를 검증(@Validated)하고, MemberService를 호출하여 새로운 사용자를 등록한 후,
     * 결과로 MemberResponse 객체를 반환
     *
     * @param request 클라이언트가 전송한 사용자 등록 요청 데이터
     * @return 등록된 사용자의 정보를 담은 MemberResponse 객체
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public MemberResponse registerMember(@Validated @RequestBody RegisterRequest request) {
        return memberService.registerMember(request);
    }
}
