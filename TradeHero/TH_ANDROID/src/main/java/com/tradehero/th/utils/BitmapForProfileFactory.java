package com.tradehero.th.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface BitmapForProfileFactory
{
    @Nullable Bitmap decodeBitmapForProfile(Resources resources, String selectedPath);
}
