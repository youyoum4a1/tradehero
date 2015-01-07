package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.Constants;
import timber.log.Timber;

public class TrendingMainFragment extends DashboardFragment
{
    private static final String KEY_ASSET_CLASS = TrendingMainFragment.class.getName() + ".assetClass";

    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    public int lastType = 0;

    private TradingPagerAdapter tradingPagerAdapter;

    public static void putAssetClass(@NonNull Bundle args, @NonNull AssetClass assetClass)
    {
        args.putInt(KEY_ASSET_CLASS, assetClass.getValue());
    }

    @Nullable private static AssetClass getAssetClass(@NonNull Bundle args)
    {
        if (!args.containsKey(KEY_ASSET_CLASS))
        {
            return null;
        }
        return AssetClass.create(args.getInt(KEY_ASSET_CLASS));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tradingPagerAdapter = new TradingPagerAdapter(this.getChildFragmentManager());
        AssetClass askedAssetClass = getAssetClass(getArguments());
        if (askedAssetClass != null)
        {
            lastType = TrendingTabType.getForAssetClass(askedAssetClass).ordinal();
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.trending_main_fragment, container, false);
        ButterKnife.inject(this, view);
        initViews();
        return view;
    }

    private void initViews()
    {
        tabViewPager.setAdapter(tradingPagerAdapter);
        if (!Constants.RELEASE)
        {
            tabViewPager.setOffscreenPageLimit(0);
        }

        pagerSlidingTabStrip.setCustomTabView(R.layout.th_tab_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_blue));
        pagerSlidingTabStrip.setViewPager(tabViewPager);

        tabViewPager.setCurrentItem(lastType, true);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.trending_header);
    }

    @Override public void onDestroyView()
    {
        lastType = tabViewPager.getCurrentItem();
        tabViewPager.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.tradingPagerAdapter = null;
        super.onDestroy();
    }

    private class TradingPagerAdapter extends FragmentPagerAdapter
    {
        public TradingPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            TrendingTabType tabType = TrendingTabType.values()[position];
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            TrendingBaseFragment subFragment = (TrendingBaseFragment) Fragment.instantiate(getActivity(), tabType.fragmentClass.getName(), args);
            subFragment
                    .getRequestedTrendingTabTypeObservable()
                    .subscribe(
                            TrendingMainFragment.this::handleRequestedTabType,
                            error -> Timber.e(error, "")
                    );
            return subFragment;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(TrendingTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return TrendingTabType.values().length;
        }
    }

    public void handleRequestedTabType(@NonNull TrendingTabType tabType)
    {
        tabViewPager.setCurrentItem(tabType.ordinal(), true);
    }
}
