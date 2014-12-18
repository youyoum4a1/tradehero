package com.tradehero.th.models.resource;

import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import javax.inject.Inject;

public class ResourceUtil
{
    //<editor-fold desc="Constructors">
    @Inject public ResourceUtil()
    {
    }
    //</editor-fold>

    public float getFloat(@NonNull Resources resources, @DimenRes int resId)
    {
        TypedValue outValue = new TypedValue();
        resources.getValue(resId, outValue, true);
        return outValue.getFloat();
    }
}
