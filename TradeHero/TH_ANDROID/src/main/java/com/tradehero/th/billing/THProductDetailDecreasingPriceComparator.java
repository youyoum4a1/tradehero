package com.tradehero.th.billing;

import java.util.Comparator;

public class THProductDetailDecreasingPriceComparator<
            THProductDetailType extends THProductDetail>
        implements Comparator<THProductDetailType>
{
    @Override public int compare(
            THProductDetailType BaseIABProductDetail, THProductDetailType baseIABProductDetail2)
    {
        if (BaseIABProductDetail == null)
        {
            return baseIABProductDetail2 == null ? 0 : -1;
        }

        if (baseIABProductDetail2 == null)
        {
            return 1;
        }

        if (BaseIABProductDetail.getPrice() == null)
        {
            return baseIABProductDetail2.getPrice() == null ? 0 : -1;
        }

        if (baseIABProductDetail2.getPrice() == null)
        {
            return 1;
        }

        return baseIABProductDetail2.getPrice().compareTo(BaseIABProductDetail.getPrice());
    }
}
