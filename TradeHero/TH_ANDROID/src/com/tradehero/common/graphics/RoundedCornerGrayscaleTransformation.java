package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 6:39 PM To change this template use File | Settings | File Templates. */
public class RoundedCornerGrayscaleTransformation implements com.squareup.picasso.Transformation, com.fedorvlasov.lazylist.ImageLoader.Transformation
{
    private RoundedCornerTransformation roundedCornerTransformation;
    private GrayscaleTransformation grayscaleTransformation;

    public RoundedCornerGrayscaleTransformation()
    {
        super();
        roundedCornerTransformation = new RoundedCornerTransformation();
        grayscaleTransformation = new GrayscaleTransformation();
    }

    public RoundedCornerGrayscaleTransformation(int pixelRadius, int color, ColorMatrix grayMatrix)
    {
        super();
        roundedCornerTransformation = new RoundedCornerTransformation(pixelRadius, color);
        grayscaleTransformation = new GrayscaleTransformation(grayMatrix);
    }

    @Override public Bitmap transform(Bitmap bitmap)
    {
        return roundedCornerTransformation.transform(grayscaleTransformation.transform(bitmap));
    }

    @Override public String key()
    {
        return "toRoundedCornerGrayscale()";
    }
}
