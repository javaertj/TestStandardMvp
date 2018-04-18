package com.ykbjson.lib.mmvp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 包名：com.ykbjson.lib.mmvp
 * 描述：View和Presenter之间的通信携带
 * 创建者：yankebin
 * 日期：2018/4/12
 */
public class MMVPAction implements Serializable, Cloneable {
    private Class<?> sourceClass;
    private Class<?> targetClass;
    private MMVPActionDescription action;
    private Map<String, Object> params;
    private IMMVPOnDataCallback onDataCallback;

    MMVPAction(Class<?> sourceClass, Class<?> targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    public MMVPAction setSourceClass(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
        return this;
    }

    public MMVPAction setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        return this;
    }

    public MMVPAction setAction(MMVPActionDescription action) {
        this.action = action;
        return this;
    }

    public MMVPAction setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public MMVPAction setOnDataCallback(IMMVPOnDataCallback onDataCallback) {
        this.onDataCallback = onDataCallback;
        return this;
    }


    public Class<?> getSourceClass() {
        return sourceClass;
    }


    public Class<?> getTargetClass() {
        return targetClass;
    }


    public MMVPActionDescription getAction() {
        return action;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public IMMVPOnDataCallback getOnDataCallback() {
        return onDataCallback;
    }

    public void send() {
        send(0);
    }

    public void send(long delayMills) {
        MMVPArtist.sendAction(this, delayMills);
    }

    public MMVPAction transform() {
        Class<?> exchange = getTargetClass();
        setTargetClass(getSourceClass());
        setSourceClass(exchange);
        return this;
    }

    public MMVPAction clearParam() {
        if (null != params && !params.isEmpty()) {
            params.clear();
        }

        return this;
    }

    public MMVPAction putParam(String key, Object value) {
        if (null == params) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public <T> T getParam(String key) {
        if (null == params || params.isEmpty()) {
            return (T) null;
        }
        return (T) params.get(key);
    }

    void recycle() {
        sourceClass = null;
        targetClass = null;
        action = null;
        params = null;
        onDataCallback = null;
    }
}
