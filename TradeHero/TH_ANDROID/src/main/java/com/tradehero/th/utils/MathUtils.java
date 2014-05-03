package com.tradehero.th.utils;


public class MathUtils
{
    /**
     * It is resistant to min and max swapping
     * if n between min and max, return n
     * if n greater than max, return max
     * if n less than min, return min
     * @param n
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float n, float min, float max)
    {
        boolean flag = max > min;
        if (!flag)
        {
            float tmp = min;
            min = max;
            max = tmp;
        }
        return Math.max(min, Math.min(max, n));
        //return Math.max(Math.min(min, max), Math.min(Math.max(min, max), n));
    }
}
