package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import com.squareup.picasso.Transformation;

public class RotateTransformation implements Transformation
{
    private final int rotationDegree;

    public RotateTransformation(int rotationDegree)
    {
        super();
        this.rotationDegree = rotationDegree;
    }

    @Override public Bitmap transform(Bitmap source)
    {
        if (rotationDegree == 0)
        {
            return source;
        }

        int width = source.getWidth();
        int height = source.getHeight();

        Matrix matrix = new Matrix();
        matrix.preRotate(rotationDegree);

        Bitmap dstBmp = Bitmap.createBitmap(source, 0, 0, width, height, matrix, false);
        source.recycle();
        return dstBmp;
    }

    @Override public String key()
    {
        return String.format("rotate-%d", rotationDegree);
    }
}
