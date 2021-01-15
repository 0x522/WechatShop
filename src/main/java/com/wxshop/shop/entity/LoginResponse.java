package com.wxshop.shop.entity;

import com.wxshop.shop.generate.User;

public class LoginResponse {

    private boolean login;

    public LoginResponse() {
    }

    private User user;

    private LoginResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    public static LoginResponse notLogin() {
        return new LoginResponse(false, null);
    }

    public static LoginResponse login(User user) {
        return new LoginResponse(true, user);
    }

    public User getUser() {
        return user;
    }

    public boolean isLogin() {
        return login;
    }
}
