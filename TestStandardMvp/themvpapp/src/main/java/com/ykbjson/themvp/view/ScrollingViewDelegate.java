package com.ykbjson.themvp.view;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

import com.ykbjson.themvp.R;
import com.ykbjson.themvp.library.view.AppDelegate;

/**
 * 包名：com.ykbjson.themvp.view
 * 描述：主页面的视图代理
 * 创建者：yankebin
 * 日期：2018/3/19
 */

public class ScrollingViewDelegate extends AppDelegate {
    FloatingActionButton floatingActionButton;
    TextView tv;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_scrolling;
    }


    @Override
    public void initWidget() {
        super.initWidget();
        //replace with butterknife
        floatingActionButton = get(R.id.fab);
        tv=get(R.id.tv);
    }

    @Override
    public void initBaseView() {

    }

    public void setTextBgColor(int color){
        tv.setTextColor(color);
    }

    public void showSnackBar(String message) {
        Snackbar.make(floatingActionButton, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showText(String message) {
        tv.setText(message);
    }
}
