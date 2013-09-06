package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import com.squareup.picasso.Transformation;

/** Created with IntelliJ IDEA. User: tho Date: 9/6/13 Time: 2:17 PM Copyright (c) TradeHero */

/**
 * WIP
 */
public class GradientTransformation implements Transformation
{
    @Override public Bitmap transform(Bitmap bitmap)
    {
        LinearGradient gradient = new LinearGradient(0, 0, 0, bitmap.getHeight(), Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setShader(gradient);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);

        Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas c = new Canvas(targetBitmap);
        c.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), paint);
        if (targetBitmap != bitmap)
        {
            bitmap.recycle();
        }
        return targetBitmap;
    }

    @Override public String key()
    {
        return "gradient()";
    }
}
