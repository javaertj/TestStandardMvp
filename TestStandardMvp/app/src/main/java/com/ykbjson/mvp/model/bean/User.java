package com.ykbjson.mvp.model.bean;

import java.io.Serializable;

/**
 * 包名：com.ykbjson.mvp.model.bean
 * 描述：用户模型
 * 创建者：yankebin
 * 日期：2018/3/16
 */

public class User implements Serializable {
    private String mobile;
    private String name;
    private String nickName;
    private long id;
    private int age;
    private int gender;
    private String token;


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
