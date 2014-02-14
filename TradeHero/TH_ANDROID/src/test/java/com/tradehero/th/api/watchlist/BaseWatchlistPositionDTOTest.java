package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.BasePositionDTOTest;
import com.tradehero.th.api.security.BaseSecurityCompactDTOTest;

/**
 * Created by xavier on 2/13/14.
 */
abstract public class BaseWatchlistPositionDTOTest extends BasePositionDTOTest
{
    public static final String TAG = BaseWatchlistPositionDTOTest.class.getSimpleName();

    protected BaseSecurityCompactDTOTest securityCompactDTOTest;

    public void setUp()
    {
        securityCompactDTOTest = new BaseSecurityCompactDTOTest(){};
    }

    public boolean haveSameFields(WatchlistPositionDTO left, WatchlistPositionDTO right)
    {
        boolean have = super.haveSameFields(left, right);
        have &= left.watchlistPrice == null ? right.watchlistPrice == null : left.watchlistPrice.equals(right.watchlistPrice);
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
