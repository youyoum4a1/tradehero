package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import com.squareup.picasso.Transformation;

/** Created with IntelliJ IDEA. User: tho Date: 9/5/13 Time: 1:12 PM Copyright (c) TradeHero */
public class RoundedShapeTransformation implements Transformation
{
    @Override public Bitmap transform(Bitmap scaleBitmapImage)
    {
        int targetWidth = scaleBitmapImage.getWidth();
        int targetHeight = scaleBitmapImage.getHeight();

        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth) / 2,
                ((float) targetHeight) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CW);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        //paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);

        canvas.clipPath(path);


        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight()), new RectF(0, 0, targetWidth,
                targetHeight), paint);
        if (targetBitmap != sourceBitmap) {
            sourceBitmap.recycle();
        }
        return targetBitmap;
    }

    @Override public String key()
    {
        return "rounded()";
    }
}
