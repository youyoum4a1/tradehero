package com.tradehero.common.graphics;

import android.graphics.ColorMatrix;

public class ColorMatrixFactory
{
    public static ColorMatrix getIdentityMatrix ()
    {
        return  new ColorMatrix(
            new float[] {
                    1f, 0f, 0f, 0, 0,
                    0f, 1f, 0f, 0, 0,
                    0f, 0f, 1f, 0, 0,
                    0,  0,  0,  1, 0
            });
    }

    public static ColorMatrix getDefaultGrayScaleMatrix()
    {
        ColorMatrix matrix = getIdentityMatrix();
        matrix.setSaturation(0);
        return matrix;
    }
}
