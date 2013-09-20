package com.tradehero.common.cache;

import com.fedorvlasov.lazylist.FileCache;

/** Created with IntelliJ IDEA. User: xavier Date: 9/10/13 Time: 3:37 PM To change this template use File | Settings | File Templates. */
public class KnownCaches
{
    public static String ORIGINAL = "Original";
    public static String TRANSPARENT_BG = "WhiteToTransparentBg";
    public static String GREY_GAUSSIAN = "GreyGaussian";

    private static FileCache mOriginal;
    private static FileCache mTransparentBg;
    private static FileCache mGreyGaussian;

    public static FileCache getOriginal()
    {
        if (mOriginal == null)
        {
            mOriginal = FileCacheFactory.createFileCache(ORIGINAL);
        }
        return mOriginal;
    }

    public static FileCache getTransparentBg()
    {
        if (mTransparentBg == null)
        {
            mTransparentBg = FileCacheFactory.createFileCache(TRANSPARENT_BG);
        }
        return mTransparentBg;
    }

    public static FileCache getGreyGaussian()
    {
        if (mGreyGaussian == null)
        {
            mGreyGaussian = FileCacheFactory.createFileCache(GREY_GAUSSIAN);
        }
        return mGreyGaussian;
    }

    // TODO think about cleaning up
}
