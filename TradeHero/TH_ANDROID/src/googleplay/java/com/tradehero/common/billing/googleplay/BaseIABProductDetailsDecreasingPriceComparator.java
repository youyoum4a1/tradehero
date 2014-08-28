package com.tradehero.common.billing.googleplay;

import java.util.Comparator;

public class BaseIABProductDetailsDecreasingPriceComparator<
            BaseIABProductDetailType extends BaseIABProductDetail>
        implements Comparator<BaseIABProductDetailType>
{
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
