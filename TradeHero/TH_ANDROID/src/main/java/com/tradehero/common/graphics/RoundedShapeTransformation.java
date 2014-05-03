package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;


public class RoundedShapeTransformation implements com.squareup.picasso.Transformation
{
    @Override public Bitmap transform(Bitmap scaleBitmapImage)
    {
        int targetWidth = Math.min(scaleBitmapImage.getWidth(), scaleBitmapImage.getHeight());
        int targetHeight = targetWidth;

        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

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


        canvas.drawBitmap(scaleBitmapImage, new Rect(0, 0, scaleBitmapImage.getWidth(),
                scaleBitmapImage.getHeight()), new RectF(0, 0, targetWidth,
                targetHeight), paint);

        if (targetBitmap != scaleBitmapImage)
        {
            scaleBitmapImage.recycle();
        }
        return targetBitmap;
    }

    @Override public String key()
    {
        return "rounded";
    }
}
