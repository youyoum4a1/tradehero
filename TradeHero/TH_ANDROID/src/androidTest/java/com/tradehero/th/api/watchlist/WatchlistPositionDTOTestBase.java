package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.PositionDTOTestBase;
import com.tradehero.th.api.security.SecurityCompactDTOTestBase;

abstract public class WatchlistPositionDTOTestBase extends PositionDTOTestBase
{
    protected SecurityCompactDTOTestBase securityCompactDTOTest;

    public void setUp()
    {
        securityCompactDTOTest = new SecurityCompactDTOTestBase(){};
    }

    public boolean haveSameFields(WatchlistPositionDTO left, WatchlistPositionDTO right)
    {
        boolean have = super.haveSameFields(left, right);
        have &= left.watchlistPriceRefCcy == null ? right.watchlistPriceRefCcy == null : left.watchlistPriceRefCcy.equals(right.watchlistPriceRefCcy);
        if (left.securityDTO != null && right.securityDTO != null)
        {
            have &= securityCompactDTOTest.haveSameFields(left.securityDTO, right.securityDTO);
        }
        else
        {
            have &= (left.securityDTO == null && right.securityDTO == null);
        }
        return have;
    }
}
