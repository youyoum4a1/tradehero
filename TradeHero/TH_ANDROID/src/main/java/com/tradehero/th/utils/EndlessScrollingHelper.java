package com.tradehero.th.utils;


public class EndlessScrollingHelper
{
    private static final int DEFAULT_MULTIPLIER = 2;

    /**
     * Define the distance between current position on the list and the item at the end of the list
     * which consider to be "near", at this point, the list will be refresh with new item coming from
     * bottom
     * @param totalItemCount
     * @param visibleItemCount
     * @return
     */
    public static int calculateThreshold(int totalItemCount, int visibleItemCount)
    {
        int multiplier = DEFAULT_MULTIPLIER;
        if (visibleItemCount > 0)
        {
            int segmentCount = totalItemCount / visibleItemCount;
            multiplier = 1 + (32 - Integer.numberOfLeadingZeros(segmentCount));
        }

        return visibleItemCount * multiplier;
    }
}
