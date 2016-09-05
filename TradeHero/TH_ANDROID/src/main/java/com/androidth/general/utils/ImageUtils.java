package com.androidth.general.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ImageUtils
{
    public static String getImageStoragePath(Context context){
        String dir = "/th";
        String path = getDefaultStoragePath(context) + dir;
        return path;
    }

    public static String getDefaultStoragePath(Context context){
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && Environment.getExternalStorageDirectory()
                .canWrite())
        {
            return Environment.getExternalStorageDirectory().getPath();
        }
        else
        {
            return context.getFilesDir().getPath();
        }
    }

    public static boolean setActionBarImage(ActionBar actionBar, Activity activity, String url){
        try {

            ImageView imageView = new ImageView(activity);
            Observable<Bitmap> observable = Observable.defer(()->{
                try {
                    return Observable.just(Picasso.with(activity).load(url).get());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
//                int height = (int)(actionBar.getHeight()*0.6);
//                int bitmapHt = bitmap.getHeight();
//                int bitmapWd = bitmap.getWidth();
//                int width = height * (bitmapWd / bitmapHt);
                Display display = activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

//                bitmap = Bitmap.createScaledBitmap(bitmap,  screenWidth*7/10, actionBar.getHeight()*4/10, true);
//                imageView.getLayoutParams().height = actionBar.getHeight()*4/10;
//                imageView.getLayoutParams().width = screenWidth*7/10;

//                android.view.ViewGroup.LayoutParams lp = imageView.getLayoutParams();
//                lp.width = size.x*7/10;
//                lp.height = actionBar.getHeight()*4/10;
//                imageView.setLayoutParams(lp);

                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.requestLayout();
                ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(size.x*7/10, actionBar.getHeight()*5/10, Gravity.CENTER);
                actionBar.setCustomView(imageView, layoutParams);
                actionBar.setElevation(5);
                actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
            }, throwable -> {
                Log.e("Error",""+throwable.getMessage());
            });

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
