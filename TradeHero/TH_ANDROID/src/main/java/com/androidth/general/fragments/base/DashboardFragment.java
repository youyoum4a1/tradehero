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
        if(hexColor!=null){
            setActionBarColor(hexColor);
        }

        if(url != null && url.length() != 0){

            setActionBarCustomImage(getActivity(), url, false);
//            setActionBarImage(url);
//        } else {
//            setActionBarColor(getString(R.string.nav_bar_color_default));
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
