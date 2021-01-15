package com.wxshop.shop.controller;

import com.wxshop.shop.entity.LoginResponse;
import com.wxshop.shop.service.AuthService;
import com.wxshop.shop.service.TelVerificationService;
import com.wxshop.shop.service.UserContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;
    private final TelVerificationService telVerificationService;

    @Autowired
    public AuthController(AuthService service,
                          TelVerificationService telVerificationService) {
        this.authService = service;
        this.telVerificationService = telVerificationService;
    }

    @PostMapping("/code")
    public void code(@RequestBody TelAndCode telAndCode,
                     HttpServletResponse response) {
        if (telVerificationService.verifyTelParameters(telAndCode)) {
            authService.sendVerificationCode(telAndCode.getTel());
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("/login")
    public void login(@RequestBody TelAndCode telAndCode) {
        UsernamePasswordToken token = new UsernamePasswordToken(
                telAndCode.getTel(),
                telAndCode.getCode());
        token.setRememberMe(true);
        SecurityUtils.getSubject().login(token);
    }

    @PostMapping("/logout")
    public void login() {
        SecurityUtils.getSubject().logout();
    }

    @GetMapping("/status")
    public LoginResponse loginStatus() {
        if (UserContext.getCurrentUser() == null) {
            return LoginResponse.notLogin();
        } else {
            return LoginResponse.login(UserContext.getCurrentUser());
        }
    }


    public static class TelAndCode {
        private String tel;
        private String code;

        public TelAndCode(String tel, String code) {
            this.tel = tel;
            this.code = code;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
