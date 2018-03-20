package com.ykbjson.themvp.model;

import com.ykbjson.themvp.databinder.ColorDataBinder;
import com.ykbjson.themvp.library.model.IModel;
import com.ykbjson.themvp.library.route.ModelBinderRouter;

/**
 * 包名：com.ykbjson.themvp.model
 * 描述：测试模型
 * 创建者：yankebin
 * 日期：2018/3/20
 */
@ModelBinderRouter(ColorDataBinder.class)
public class ColorModel implements IModel {
    private int bgColor;

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
