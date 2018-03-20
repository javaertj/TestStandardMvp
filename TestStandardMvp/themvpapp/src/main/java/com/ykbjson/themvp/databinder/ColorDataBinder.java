package com.ykbjson.themvp.databinder;

import com.ykbjson.themvp.library.databinder.DataBinder;
import com.ykbjson.themvp.model.ColorModel;
import com.ykbjson.themvp.view.ScrollingViewDelegate;

/**
 * 包名：com.ykbjson.themvp.databinder
 * 描述：测试DataBinder
 * 创建者：yankebin
 * 日期：2018/3/19
 */
public class ColorDataBinder implements DataBinder<ScrollingViewDelegate,ColorModel> {
    @Override
    public void notifyModelChange(ScrollingViewDelegate viewDelegate, ColorModel model) {
        viewDelegate.setTextBgColor(model.getBgColor());
    }
}
