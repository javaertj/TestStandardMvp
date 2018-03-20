package com.ykbjson.themvp.databinder;

import com.ykbjson.themvp.library.databinder.DataBinder;
import com.ykbjson.themvp.model.Article;
import com.ykbjson.themvp.view.ScrollingViewDelegate;

/**
 * 包名：com.ykbjson.themvp.databinder
 * 描述：测试DataBinder
 * 创建者：yankebin
 * 日期：2018/3/19
 */
public class ArticleDataBinder implements DataBinder<ScrollingViewDelegate,Article> {

    @Override
    public void notifyModelChange(ScrollingViewDelegate viewDelegate, Article model) {
        viewDelegate.showSnackBar(model.getTitle());
        viewDelegate.showText(model.getContent());
    }
}
