package com.ykbjson.lib.mmvp;

import android.support.annotation.NonNull;

/**
 * 包名：com.ykbjson.lib.mmvp
 * 描述：{@link MMVPAction}处理接口
 * 创建者：yankebin
 * 日期：2018/4/13
 */
public interface IMMVPActionHandler {

    boolean handleAction(@NonNull MMVPAction action);

    @NonNull
    IMMVPActionHandler get();

    IMMVPActionHandler EMPTY = new IMMVPActionHandler() {
        @Override
        public boolean handleAction(@NonNull MMVPAction action) {
            return false;
        }

        @NonNull
        @Override
        public IMMVPActionHandler get() {
            return this;
        }
    };
}
