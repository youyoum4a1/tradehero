package com.tradehero.common.graphics;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 11:44 AM To change this template use File | Settings | File Templates. */
public class FlipAlphaTransformation extends PixelBitWiseMaskTransformation
{
    public final static int FLIP_ALPHA_MASK = 0xFF000000;

    public FlipAlphaTransformation()
    {
        super(FLIP_ALPHA_MASK);
    }
}
