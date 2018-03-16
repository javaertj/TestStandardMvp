package com.ykbjson.mvp.view;

import android.support.annotation.NonNull;

import com.ykbjson.mvp.presenter.ILoginPresenter;

/**
 * 包名：com.ykbjson.mvp.view
 * 描述：登录界面UI操作接口
 * 创建者：yankebin
 * 日期：2018/3/16
 */

public interface ILoginView {

    void showProgress(boolean showProgress);

    void onShowLoginError(String error);

    void onShowLoginSuccess(String userName);
}
