package com.ykbjson.app.hvp.login.data.source;

import com.ykbjson.app.hvp.login.data.LoginResult;
import com.ykbjson.lib.mmvp.IMMVPOnDataCallback;

/**
 * 包名：com.ykbjson.app.hvp.login.data.source
 * 描述：
 * 创建者：yankebin
 * 日期：2018/4/13
 */
public class LoginRepository implements ILoginDataSource {
    @Override
    public void doLogin(String mobile, String password, final IMMVPOnDataCallback<LoginResult> callback) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(new LoginResult());
            }
        }, 2000);
    }
}
