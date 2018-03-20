
package com.ykbjson.themvp.library.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ykbjson.themvp.library.databinder.DataBinder;
import com.ykbjson.themvp.library.model.IModel;
import com.ykbjson.themvp.library.route.ModelBinderRouter;
import com.ykbjson.themvp.library.route.ViewBinderRouter;
import com.ykbjson.themvp.library.view.IDelegate;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 包名：com.ykbjson.themvp.library.presenter
 * 描述：Presenter层的实现基类,Presenter base class for Fragment.
 * <p>操作和model无关方法直接操作ViewDelegate？</p>
 * 创建者：yankebin
 * 日期：2015/11/19
 */
public abstract class FragmentPresenter<T extends IDelegate> extends Fragment {
    protected Map<String, DataBinder> binderMap;

    public T viewDelegate;

    public FragmentPresenter() {
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
        } catch (java.lang.InstantiationException e) {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        viewDelegate.create(inflater, container, savedInstanceState);
        return viewDelegate.getRootView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewDelegate.initWidget();
        bindEvenListener();
    }

    protected void bindEvenListener() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (viewDelegate.getOptionsMenuId() != 0) {
            inflater.inflate(viewDelegate.getOptionsMenuId(), menu);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (viewDelegate == null || null == binderMap) {
            initDataBinderAndViewDelegate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewDelegate = null;
    }

    public void notifyModelChange(IModel model) {
        DataBinder dataBinder = getDataBinder(model);
        if (null == dataBinder) {
            throw new RuntimeException("Can not find DataBinder,just check your Presenter's annotation");
        }
        dataBinder.notifyModelChange(viewDelegate, model);
    }
}
