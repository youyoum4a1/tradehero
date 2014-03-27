package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABProductDetailComparator;
import com.tradehero.th.billing.ProductIdentifierDomain;

/**
 * Created by xavier on 2/10/14.
 */
public class THIABProductDetailComparator<THIABProductDetailType extends THIABProductDetail>
        extends BaseIABProductDetailComparator<THIABProductDetailType>
{
    public static final String TAG = THIABProductDetailComparator.class.getSimpleName();

    @Override public int compare(THIABProductDetailType lhs, THIABProductDetailType rhs)
    {
        int parentCompare = super.compare(lhs, rhs);
        if (parentCompare == 0)
        {
            return parentCompare;
        }
        ProductIdentifierDomain ldom = lhs.domain;
        ProductIdentifierDomain rdom = rhs.domain;

        if (ldom == null)
        {
            return rdom == null ? 0 : 1;
        }

        if (rdom == null)
        {
            return -1;
        }

        int domainCompare = rdom.compareTo(ldom);
        if (domainCompare != 0)
        {
            return domainCompare;
        }

        return - parentCompare;
    }
}
