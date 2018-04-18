package com.ykbjson.lib.mmvp.internal;

import com.ykbjson.lib.mmvp.MMVPPresenter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包名：com.ykbjson.lib.mmvp.internal
 * 描述：View绑定与Presenter的注解
 * 创建者：yankebin
 * 日期：2018/4/12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindPresenter {
    /**
     * View需要的Presenter集合
     */
    Class<? extends MMVPPresenter>[] value();
}
