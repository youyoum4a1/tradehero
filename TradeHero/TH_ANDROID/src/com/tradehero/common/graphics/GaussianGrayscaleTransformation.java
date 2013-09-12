package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import com.tradehero.common.utils.THLog;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 4:55 PM To change this template use File | Settings | File Templates. */
public class GaussianGrayscaleTransformation implements com.squareup.picasso.Transformation, com.fedorvlasov.lazylist.ImageLoader.Transformation
{
    private static final String TAG = GaussianGrayscaleTransformation.class.getSimpleName();

    private GaussianTransformation gaussianTransformation;
    private GrayscaleTransformation grayscaleTransformation;

    public GaussianGrayscaleTransformation()
    {
        super();
        gaussianTransformation = new GaussianTransformation();
        grayscaleTransformation = new GrayscaleTransformation();
    }

    public GaussianGrayscaleTransformation(ColorMatrix grayMatrix)
    {
        super();
        gaussianTransformation = new GaussianTransformation();
        grayscaleTransformation = new GrayscaleTransformation(grayMatrix);
    }

    @Override public Bitmap transform(Bitmap bitmap)
    {
        Bitmap grey = grayscaleTransformation.transform(bitmap);
        bitmap.recycle();
        Bitmap gaussian = gaussianTransformation.transform(grey);
        grey.recycle();
        THLog.d(TAG, "Transformed");
        return gaussian;
    }

    @Override public String key()
    {
        return "toGaussianGrayscale";
    }
}
