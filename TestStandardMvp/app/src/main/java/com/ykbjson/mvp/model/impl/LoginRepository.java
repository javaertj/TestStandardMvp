package com.ykbjson.mvp.model.impl;

import android.os.Handler;
import android.util.Log;

import com.ykbjson.mvp.model.ILoginModel;
import com.ykbjson.mvp.model.bean.User;

import java.util.Random;

/**
 * 包名：com.ykbjson.mvp.model.impl
 * 描述：登录数据处理实现
 * 创建者：yankebin
 * 日期：2018/3/16
 */

public class LoginRepository implements ILoginModel {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void doLogin(String email, String password, final OnLoginCallback onLoginCallback) {
        if (null == onLoginCallback) {
            Log.w(TAG, "Invalid OnLoginCallback，stop request");
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int status = random.nextInt(10);
                if (status % 2 == 0) {
                    User user = new User();
                    user.setName("运气好的人");
                    onLoginCallback.onLoginSuccess(user);
                } else {
                    onLoginCallback.onLoginFailed("运气不好的人登录失败！");
                }
            }
        }, 3000L);
    }
}
