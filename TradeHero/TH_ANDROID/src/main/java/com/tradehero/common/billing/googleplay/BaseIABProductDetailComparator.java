package com.tradehero.common.billing.googleplay;

import java.util.Comparator;

/**
 * Created by xavier on 2/10/14.
 */
public class BaseIABProductDetailComparator<BaseIABProductDetailType extends BaseIABProductDetail> implements Comparator<BaseIABProductDetailType>
{
    public static final String TAG = BaseIABProductDetailComparator.class.getSimpleName();

    @Override public int compare(BaseIABProductDetailType lhs, BaseIABProductDetailType rhs)
    {
        if (lhs == null)
        {
            return rhs == null ? 0 : 1;
        }

        if (rhs == null)
        {
            return -1;
        }

        Long lpri = lhs.priceAmountMicros;
        Long rpri = rhs.priceAmountMicros;

        if (lpri == null)
        {
            return rpri == null ? compareById(lhs, rhs) : 1;
        }

        if (rpri == null)
        {
            return -1;
        }

        return -lpri.compareTo(rpri);
    }

    public int compareById(BaseIABProductDetailType lhs, BaseIABProductDetailType rhs)
    {
        if (lhs == null)
        {
            return rhs == null ? 0 : 1;
        }

        if (rhs == null)
        {
            return -1;
        }

        IABSKU lid = lhs.getProductIdentifier();
        IABSKU rid = rhs.getProductIdentifier();

        if (lid == null)
        {
            return rid == null ? 0 : 1;
        }

        if (rid == null)
        {
            return -1;
        }

        String lstr = lid.identifier;
        String rstr = rid.identifier;

        if (lstr == null)
        {
            return rstr == null ? 0 : 1;
        }

        if (rstr == null)
        {
            return -1;
        }

        return lstr.compareTo(rstr);
    }
}
