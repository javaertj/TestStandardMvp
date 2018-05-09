package com.ykbjson.app.hvp.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ykbjson.app.hvp.R;
import com.ykbjson.lib.mmvp.IMMVPActionHandler;
import com.ykbjson.lib.mmvp.MMVPAction;
import com.ykbjson.lib.mmvp.MMVPArtist;
import com.ykbjson.lib.mmvp.annotation.ActionProcess;
import com.ykbjson.lib.mmvp.annotation.MMVPActionProcessor;
import com.ykbjson.lib.mmvp.internal.BindPresenter;

/**
 * 包名：com.ykbjson.app.hvp.login
 * 描述：登录页面展示
 * 创建者：yankebin
 * 日期：2018/4/13
 */
@MMVPActionProcessor
@BindPresenter(LoginPresenter.class)
public class LoginActivity extends AppCompatActivity implements LoginContract.ILoginView {
    private ProgressDialog progressBar;
    private Button loginOrRegisterButton;
    private EditText etAccount;
    private EditText etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MMVPArtist.registerView(this);

        etAccount = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        loginOrRegisterButton = findViewById(R.id.email_sign_in_button);
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("登录中，请稍后...");

        loginOrRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MMVPArtist.unregisterView(this);
    }

    @ActionProcess(LoginPresenter.ACTION_NOTIFY_LOGIN_SUCCESS)
    @Override
    public void onLoginSuccess() {
        showLoading(false);
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
    }

    @ActionProcess(value = LoginPresenter.ACTION_NOTIFY_LOGIN_FAILED, needActionParams = true)
    @Override
    public void onLoginFailed(String error) {
        showLoading(false);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean showLoading) {
        if (showLoading && !progressBar.isShowing()) {
            progressBar.show();
        } else {
            progressBar.dismiss();
        }
    }

    @Override
    public boolean handleAction(@NonNull MMVPAction action) {
        switch (action.getAction().getAction()) {
            case LoginPresenter.ACTION_NOTIFY_LOGIN_SUCCESS:
                onLoginSuccess();
                break;
            case LoginPresenter.ACTION_NOTIFY_LOGIN_FAILED:
                onLoginFailed((String) action.getParam("error"));
                break;
        }

        return true;
    }

    @Override
    public String getScope() {
        return getClass().getCanonicalName() + "_" + getClass().hashCode();
    }

    @NonNull
    @Override
    public IMMVPActionHandler get() {
        return this;
    }

    private void login() {
        showLoading(true);
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        MMVPArtist.buildAction(getClass(), LoginPresenter.class, LoginPresenter.ACTION_DO_LOGIN)
                .putParam("mobile", account).putParam("password", password).send();
    }
}
