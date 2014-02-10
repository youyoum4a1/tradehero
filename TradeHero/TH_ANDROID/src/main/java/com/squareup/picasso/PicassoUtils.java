package com.squareup.picasso;

import android.content.Context;

/**
 * Created by xavier on 2/10/14.
 */
public class PicassoUtils
{
    public static final String TAG = PicassoUtils.class.getSimpleName();

    public static int calculateMemoryCacheSize(Context context)
    {
        return Utils.calculateMemoryCacheSize(context);
    }
}
