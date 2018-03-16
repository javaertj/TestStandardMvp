package com.ykbjson.mvp.model;

import com.ykbjson.mvp.model.bean.User;

/**
 * 包名：com.ykbjson.mvp.model
 * 描述：登录数据处理接口
 * 创建者：yankebin
 * 日期：2018/3/16
 */

public interface ILoginModel {
    interface OnLoginCallback{
        void onLoginSuccess(User user);

        void onLoginFailed(String error);
    }
    void doLogin(String email,String password,OnLoginCallback onLoginCallback);
}
