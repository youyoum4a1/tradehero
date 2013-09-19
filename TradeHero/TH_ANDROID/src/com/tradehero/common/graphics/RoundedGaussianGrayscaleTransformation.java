package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import com.tradehero.common.utils.THLog;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 4:55 PM To change this template use File | Settings | File Templates. */
public class RoundedGaussianGrayscaleTransformation implements com.squareup.picasso.Transformation, com.fedorvlasov.lazylist.ImageLoader.Transformation
{
    private static final String TAG = RoundedGaussianGrayscaleTransformation.class.getSimpleName();

    private RoundedCornerTransformation roundedCornerTransformation;
    private GaussianTransformation gaussianTransformation;
    private GrayscaleTransformation grayscaleTransformation;

    public RoundedGaussianGrayscaleTransformation()
    {
        super();
        roundedCornerTransformation = new RoundedCornerTransformation();
        gaussianTransformation = new GaussianTransformation();
        grayscaleTransformation = new GrayscaleTransformation();
    }

    public RoundedGaussianGrayscaleTransformation(ColorMatrix grayMatrix)
    {
        super();
        roundedCornerTransformation = new RoundedCornerTransformation();
        gaussianTransformation = new GaussianTransformation();
        grayscaleTransformation = new GrayscaleTransformation(grayMatrix);
    }

    public RoundedGaussianGrayscaleTransformation(int pixelRadius, int borderColor)
    {
        super();
        roundedCornerTransformation = new RoundedCornerTransformation(pixelRadius, borderColor);
        gaussianTransformation = new GaussianTransformation();
        grayscaleTransformation = new GrayscaleTransformation();
    }

    public RoundedGaussianGrayscaleTransformation(ColorMatrix grayMatrix, int pixelRadius, int borderColor)
    {
        super();
        roundedCornerTransformation = new RoundedCornerTransformation(pixelRadius, borderColor);
        gaussianTransformation = new GaussianTransformation();
        grayscaleTransformation = new GrayscaleTransformation(grayMatrix);
    }

    @Override public Bitmap transform(Bitmap bitmap)
    {
        Bitmap grey = grayscaleTransformation.transform(bitmap);
        bitmap.recycle();
        Bitmap gaussian = gaussianTransformation.transform(grey);
        grey.recycle();
        Bitmap rounded = roundedCornerTransformation.transform(gaussian);
        gaussian.recycle();
        THLog.d(TAG, "Transformed");
        return rounded;
    }

    @Override public String key()
    {
        return "toRoundedGaussianGrayscale";
    }
}
