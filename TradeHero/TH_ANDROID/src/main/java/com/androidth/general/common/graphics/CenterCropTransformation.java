package com.androidth.general.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

public class CenterCropTransformation implements Transformation
{
    //<editor-fold desc="Constructors">
    public CenterCropTransformation()
    {
        super();
    }
    //</editor-fold>

    @Override public Bitmap transform(Bitmap source)
    {
        if (source == null)
        {
            return null;
        }

        Bitmap dstBmp;
        if (source.getWidth() > source.getHeight())
        {
            dstBmp = Bitmap.createBitmap(
                    source,
                    source.getWidth() / 2 - source.getHeight() / 2,
                    0,
                    source.getHeight(),
                    source.getHeight()
            );
            source.recycle();
        }
        else if (source.getWidth() < source.getHeight())
        {
            dstBmp = Bitmap.createBitmap(
                    source,
                    0,
                    source.getHeight() / 2 - source.getWidth() / 2,
                    source.getWidth(),
                    source.getWidth()
            );
            source.recycle();
        }
        else
        {
            dstBmp = source;
        }
        return dstBmp;
    }

    @Override public String key()
    {
        return "centerCrop";
    }
}
