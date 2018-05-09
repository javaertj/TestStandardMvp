package com.ykbjson.app.hvp.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ykbjson.app.hvp.login.data.LoginResult;
import com.ykbjson.app.hvp.login.data.source.LoginRepository;
import com.ykbjson.lib.mmvp.IMMVPActionHandler;
import com.ykbjson.lib.mmvp.IMMVPOnDataCallback;
import com.ykbjson.lib.mmvp.MMVPAction;
import com.ykbjson.lib.mmvp.annotation.ActionProcess;
import com.ykbjson.lib.mmvp.annotation.MMVPActionProcessor;


/**
 * 包名：com.ykbjson.app.hvp.login
 * 描述：登录逻辑处理
 * 创建者：yankebin
 * 日期：2018/4/13
 */
@MMVPActionProcessor
public class LoginPresenter implements LoginContract.ILoginPresenter {
    public static final String ACTION_DO_LOGIN = "LoginPresenter.action.ACTION_DO_LOGIN";
    public static final String ACTION_NOTIFY_LOGIN_FAILED = "LoginPresenter.action.ACTION_NOTIFY_LOGIN_FAILED";
    public static final String ACTION_NOTIFY_LOGIN_SUCCESS = "LoginPresenter.action.ACTION_NOTIFY_LOGIN_SUCCESS";

    private LoginRepository repository = new LoginRepository();

    @ActionProcess(value = ACTION_DO_LOGIN, needActionParam = true, needTransformAction = true)
    @Override
    public void doLogin(final MMVPAction action) {
        //参数校验
        String mobile = action.getParam("mobile");
        String password = action.getParam("password");
        if (TextUtils.isEmpty(mobile)) {
            action.getAction().setAction(ACTION_NOTIFY_LOGIN_FAILED);
            action.clearParam().putParam("error", "登录账号无效").send();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            action.getAction().setAction(ACTION_NOTIFY_LOGIN_FAILED);
            action.clearParam().putParam("error", "登录密码无效").send();
            return;
        }

        repository.doLogin(mobile, password, new IMMVPOnDataCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult data) {
                action.getAction().setAction(ACTION_NOTIFY_LOGIN_SUCCESS);
                action.clearParam().putParam("loginResult", data).send();
            }

            @Override
            public void onError(String msg) {
                action.getAction().setAction(ACTION_NOTIFY_LOGIN_FAILED);
                action.clearParam().putParam("error", msg).send();
            }
        });
    }

    @Override
    public boolean handleAction(@NonNull MMVPAction action) {
        switch (action.getAction().getAction()) {
            case ACTION_DO_LOGIN:
                doLogin(action.transform());
                break;
        }

        return true;
    }

    @NonNull
    @Override
    public IMMVPActionHandler get() {
        return this;
    }
}
