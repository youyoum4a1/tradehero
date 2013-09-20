package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

/** Created with IntelliJ IDEA. User: xavier Date: 9/19/13 Time: 3:09 PM To change this template use File | Settings | File Templates. */
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
