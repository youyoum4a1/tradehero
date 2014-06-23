package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class RoundedCornerTransformation  implements com.squareup.picasso.Transformation
{
    public static final int DEFAULT_PIXEL_RADIUS = 10;
    public static final int DEFAULT_COLOR = 0xff424242;
    private final int pixelRadius;
    /**
     * The color with which the SRC_IN will be performed. For full content, use alpha = 1.
     */
    private final int color;

    //<editor-fold desc="Constructors">
    public RoundedCornerTransformation()
    {
        super();
        this.pixelRadius = DEFAULT_PIXEL_RADIUS;
        this.color = DEFAULT_COLOR;
    }

    public RoundedCornerTransformation(int pixelRadius, int color)
    {
        super();
        this.pixelRadius = pixelRadius;
        this.color = color;
    }
    //</editor-fold>

    @Override public Bitmap transform(Bitmap bitmap)
    {
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // The future round rectangle description
        RectF rectF = new RectF(rect);
        float roundPx = pixelRadius;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //paint.setColor(0xff000000);
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (bitmap != result)
        {
            bitmap.recycle();
        }
        return result;
    }

    @Override public String key()
    {
        return String.format("toRoundedCorner(%d, %d)", pixelRadius, color);
    }
}
