package com.tradehero.th.adapters.trending;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorBasicFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorPriceFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorVolumeFragment;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 10:27 AM To change this template use File | Settings | File Templates. */
public class TrendingFilterPagerAdapter extends FragmentStatePagerAdapter
{
    public static final String TAG = TrendingFilterPagerAdapter.class.getSimpleName();

    private final Context context;
    private TrendingFilterSelectorFragment.OnResumedListener onResumedListener;

    //<editor-fold desc="Constructors">
    public TrendingFilterPagerAdapter(Context context, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.context = context;
    }
    //</editor-fold>

    @Override public int getCount()
    {
        return 3;
    }

    @Override public Fragment getItem(int position)
    {
        TrendingFilterSelectorFragment fragment = null;
        switch(position)
        {
            default:
                THLog.i(TAG, "Not supported index " + position);
            case TrendingFilterSelectorBasicFragment.POSITION_IN_PAGER:
                fragment = new TrendingFilterSelectorBasicFragment();
                break;
            case TrendingFilterSelectorVolumeFragment.POSITION_IN_PAGER:
                fragment = new TrendingFilterSelectorVolumeFragment();
                break;
            case TrendingFilterSelectorPriceFragment.POSITION_IN_PAGER:
                fragment = new TrendingFilterSelectorPriceFragment();
                break;
        }

        fragment.setOnResumedListener(onResumedListener);

        // TODO listen to spinner

        fragment.setRetainInstance(false);
        return fragment;
    }

    @Override public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    public void setOnResumedListener(TrendingFilterSelectorFragment.OnResumedListener onResumedListener)
    {
        this.onResumedListener = onResumedListener;
    }
}
