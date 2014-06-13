package com.tradehero.common.graphics;

public class FlipAlphaTransformation extends PixelBitWiseMaskTransformation
{
    public final static int FLIP_ALPHA_MASK = 0xFF000000;

    public FlipAlphaTransformation()
    {
        super(FLIP_ALPHA_MASK);
    }
}
