package com.ykbjson.app.hvp.login;

import com.ykbjson.lib.mmvp.MMVPAction;
import com.ykbjson.lib.mmvp.MMVPPresenter;
import com.ykbjson.lib.mmvp.MMVPView;

/**
 * 包名：com.ykbjson.app.hvp.login
 * 描述：
 * 创建者：yankebin
 * 日期：2018/4/13
 */
public interface LoginContract {

    interface ILoginView extends MMVPView {
        void onLoginSuccess();

        void onLoginFailed(String error);

        void showLoading(boolean showLoading);
    }

    interface ILoginPresenter extends MMVPPresenter {

        void doLogin(MMVPAction action);
    }
}
