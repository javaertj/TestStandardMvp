
package com.ykbjson.themvp.library.view;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 包名：com.ykbjson.themvp.library.view
 * 描述：视图层代理的接口协议，View delegate base class
 * 创建者：yankebin
 * 日期：2015/11/19
 */

public interface IDelegate {
    /**
     * 创建视图
     *
     * @param inflater           inflater
     * @param container          视图容器
     * @param savedInstanceState 额外参数
     */
    void create(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 获取menu资源id
     *
     * @return menu资源id
     */
    int getOptionsMenuId();

    /**
     * 获取toolbar
     *
     * @return toolbar
     */
    Toolbar getToolbar();

    /**
     * 获取根视图
     *
     * @return 根视图
     */
    View getRootView();

    /**
     * 初始化控件
     */
    void initWidget();

    /**
     * 初始化基础视图
     */
    void initBaseView();

}
