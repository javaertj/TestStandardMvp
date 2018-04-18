package com.ykbjson.app.hvp.login.data.source;

import com.ykbjson.app.hvp.login.data.LoginResult;
import com.ykbjson.lib.mmvp.IMMVPOnDataCallback;

/**
 * 包名：com.ykbjson.app.hvp.login.data.source
 * 描述：
 * 创建者：yankebin
 * 日期：2018/4/13
 */
public interface ILoginDataSource {
    void doLogin(String mobile, String password, IMMVPOnDataCallback<LoginResult> callback);
}
