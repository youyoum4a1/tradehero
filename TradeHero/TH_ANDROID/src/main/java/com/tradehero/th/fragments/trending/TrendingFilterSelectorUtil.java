package com.tradehero.th.fragments.trending;

import android.support.v4.view.PagerAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;

/**
 * Created by xavier on 12/11/13.
 */
public class TrendingFilterSelectorUtil
{
    public static final String TAG = TrendingFilterSelectorUtil.class.getSimpleName();
    public static final int FRAGMENT_COUNT = 4;

    public static int[] getPositions()
    {
        return new int[] {
                TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER,
                TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER,
                TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER,
                TrendingFilterSelectorAllFragment.POSITION_IN_PAGER
        };
    }

    public static TrendingFilterSelectorFragment createNewFragment(int position)
    {
        switch(position)
        {
            default:
                THLog.e(TAG, "Index not supported " + position, new Exception());
                return null;

            case TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER:
                return new TrendingFilterSelectorBasicFragment();

            case TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER:
                return new TrendingFilterSelectorVolumeFragment();

            case TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER:
                return new TrendingFilterSelectorPriceFragment();

            case TrendingFilterSelectorAllFragment.POSITION_IN_PAGER:
                return new TrendingFilterSelectorAllFragment();
        }
    }

    public static TrendingSecurityListType getSecurityListType(int filterPosition, String usableExchangeName, Integer page, Integer perPage)
    {
        switch (filterPosition)
        {
            case TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER:
                return TrendingFilterSelectorBasicFragment.sGetTrendingSecurityListType(usableExchangeName, page, perPage);

            case TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER:
                return TrendingFilterSelectorVolumeFragment.sGetTrendingSecurityListType(usableExchangeName, page, perPage);

            case TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER:
                return TrendingFilterSelectorPriceFragment.sGetTrendingSecurityListType(usableExchangeName, page, perPage);

            case TrendingFilterSelectorAllFragment.POSITION_IN_PAGER:
                return TrendingFilterSelectorAllFragment.sGetTrendingSecurityListType(usableExchangeName, page, perPage);

            default:
                THLog.d(TAG, "getSecurityListType: Unhandled filterPageSelector: " + filterPosition);
                return new TrendingBasicSecurityListType(usableExchangeName, page, perPage);
        }
    }

    public static int getFragmentPosition(TrendingFilterSelectorFragment fragment)
    {
        if (fragment instanceof TrendingFilterSelectorBasicFragment)
        {
            return TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER;
        }
        else if (fragment instanceof TrendingFilterSelectorVolumeFragment)
        {
            return TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER;
        }
        else if (fragment instanceof TrendingFilterSelectorPriceFragment)
        {
            return TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER;
        }
        else if (fragment instanceof TrendingFilterSelectorAllFragment)
        {
            return TrendingFilterSelectorAllFragment.POSITION_IN_PAGER;
        }
        return PagerAdapter.POSITION_NONE;
    }
}
