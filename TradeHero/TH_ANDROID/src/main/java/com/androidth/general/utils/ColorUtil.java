package com.androidth.general.utils;

import android.graphics.Color;

public class ColorUtil
{
    /**
     * Indicates whether the sampled color is within the cube centered on target color and with
     * a side length of 2 * halfSide
     * @param sampledColor the color tested
     * @param targetColor the center of the cube
     * @param halfSide the cube has a side length double this value
     * @return true if the sampled color falls within the cube
     */
    public static boolean isWithin(int sampledColor, int targetColor, int halfSide)
    {
        int sampleR = Color.red(sampledColor);
        int sampleG = Color.green(sampledColor);
        int sampleB = Color.blue(sampledColor);

        int targetR = Color.red(targetColor);
        int targetG = Color.green(targetColor);
        int targetB = Color.blue(targetColor);

        return Math.abs(sampleR - targetR) <= halfSide
                && Math.abs(sampleG - targetG) <= halfSide
                && Math.abs(sampleB - targetB) <= halfSide;
    }
}
