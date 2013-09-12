package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 12:33 PM To change this template use File | Settings | File Templates. */
public class GrayscaleTransformation implements com.squareup.picasso.Transformation, com.fedorvlasov.lazylist.ImageLoader.Transformation
{
    private static ColorMatrix getDefaultColorMatrix()
    {
        ColorMatrix cm = new ColorMatrix(
            new float[] {
                    1f, 1f, 1f, 0, 0,
                    1f, 1f, 1f, 0, 0,
                    1f, 1f, 1f, 0, 0,
                    0,    0,    0, 1, 0
            });
        cm.setSaturation(0);
        return cm;
    }

    private ColorMatrix colorMatrix;

    public GrayscaleTransformation()
    {
        super();
        this.colorMatrix = getDefaultColorMatrix();
    }

    public GrayscaleTransformation(ColorMatrix colorMatrix)
    {
        this.colorMatrix = colorMatrix;
    }

    @Override public Bitmap transform(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    @Override public String key()
    {
        return "toGrayscale";
    }
}
