package com.ykbjson.themvp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ykbjson.themvp.databinder.ArticleDataBinder;
import com.ykbjson.themvp.databinder.ColorDataBinder;
import com.ykbjson.themvp.library.presenter.ActivityPresenter;
import com.ykbjson.themvp.library.route.ViewBinderRouter;
import com.ykbjson.themvp.model.Article;
import com.ykbjson.themvp.model.ColorModel;
import com.ykbjson.themvp.view.ScrollingViewDelegate;

@ViewBinderRouter(dataBinder = {ArticleDataBinder.class, ColorDataBinder.class},
        viewDelegate = ScrollingViewDelegate.class)
public class ScrollingActivity extends ActivityPresenter<ScrollingViewDelegate> {
    private Article article;
    private ColorModel colorModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView();
            }
        }, R.id.fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        article = new Article();
        article.setTitle(getString(R.string.app_name));
        article.setContent(getString(R.string.large_text));
        notifyModelChange(article);

        colorModel = new ColorModel();
        colorModel.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        notifyModelChange( colorModel);
    }

    public void showView() {
        article.setTitle(getString(R.string.action_settings));
        notifyModelChange(article);

        colorModel.setBgColor(getResources().getColor(R.color.colorAccent));
        notifyModelChange(colorModel);
    }
}
