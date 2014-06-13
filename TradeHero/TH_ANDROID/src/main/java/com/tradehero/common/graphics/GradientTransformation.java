package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import com.squareup.picasso.Transformation;

public class GradientTransformation implements Transformation
{
    private final int endColor;
    private final int startColor;

    public GradientTransformation()
    {
        this(Color.TRANSPARENT, Color.WHITE);
    }

    public GradientTransformation(int startColor, int endColor)
    {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    @Override public Bitmap transform(Bitmap bitmap)
    {
        LinearGradient gradient = new LinearGradient(0, 0, 0, bitmap.getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setShader(gradient);

        Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(targetBitmap);
        c.drawBitmap(bitmap, 0, 0, null);
        c.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);

        if (targetBitmap != bitmap)
        {
            bitmap.recycle();
        }
        return targetBitmap;
    }

    @Override public String key()
    {
        return String.format("gradient(%d, %d)", startColor, endColor);
    }
}
