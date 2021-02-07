package com.wxshop.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.kevinsawicki.http.HttpRequest;
import com.wxshop.shop.WechatShopApplication;
import com.wxshop.shop.api.rpc.OrderService;
import com.wxshop.shop.entity.LoginResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.wxshop.shop.service.TelVerificationServiceTest.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class AuthIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    OrderService orderService;

    @Test
    public void loginLogoutTest() throws JsonProcessingException {
        String sessionId = loginAndGetCookie().cookie;

        // 带着Cookie访问 /api/v1/status 应该处于登录状态
        String statusResponse = doHttpRequest("/api/v1/status", "GET", null, sessionId).body;
        LoginResponse response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertTrue(response.isLogin());
        Assertions.assertEquals(VALID_PARAMETER.getTel(), response.getUser().getTel());


        // 调用/api/v1/logout
        // 注销登录，注意注销登录也需要带Cookie
        doHttpRequest("/api/v1/logout", "POST", null, sessionId);

        // 再次带着Cookie访问/api/v1/status 恢复成为未登录状态
        statusResponse = doHttpRequest("/api/v1/status", "GET", null, sessionId).body;

        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());
    }

//    @Test
//    public void returnForbiddenWhenCodeIsNotCorrect() throws Exception {
//        int responseCode = doHttpRequest("/api/v1/login",
//                "POST",
//                WRONG_CODE,
//                null).code;
//
//        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), responseCode);
//    }

    @Test
    public void returnHttpOKWhenParameterIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/v1/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(VALID_PARAMETER))
                .code();
        Assertions.assertEquals(HttpStatus.OK.value(), responseCode);
    }

    @Test
    public void returnHttpBadRequestWhenParameterIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/v1/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(TelVerificationServiceTest.EMPTY_TEL))
                .code();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), responseCode);
    }
}
