package com.ykbjson.themvp.model;

import com.ykbjson.themvp.databinder.ArticleDataBinder;
import com.ykbjson.themvp.library.model.IModel;
import com.ykbjson.themvp.library.route.ModelBinderRouter;

/**
 * 包名：com.ykbjson.themvp.model
 * 描述：测试数据
 * 创建者：yankebin
 * 日期：2018/3/20
 */
@ModelBinderRouter(ArticleDataBinder.class)
public class Article implements IModel {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
