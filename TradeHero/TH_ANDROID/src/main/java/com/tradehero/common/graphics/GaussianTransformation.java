package com.tradehero.common.graphics;

import android.graphics.Bitmap;


public class GaussianTransformation implements com.squareup.picasso.Transformation
{
    @Override public Bitmap transform(Bitmap bitmap)
    {
        double[][] GaussianBlurConfig = new double[][] {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(GaussianBlurConfig);
        convMatrix.Factor = 27;
        convMatrix.Offset = 0;
        Bitmap returnBitmap = ConvolutionMatrix.computeConvolution3x3(bitmap, convMatrix);

        if (returnBitmap != bitmap)
        {
            bitmap.recycle();
        }
        return returnBitmap;
    }

    @Override public String key()
    {
        return "gaussian";
    }
}
