
package com.ykbjson.themvp.library.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.ykbjson.themvp.library.databinder.DataBinder;
import com.ykbjson.themvp.library.model.IModel;
import com.ykbjson.themvp.library.route.ModelBinderRouter;
import com.ykbjson.themvp.library.route.ViewBinderRouter;
import com.ykbjson.themvp.library.view.IDelegate;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 包名：com.ykbjson.themvp.library.presenter
 * 描述：Presenter层的实现基类,Presenter base class for Activity.
 * <p>操作和model无关方法直接操作ViewDelegate？</p>
 * 创建者：yankebin
 * 日期：2015/11/19
 */
public abstract class ActivityPresenter<T extends IDelegate> extends AppCompatActivity {
    protected Map<String, DataBinder> binderMap ;
    /**
     * 视图代理
     */
    protected T viewDelegate;

    public ActivityPresenter() {
        initDataBinderAndViewDelegate();
    }

    /**
     * 初始化绑定代理
     */
    private void initDataBinderAndViewDelegate() {
        ViewBinderRouter router = getClass().getAnnotation(ViewBinderRouter.class);
        if (null == router) {
            throw new RuntimeException("ViewBinderRouter is invalid");
        }
        try {
            if (null == viewDelegate) {
                Class<? extends IDelegate> viewClazz = router.viewDelegate()[0];
                viewDelegate = (T) viewClazz.newInstance();
            }
            if (null == binderMap) {
                binderMap = new LinkedHashMap<>();
                for (Class<? extends DataBinder> clazz : router.dataBinder()) {
                    DataBinder dataBinder = clazz.newInstance();
                    binderMap.put(clazz.getSimpleName(), dataBinder);
                }
            }
        } catch (InstantiationException e) {
            throw new RuntimeException("create DataBinder failure", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("create DataBinder failure", e);
        }
    }

    protected DataBinder getDataBinder(IModel model) {
        ModelBinderRouter modelRouter = model.getClass().getAnnotation(ModelBinderRouter.class);
        if (null == modelRouter) {
            throw new RuntimeException("find ModelBinderRouter failure");
        }
        return binderMap.get(modelRouter.value().getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDelegate.create(getLayoutInflater(), null, savedInstanceState);
        setContentView(viewDelegate.getRootView());
        viewDelegate.initBaseView();
        viewDelegate.initWidget();
    }

    /**
     * 显示Actionbar菜单图标
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);// 显示
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * Actionbar点击返回键关闭事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewDelegate == null || null == binderMap) {
            initDataBinderAndViewDelegate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewDelegate.getOptionsMenuId() != 0) {
            getMenuInflater().inflate(viewDelegate.getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (handleSoftInputOnTouch() && null != viewDelegate.getRootView()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(viewDelegate.getRootView().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        viewDelegate = null;
        binderMap.clear();
        super.onDestroy();
    }

    protected boolean handleSoftInputOnTouch() {
        return true;
    }

    public void notifyModelChange(IModel model) {
        DataBinder dataBinder = getDataBinder(model);
        if (null == dataBinder) {
            throw new RuntimeException("Can not find DataBinder,just check your Presenter's annotation");
        }
        dataBinder.notifyModelChange(viewDelegate, model);
    }
}
