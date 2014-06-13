package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

public class ScaleKeepRatioTransformation implements Transformation
{
    private final int width;
    private final int height;
    private final int maxHeight;
    private final int maxWidth;

    //<editor-fold desc="Constructors">
    public ScaleKeepRatioTransformation(int width, int height)
    {
        this(width, height, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * set one of @width or @height to zero so that bitmap will be resized to match the other value
     * @param width Target width, height will be resize to match width and original bitmap ratio
     * @param height Target height, width will be resize to match height and original bitmap ratio
     * @param maxWidth if resized width is larger than this value, height will be resize accordingly
     * @param maxHeight if resized height is larger than this value, width will be resize accordingly
     */
    public ScaleKeepRatioTransformation(int width, int height, int maxWidth, int maxHeight)
    {
        this.width = width;
        this.height = height;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }
    //</editor-fold>

    @Override public Bitmap transform(Bitmap source)
    {
        int scaledWidth = width;
        int scaledHeight = height;

        if (width != 0 && source.getWidth() != 0)
        {
            scaledHeight = source.getHeight() * width / source.getWidth();
            if (scaledHeight > maxHeight)
            {
                scaledHeight = maxHeight;
                scaledWidth = source.getWidth() * scaledHeight / source.getHeight();
            }
        }
        else if (height != 0 && source.getHeight() != 0)
        {
            scaledWidth = source.getWidth() * height / source.getHeight();

            if (scaledWidth > maxWidth)
            {
                scaledWidth = maxWidth;
                scaledHeight = source.getHeight() * scaledWidth / source.getWidth();
            }
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, false);

        if (scaledBitmap != source)
        {
            source.recycle();
        }

        return scaledBitmap;
    }

    @Override public String key()
    {
        return String.format("scaleKeepRatio_%d_%d_%d_%d", this.width, this.height, this.maxWidth, this.maxHeight);
    }
}
