package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

public class IdentityTransformation implements Transformation
{
    @Override public String key()
    {
        return "identity";
    }

    @Override public Bitmap transform(Bitmap source)
    {
        return source;
    }
}
