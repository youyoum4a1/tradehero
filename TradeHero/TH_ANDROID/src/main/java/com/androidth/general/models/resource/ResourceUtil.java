package com.androidth.general.models.resource;

import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;

public class ResourceUtil
{
    public static float getFloat(@NonNull Resources resources, @DimenRes int resId)
    {
        TypedValue outValue = new TypedValue();
        resources.getValue(resId, outValue, true);
        return outValue.getFloat();
    }
}
