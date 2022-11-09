package com.ll.exam.mutBook.app.member.controller;

import com.ll.exam.mutBook.app.base.dto.RsData;
import com.ll.exam.mutBook.app.member.entity.Member;
import com.ll.exam.mutBook.app.member.service.MemberService;
import com.ll.exam.mutBook.util.Ut;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/member")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<RsData> login(@RequestBody LoginDto loginDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", "JWT키");

        Member member = memberService.findByUsername(loginDto.getUsername()).orElse(null);

        // 로그인 정보 유효성 검사
        if (loginDto.isNotValid()) {
            return Ut.spring.responseEntityOf(RsData.of("F-1", "로그인 정보가 올바르지 않습니다."));
        }

        // 회원 정보 유효성 검사
        if (member == null) {
            return Ut.spring.responseEntityOf(RsData.of("F-2", "일치하는 회원이 존재하지 않습니다."));
        }

        // 비밀번호 유효성 검사
        if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword()) == false) {
            return Ut.spring.responseEntityOf(RsData.of("F-3", "비밀번호가 일치하지 않습니다."));
        }

        headers.set("Authentication", "JWT_Access_Token");

        return Ut.spring.responseEntityOf(RsData.of("S-1", "로그인 성공, Access Token을 발급합니다."), headers);
    }

    @Data
    public static class LoginDto {
        private String username;
        private String password;

        public boolean isNotValid() {
            return username == null || password == null || username.trim().length() == 0 || password.trim().length() == 0;
        }
    }
}
