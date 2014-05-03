package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.tradehero.common.graphics.RoundedShapeTransformation;


public class RoundedImageView extends ImageView
{
    //<editor-fold desc="Constructors">
    public RoundedImageView(Context context)
    {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void setImageDrawable(Drawable drawable)
    {
        if (drawable != null)
        {
            RoundedShapeTransformation transformation = new RoundedShapeTransformation();
            Bitmap bitmapDrawable = drawableToBitmap(drawable);
            drawable = new BitmapDrawable(getResources(), transformation.transform(bitmapDrawable.copy(Bitmap.Config.ARGB_8888, true)));
        }
        super.setImageDrawable(drawable);
    }

    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
