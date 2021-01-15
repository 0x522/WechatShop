package com.wxshop.shop.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import com.wxshop.shop.WechatShopApplication;
import com.wxshop.shop.entity.LoginResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;


import static com.wxshop.shop.service.TelVerificationServiceTest.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechatShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
public class AuthIntegrationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    Environment environment;

    @Test
    public void loginLogoutTest() throws IOException {

        //最开始默认情况下访问 /api/status 处于未登录状态
        String statusResponse = HttpRequest
                .get(getUrl("/api/status"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        LoginResponse response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());

        //发送验证码
        int responseCode = HttpRequest
                .post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(TelVerificationServiceTest.VALID_PARAMETER))
                .code();
        Assertions.assertEquals(HttpStatus.OK.value(), responseCode);

        //带着验证码进行登录，得到cookie
        Map<String, List<String>> responseHeaders = HttpRequest
                .post(getUrl("/api/login"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(VALID_PARAMETER_CODE))
                .headers();
        List<String> setCookie = responseHeaders.get("Set-Cookie");
        Assertions.assertNotNull(setCookie);

        //带着cookie进行访问 /api/status 处于登录状态
        String jsessionidLine = setCookie.stream().filter(cookie -> cookie.contains("JSESSIONID")).findFirst().get();
        String sessionId = getSessionIdFromSetCookie(jsessionidLine);
        statusResponse = HttpRequest
                .get(getUrl("/api/status"))
                .header("Cookie", sessionId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertTrue(response.isLogin());
        Assertions.assertEquals(VALID_PARAMETER.getTel(), response.getUser().getTel());

        //访问 /api/logout
        //注销登录，注意注销登录也要带cookie，否则服务器不知道是哪个用户
        HttpRequest
                .post(getUrl("/api/logout"))
                .header("Cookie", sessionId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers();

        //再次带着cookie访问 /api/status 处于未登录状态
        statusResponse = HttpRequest
                .get(getUrl("/api/status"))
                .header("Cookie", sessionId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        response = objectMapper.readValue(statusResponse, LoginResponse.class);
        Assertions.assertFalse(response.isLogin());
    }

    private String getSessionIdFromSetCookie(String setCookie) {
//  JSESSIONID=fb16ca94-89bf-4df6-bdb8-6dc55678fbd6; Path=/; HttpOnly; SameSite=lax
//                  ↓
//  JSESSIONID=fb16ca94-89bf-4df6-bdb8-6dc55678fbd6;
        int semicolonIndex = setCookie.indexOf(";");
        return setCookie.substring(0, semicolonIndex);
    }


    @Test
    public void returnHttpOkWhenParameterIsCorrect() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(getUrl("/api/code"));

        String json = JSON.toJSONString(VALID_PARAMETER);
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusLine().getStatusCode());
        client.close();
    }

    @Test
    public void returnHttpBadRequestWhenParameterIsError() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(getUrl("/api/code"));

        String json = JSON.toJSONString(EMPTY_TEL);
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusLine().getStatusCode());
        client.close();
    }

    private String getUrl(String apiName) {
        // 获取集成测试的端口号
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }
}
