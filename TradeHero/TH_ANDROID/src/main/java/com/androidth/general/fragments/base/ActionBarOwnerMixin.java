package com.androidth.general.fragments.base;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidth.general.R;
import com.squareup.picasso.Picasso;

public class ActionBarOwnerMixin
{
    private static final String BUNDLE_KEY_TITLE = ActionBarOwnerMixin.class.getName() + ".title";
    private static final String BUNDLE_KEY_TOUCH_HOME = ActionBarOwnerMixin.class.getName() + ".touchHome";
    private static final String BUNDLE_KEY_SHOW_HOME = ActionBarOwnerMixin.class.getName() + ".showHome";
    private static final String BUNDLE_KEY_SHOW_HOME_AS_UP = ActionBarOwnerMixin.class.getName() + ".showHomeAsUp";
    private static final boolean DEFAULT_TOUCH_HOME = true;
    private static final boolean DEFAULT_SHOW_HOME = true;
    private static final boolean DEFAULT_SHOW_HOME_AS_UP = true;

    private final Fragment fragment;
    private final ActionBar actionBar;

    @NonNull public static ActionBarOwnerMixin of(@NonNull Fragment fragment)
    {
        return new ActionBarOwnerMixin(fragment);
    }

    //<editor-fold desc="Arguments Passing">
    public static void putKeyTouchHome(@NonNull Bundle args, boolean touchHome)
    {
        args.putBoolean(BUNDLE_KEY_TOUCH_HOME, touchHome);
    }

    protected static boolean getKeyTouchHome(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_TOUCH_HOME;
        }
        return args.getBoolean(BUNDLE_KEY_TOUCH_HOME, DEFAULT_TOUCH_HOME);
    }

    public static void putKeyShowHome(@NonNull Bundle args, boolean showHome)
    {
        args.putBoolean(BUNDLE_KEY_SHOW_HOME, showHome);
    }

    protected static boolean getKeyShowHome(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_SHOW_HOME;
        }
        return args.getBoolean(BUNDLE_KEY_SHOW_HOME, DEFAULT_SHOW_HOME);
    }

    public static void putKeyShowHomeAsUp(@NonNull Bundle args, boolean showAsUp)
    {
        args.putBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, showAsUp);
    }

    protected static boolean getKeyShowHomeAsUp(@Nullable Bundle args)
    {
        if (args == null)
        {
            return DEFAULT_SHOW_HOME_AS_UP;
        }
        return args.getBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, DEFAULT_SHOW_HOME_AS_UP);
    }

    public ActionBar getActionBar()
    {
        return actionBar;
    }

    public static void putActionBarTitle(Bundle args, String title)
    {
        if (args != null)
        {
            args.putString(BUNDLE_KEY_TITLE, title);
        }
    }
    //</editor-fold>

    private ActionBarOwnerMixin(@NonNull Fragment fragment)
    {
        this.fragment = fragment;
        this.actionBar = ((AppCompatActivity) fragment.getActivity()).getSupportActionBar();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Bundle argument = fragment.getArguments();
        if (argument != null && argument.containsKey(BUNDLE_KEY_TITLE))
        {
            String title = argument.getString(BUNDLE_KEY_TITLE);

            if (title != null && !title.isEmpty())
            {
                setActionBarTitle(title);
            }
        }

        if (actionBar != null && shouldTouchHome())
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_HOME);
            if (!shouldShowHome())
            {
                actionBar.setHomeAsUpIndicator(null);
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            else if (shouldShowHomeAsUp())
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
            }
            else
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.icn_actionbar_hamburger);
            }
        }
    }

    public void setActionBarTitle(@StringRes int titleResId)
    {
        if (actionBar != null)
        {
            actionBar.setTitle(titleResId);
        }
    }

    public void setActionBarColor(String hexColor)
    {
        if(actionBar != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor(hexColor));
            actionBar.setBackgroundDrawable(colorDrawable);
        }
    }

    public void setActionBarColor(int resourceId)
    {
        if(actionBar != null) {
            ColorDrawable colorDrawable = new ColorDrawable(resourceId);
            actionBar.setBackgroundDrawable(colorDrawable);

            actionBar.setBackgroundDrawable(
                    new ColorDrawable(resourceId));
        }
    }

    public boolean shouldTouchHome()
    {
        return getKeyTouchHome(fragment.getArguments());
    }

    public boolean shouldShowHome()
    {
        return getKeyShowHome(fragment.getArguments());
    }

    public boolean shouldShowHomeAsUp()
    {
        return getKeyShowHomeAsUp(fragment.getArguments());
    }

    public void setActionBarSubtitle(@StringRes int subTitleResId)
    {
        if (actionBar != null)
        {
            actionBar.setSubtitle(subTitleResId);
        }
    }

    public void onDestroy()
    {
        // nothing for now
    }

    public void setActionBarSubtitle(String subtitle)
    {
        if (actionBar != null)
        {
            actionBar.setSubtitle(subtitle);
        }
    }

    public void setActionBarTitle(String title)
    {
        if (actionBar != null)
        {
            actionBar.setTitle(title);
        }
    }

    public void setCustomView(View view)
    {
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(view);
        }
    }


    public void setActionBarCustomImage(ActionBar tempActionBar, Activity activity, String url, boolean hasOtherItems){

        if(url==null || activity==null){
            return;
        }
        try {
            ImageView imageView = new ImageView(activity);
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

//                bitmap = Bitmap.createScaledBitmap(bitmap,  screenWidth*7/10, actionBar.getHeight()*4/10, true);
//                imageView.getLayoutParams().height = actionBar.getHeight()*4/10;
//                imageView.getLayoutParams().width = screenWidth*7/10;

//            imageView.setImageBitmap(bitmap);



            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            ActionBar.LayoutParams layoutParams;

            if(hasOtherItems){
                layoutParams  = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tempActionBar.getHeight()*5/10, Gravity.CENTER);
            }else{
                layoutParams  = new ActionBar.LayoutParams(size.x*7/10, tempActionBar.getHeight()*5/10, Gravity.CENTER);
            }

            tempActionBar.setCustomView(imageView, layoutParams);
            tempActionBar.setElevation(5);
            tempActionBar.setDisplayOptions(tempActionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);

            tempActionBar.setTitle("");
            Picasso.with(activity).load(url).into(imageView);


//            Observable<Bitmap> observable = Observable.defer(()->{
//                try {
//                    return Observable.just(Picasso.with(activity).load(url).get());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return Observable.error(e);
//                }
//            });

//            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
////                int height = (int)(actionBar.getHeight()*0.6);
////                int bitmapHt = bitmap.getHeight();
////                int bitmapWd = bitmap.getWidth();
////                int width = height * (bitmapWd / bitmapHt);
//                Display display = activity.getWindowManager().getDefaultDisplay();
//                Point size = new Point();
//                display.getSize(size);
//
////                bitmap = Bitmap.createScaledBitmap(bitmap,  screenWidth*7/10, actionBar.getHeight()*4/10, true);
////                imageView.getLayoutParams().height = actionBar.getHeight()*4/10;
////                imageView.getLayoutParams().width = screenWidth*7/10;
//
//                imageView.setImageBitmap(bitmap);
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                Picasso.with(activity).load(url).into(imageView);
//
//                ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(size.x*7/10, actionBar.getHeight()*5/10, Gravity.CENTER);
//                actionBar.setCustomView(imageView, layoutParams);
//                actionBar.setElevation(5);
//                actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
//            }, throwable -> {
//                Log.e("Error",""+throwable.getMessage());
//            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
