
package com.ykbjson.themvp.library.databinder;


import com.ykbjson.themvp.library.model.IModel;
import com.ykbjson.themvp.library.view.IDelegate;

/**
 *
 * 包名：com.ykbjson.themvp.library.databinder
 * 描述：ViewModel实现
 * 创建者：yankebin
 * 日期：2015/11/19
 */
public interface DataBinder<T extends IDelegate,D extends IModel> {
    void notifyModelChange(T viewDelegate,D model);
}
