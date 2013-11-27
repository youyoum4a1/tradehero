package com.tradehero.common.billing.googleplay;

import java.util.Comparator;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public class BaseIABProductDetailsDecreasingPriceComparator<BaseIABProductDetailsType extends BaseIABProductDetails>
        implements Comparator<BaseIABProductDetailsType>
{
    public static final String TAG = BaseIABProductDetailsDecreasingPriceComparator.class.getSimpleName();

    @Override public int compare(BaseIABProductDetailsType BaseIABProductDetailsType, BaseIABProductDetailsType BaseIABProductDetailsType2)
    {
        if (BaseIABProductDetailsType == null)
        {
            return BaseIABProductDetailsType2 == null ? 0 : -1;
        }

        if (BaseIABProductDetailsType2 == null)
        {
            return 1;
        }

        if (BaseIABProductDetailsType.priceAmountMicros == null)
        {
            return BaseIABProductDetailsType2.priceAmountMicros == null ? 0 : -1;
        }

        if (BaseIABProductDetailsType2.priceAmountMicros == null)
        {
            return 1;
        }

        return BaseIABProductDetailsType2.priceAmountMicros.compareTo(BaseIABProductDetailsType.priceAmountMicros);
    }
}
