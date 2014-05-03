package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;


public class PixelBitWiseMaskTransformation implements Transformation
{
    // The default mask negates the colors and leaves alpha unchanged
    public static final int DEFAULT_MASK = 0x00FFFFFF;

    private int mask;

    //<editor-fold desc="Constructors">
    public PixelBitWiseMaskTransformation()
    {
        this(DEFAULT_MASK);
    }

    public PixelBitWiseMaskTransformation(int mask)
    {
        this.mask = mask;
    }
    //</editor-fold>

    @Override public String key()
    {
        return String.format("PixelBitWiseMaskTransformation:%s", Integer.toHexString(mask));
    }

    @Override public Bitmap transform(Bitmap original)
    {
        // Create mutable Bitmap to invert, argument true makes it mutable
        Bitmap inversion = original.copy(Bitmap.Config.ARGB_8888, true);

        // Get info about Bitmap
        int width = inversion.getWidth();
        int height = inversion.getHeight();
        int pixels = width * height;

        // Get original pixels
        int[] pixel = new int[pixels];
        inversion.getPixels(pixel, 0, width, 0, 0, width, height);

        // Modify pixels
        for (int i = 0; i < pixels; i++)
        {
            pixel[i] ^= mask;
        }
        inversion.setPixels(pixel, 0, width, 0, 0, width, height);

        if (inversion != original)
        {
            original.recycle();
        }

        // Return inverted Bitmap
        return inversion;
    }
}
