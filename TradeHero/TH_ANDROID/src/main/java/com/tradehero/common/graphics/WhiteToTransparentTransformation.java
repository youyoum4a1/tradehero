package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 12:38 PM To change this template use File | Settings | File Templates. */
public class WhiteToTransparentTransformation implements com.squareup.picasso.Transformation
{
    public static final int DEFAULT_TOLERANCE = 5;
    public final int tolerance;

    public WhiteToTransparentTransformation()
    {
        super();
        this.tolerance = DEFAULT_TOLERANCE;
    }

    public WhiteToTransparentTransformation(int tolerance)
    {
        this.tolerance = tolerance;
    }

    @Override public Bitmap transform(Bitmap source)
    {
        Bitmap processedBitmap = source.copy(Bitmap.Config.ARGB_8888, true);
        //Remove background white color
        int iWidth = processedBitmap.getWidth();
        int iHeight = processedBitmap.getHeight();
        int pixelSize = iWidth * iHeight;
        int[] imagePixels = new int[pixelSize];
        processedBitmap.getPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);
        int lowerBound = 255 - tolerance;
        int alpha, red, green, blue;
        for (int i = 0; i < pixelSize; i++)
        {
            alpha = Color.alpha(imagePixels[i]);
            red = Color.red(imagePixels[i]);
            green = Color.green(imagePixels[i]);
            blue = Color.blue(imagePixels[i]);

            if (((alpha >= lowerBound) && (alpha <= 255)) &&
                    ((red >= lowerBound) && (red <= 255)) &&
                    ((green >= lowerBound) && (green <= 255)) &&
                    ((blue >= lowerBound) && (blue <= 255)))
            {

                imagePixels[i] = Color.TRANSPARENT;
            }
        }
        processedBitmap.setPixels(imagePixels, 0, iWidth, 0, 0, iWidth, iHeight);

        if (processedBitmap != source)
        {
            source.recycle();
        }
        return source;
    }

    @Override public String key()
    {
        return "whiteToTransparent(" + tolerance + ")";
    }
}
