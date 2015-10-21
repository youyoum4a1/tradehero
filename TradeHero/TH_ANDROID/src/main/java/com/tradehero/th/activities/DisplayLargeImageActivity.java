package com.tradehero.th.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.widget.AdaptiveImageView;
import com.tradehero.th.widget.TradeHeroProgressBar;

/**
 * Created by palmer on 15/10/19.
 */
public class DisplayLargeImageActivity extends Activity implements ImageLoadingListener {

    private AdaptiveImageView displayView;
    private TradeHeroProgressBar loadingPB;

    private String imageUrl;

    public final static String KEY_LARGE_IMAGE_URL = "key_large_image_url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UniversalImageLoader.initImageLoader(this);

        Bundle bundle = getIntent().getExtras();
        imageUrl = bundle.getString(KEY_LARGE_IMAGE_URL, "");

        setContentView(R.layout.activity_display_large_img);
        displayView = (AdaptiveImageView)findViewById(R.id.imageview_display_img);
        loadingPB = (TradeHeroProgressBar)findViewById(R.id.loading);
        loadingPB.startLoading();
        downloadImage();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
        finish();
    }

    private void downloadImage(){
        ImageLoader.getInstance().displayImage(imageUrl, displayView, UniversalImageLoader.getDisplayLargeImageOptions(),this);
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
        dismissLoading();
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        dismissLoading();
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
        dismissLoading();
    }

    private void dismissLoading(){
        if(loadingPB!=null) {
            loadingPB.stopLoading();
            loadingPB.setVisibility(View.GONE);
        }
        if(displayView!=null){
            loadingPB.stopLoading();
            displayView.setVisibility(View.VISIBLE);
        }
    }
}

