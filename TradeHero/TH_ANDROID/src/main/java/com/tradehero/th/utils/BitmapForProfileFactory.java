package com.tradehero.th.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;

public interface BitmapForProfileFactory
{
    Bitmap decodeBitmapForProfile(Resources resources, String selectedPath);
}
