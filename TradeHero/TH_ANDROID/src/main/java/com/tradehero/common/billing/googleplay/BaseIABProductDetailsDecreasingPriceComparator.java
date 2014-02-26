package com.tradehero.common.billing.googleplay;

import java.util.Comparator;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public class BaseIABProductDetailsDecreasingPriceComparator<
            BaseIABProductDetailType extends BaseIABProductDetail>
        implements Comparator<BaseIABProductDetailType>
{
    public static final String TAG = BaseIABProductDetailsDecreasingPriceComparator.class.getSimpleName();

    @Override public int compare(BaseIABProductDetailType BaseIABProductDetail, BaseIABProductDetailType baseIABProductDetail2)
    {
        if (BaseIABProductDetail == null)
        {
            return baseIABProductDetail2 == null ? 0 : -1;
        }

        if (baseIABProductDetail2 == null)
        {
            return 1;
        }

        if (BaseIABProductDetail.priceAmountMicros == null)
        {
            return baseIABProductDetail2.priceAmountMicros == null ? 0 : -1;
        }

        if (baseIABProductDetail2.priceAmountMicros == null)
        {
            return 1;
        }

        return baseIABProductDetail2.priceAmountMicros.compareTo(BaseIABProductDetail.priceAmountMicros);
    }
}
