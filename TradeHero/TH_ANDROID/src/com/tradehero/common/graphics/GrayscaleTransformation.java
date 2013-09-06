package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/** Created with IntelliJ IDEA. User: xavier Date: 9/6/13 Time: 12:33 PM To change this template use File | Settings | File Templates. */
public class GrayscaleTransformation implements com.squareup.picasso.Transformation, com.fedorvlasov.lazylist.ImageLoader.Transformation
{
    @Override public Bitmap transform(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        //ColorMatrix cm = new ColorMatrix();
        ColorMatrix cm = new ColorMatrix(
                new float[]{
                        0.5f, 0.5f, 0.5f, 0, 0,
                        0.5f, 0.5f, 0.5f, 0, 0,
                        0.5f, 0.5f, 0.5f, 0, 0,
                        0,    0,    0,    1, 0, 0,
                        0,    0,    0,    0, 1, 0
                });
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    @Override public String key()
    {
        return "toGrayscale()";
    }
}
