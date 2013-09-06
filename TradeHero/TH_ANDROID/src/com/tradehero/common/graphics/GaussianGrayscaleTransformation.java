package com.tradehero.common.graphics;

import android.graphics.Bitmap;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 4:55 PM To change this template use File | Settings | File Templates. */
public class GaussianGrayscaleTransformation implements com.squareup.picasso.Transformation, com.fedorvlasov.lazylist.ImageLoader.Transformation
{
    private GaussianTransformation gaussianTransformation = new GaussianTransformation();
    private GrayscaleTransformation grayscaleTransformation = new GrayscaleTransformation();

    @Override public Bitmap transform(Bitmap bitmap)
    {
        return gaussianTransformation.transform(grayscaleTransformation.transform(bitmap));
    }

    @Override public String key()
    {
        return "toGaussianGrayscale()";
    }
}
