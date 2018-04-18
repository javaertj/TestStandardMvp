package com.ykbjson.lib.mmvp;

import java.io.Serializable;

/**
 * 包名：com.ykbjson.lib.mmvp
 * 描述：数据回调接口
 * 创建者：yankebin
 * 日期：2018/4/12
 */
public interface IMMVPOnDataCallback<T> extends Serializable{
    void onSuccess(T data);

    void onError(String msg);
}
