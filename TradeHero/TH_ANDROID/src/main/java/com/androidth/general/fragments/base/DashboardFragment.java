package com.androidth.general.fragments.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.androidth.general.BuildConfig;
import com.androidth.general.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

abstract public class DashboardFragment extends BaseFragment
{
    @Inject protected Lazy<FragmentOuterElements> fragmentElements;
    //private static final String BUNDLE_KEY_URL = MainCompetitionFragment.class.getName() + ".url";
    //private static final String BUNDLE_KEY_COLOR = MainCompetitionFragment.class.getName() + ".color";
    //public static Bundle bundle;
    public boolean shouldShowLiveTradingToggle()
    {
        return BuildConfig.HAS_LIVE_ACCOUNT_FEATURE;
    }

    public void onLiveTradingChanged(boolean isLive)
    {
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (!actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    fragmentElements.get().onOptionItemsSelected(item);
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        /*if(this instanceof RedeemFragment){
            setActionBarColor("#FFFFFF");
        }*/
        //setActionBarColorSelf(getBundleKeyUrl(bundle), getBundleKeyColor(bundle));
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override public void onResume(){
        super.onResume();
    }

    public void setActionBarColorSelf(String url, String hexColor){

        if(url != null && url.length() != 0){
            setActionBarColor(hexColor);
            setActionBarImage(url);
            setActionBarTitle("");
        } else {
            setActionBarColor(getString(R.string.nav_bar_color_default));
        }
    }

    private boolean setActionBarImage(String url){
        try {
            ActionBar actionBar = getSupportActionBar();
            ImageView imageView = new ImageView(getContext());
            Observable<Bitmap> observable = Observable.defer(()->{
                try {
                    return Observable.just(Picasso.with(getContext()).load(url).get());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
                int height = (int)(actionBar.getHeight()*0.6);
                int bitmapHt = bitmap.getHeight();
                int bitmapWd = bitmap.getWidth();
                int width = height * (bitmapWd / bitmapHt);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                imageView.setImageBitmap(bitmap);
                ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                actionBar.setCustomView(imageView, layoutParams);
                actionBar.setElevation(5);
                actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setTitle("");
            }, throwable -> {
                Log.e("Error",""+throwable.getMessage());
            });

            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /*public static void putUrl(Bundle args, String url){
        args.putString(BUNDLE_KEY_URL, url);
    }
    public static void putActionBarColor(Bundle args, String url){
        args.putString(BUNDLE_KEY_COLOR, url);
    }
    public static String getBundleKeyUrl(Bundle args){
        if(args!=null)
            return args.getString(BUNDLE_KEY_URL);
        return null;
    }
    public static String getBundleKeyColor(Bundle args){
        if(args!=null)
            return args.getString(BUNDLE_KEY_COLOR);
        return null;
    }*/
}
