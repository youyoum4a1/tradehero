package com.tradehero.common.billing.googleplay;

import java.util.Comparator;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public class BaseIABProductDetailsIncreasingPriceComparator<BaseIABProductDetailsType extends BaseIABProductDetails>
        implements Comparator<BaseIABProductDetailsType>
{
    public static final String TAG = BaseIABProductDetailsIncreasingPriceComparator.class.getSimpleName();

    @Override public int compare(BaseIABProductDetailsType baseIABProductDetailsType, BaseIABProductDetailsType baseIABProductDetailsType2)
    {
        if (baseIABProductDetailsType == null)
        {
            return baseIABProductDetailsType2 == null ? 0 : -1;
        }

        if (baseIABProductDetailsType2 == null)
        {
            return 1;
        }

        if (baseIABProductDetailsType.priceAmountMicros == null)
        {
            return baseIABProductDetailsType2.priceAmountMicros == null ? 0 : -1;
        }

        if (baseIABProductDetailsType2.priceAmountMicros == null)
        {
            return 1;
        }

        return baseIABProductDetailsType.priceAmountMicros.compareTo(baseIABProductDetailsType2.priceAmountMicros);
    }
}
