package com.ykbjson.mvp.presenter.impl;

import android.support.annotation.NonNull;

import com.ykbjson.mvp.model.ILoginModel;
import com.ykbjson.mvp.model.bean.User;
import com.ykbjson.mvp.model.impl.LoginRepository;
import com.ykbjson.mvp.presenter.ILoginPresenter;
import com.ykbjson.mvp.view.ILoginView;

/**
 * 包名：com.ykbjson.mvp.presenter.impl
 * 描述：登录界面代理实现
 * 创建者：yankebin
 * 日期：2018/3/16
 */

public class LoginPresenterImpl implements ILoginPresenter {

    private ILoginView loginView;
    private ILoginModel loginModel;

    public LoginPresenterImpl(@NonNull ILoginView loginView) {
        this.loginView = loginView;
        loginModel = new LoginRepository();
    }

    @Override
    public void login(String email, String password) {
        loginView.showProgress(true);
        loginModel.doLogin(email, password, new ILoginModel.OnLoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                loginView.showProgress(false);
                loginView.onShowLoginSuccess(user.getName());
            }

            @Override
            public void onLoginFailed(String error) {
                loginView.showProgress(false);
                loginView.onShowLoginError(error);
            }
        });
    }
}
