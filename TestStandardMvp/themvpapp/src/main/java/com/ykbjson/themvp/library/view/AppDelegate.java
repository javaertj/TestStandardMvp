
package com.ykbjson.themvp.library.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 包名：com.ykbjson.themvp.library.view
 * 描述：视图层代理的基类
 * 创建者：yankebin
 * 日期：2015/11/19
 */
public abstract class AppDelegate implements IDelegate {
    final SparseArray<View> mViews = new SparseArray<>();
    View rootView;
    AppCompatActivity activity;

    /**
     * 获取根视图布局id
     *
     * @return 根视图布局id
     */
    public abstract int getRootLayoutId();

    @Override
    public void create(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int rootLayoutId = getRootLayoutId();
        rootView = inflater.inflate(rootLayoutId, container, false);
        activity = getActivity();
    }

    @Override
    public int getOptionsMenuId() {
        return 0;
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public void initWidget() {
    }

    public <T extends View> T bindView(int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) rootView.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    /**
     * 获取子视图
     *
     * @param id  子视图id
     * @param <T> 子视图类型
     * @return id对应的子视图
     */
    public <T extends View> T get(int id) {
        return (T) bindView(id);
    }

    /**
     * 设置View.OnClickListener
     *
     * @param listener {@link View.OnClickListener}
     * @param ids      需要设置{@link View.OnClickListener}的子视图id
     */
    public void setOnClickListener(View.OnClickListener listener, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            get(id).setOnClickListener(listener);
        }
    }

    /**
     * 获取当前活动类
     *
     * @param <T> 活动类类型
     * @return 活动类
     */
    public <T extends AppCompatActivity> T getActivity() {
        return (T) rootView.getContext();
    }
}
