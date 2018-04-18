package com.ykbjson.app.hvp.login.data;

/**
 * 包名：com.ykbjson.app.hvp.login.data
 * 描述：
 * 创建者：yankebin
 * 日期：2018/4/13
 */
public class LoginResult {
    private String name;
    private String token;
    private String mobile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
