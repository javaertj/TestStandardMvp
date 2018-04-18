package com.ykbjson.lib.mmvp;

import java.io.Serializable;

/**
 * 包名：com.ykbjson.lib.mmvp
 * 描述：View和Presenter之间的通信携带描述
 * 创建者：yankebin
 * 日期：2018/4/13
 */
public class MMVPActionDescription implements Serializable {
    private String action;
    private String code;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
