package com.tradehero.th.billing;

import java.util.Comparator;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public class THProductDetailDecreasingPriceComparator<
            THProductDetailType extends THProductDetail>
        implements Comparator<THProductDetailType>
{
    public static final String TAG = THProductDetailDecreasingPriceComparator.class.getSimpleName();

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
