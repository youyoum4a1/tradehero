package com.tradehero.th.api.position;

/**
 * Created by xavier on 2/13/14.
 */
abstract public class BasePositionDTOCompactTest
{
    public static final String TAG = BasePositionDTOCompactTest.class.getSimpleName();

    public boolean haveSameFields(PositionDTOCompact left, PositionDTOCompact right)
    {
        boolean have = left.id == right.id;
        have &= left.shares == null ? right.shares == null : left.shares.equals(right.shares);
        have &= left.portfolioId == right.portfolioId;
        have &= left.averagePriceRefCcy == null ? right.averagePriceRefCcy == null : left.averagePriceRefCcy.equals(right.averagePriceRefCcy);
        return have;
    }
}
