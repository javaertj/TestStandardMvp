//package com.ykbjson.lib.mmvp.internal;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
///**
// * 包名：com.ykbjson.lib.mmvp.internal
// * 描述：Presenter或View提供的可供反射调用的方法的注解
// * 创建者：yankebin
// * 日期：2018/4/12
// */
//@Target(ElementType.METHOD)
//@Retention(RetentionPolicy.RUNTIME)
//public @interface ActionProcess {
//    /**
//     * action
//     **/
//    String value() default "";
//
//    /**
//     * 是否需要{@link com.ykbjson.lib.mmvp.MMVPAction}参数
//     **/
//    boolean needActionParam() default false;
//
//    /**
//     * 是否需要转换{@link com.ykbjson.lib.mmvp.MMVPAction}，即交换sourceClass和targetClass
//     **/
//    boolean needTransformAction() default false;
//
//    /**
//     * 是否需要{@link com.ykbjson.lib.mmvp.MMVPAction}里面的param参数
//     **/
//    boolean needActionParams() default false;
//
//}
