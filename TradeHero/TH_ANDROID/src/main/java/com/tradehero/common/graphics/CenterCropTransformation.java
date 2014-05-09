package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;
import javax.inject.Inject;

public class CenterCropTransformation implements Transformation
{
    @Inject public CenterCropTransformation()
    {
        super();
    }

    @Override public Bitmap transform(Bitmap source)
    {
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
