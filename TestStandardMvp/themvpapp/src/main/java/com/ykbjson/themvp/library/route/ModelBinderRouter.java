package com.ykbjson.themvp.library.route;

import com.ykbjson.themvp.library.databinder.DataBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包名：com.ykbjson.themvp.library.route
 * 描述：
 * 创建者：yankebin
 * 日期：2018/3/20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelBinderRouter {
    Class<? extends DataBinder> value();
}
