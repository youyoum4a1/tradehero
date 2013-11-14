package com.tradehero.common.billing.googleplay;

import java.util.Comparator;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public class SKUDetailsDecreasingPriceComparator<SKUDetailsType extends SKUDetails>
        implements Comparator<SKUDetailsType>
{
    public static final String TAG = SKUDetailsDecreasingPriceComparator.class.getSimpleName();

    @Override public int compare(SKUDetailsType skuDetailsType, SKUDetailsType skuDetailsType2)
    {
        if (skuDetailsType == null)
        {
            return skuDetailsType2 == null ? 0 : -1;
        }

        if (skuDetailsType2 == null)
        {
            return 1;
        }

        if (skuDetailsType.priceAmountMicros == null)
        {
            return skuDetailsType2.priceAmountMicros == null ? 0 : -1;
        }

        if (skuDetailsType2.priceAmountMicros == null)
        {
            return 1;
        }

        return skuDetailsType2.priceAmountMicros.compareTo(skuDetailsType.priceAmountMicros);
    }
}
