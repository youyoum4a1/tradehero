package com.tradehero.th.utils;

/** Created with IntelliJ IDEA. User: xavier Date: 10/17/13 Time: 3:26 PM To change this template use File | Settings | File Templates. */
public class MathUtils
{
    /**
     * It is resistant to min and max swapping
     * @param n
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float n, float min, float max)
    {
        return Math.max(Math.min(min, max), Math.min(Math.max(min, max), n));
    }
}
