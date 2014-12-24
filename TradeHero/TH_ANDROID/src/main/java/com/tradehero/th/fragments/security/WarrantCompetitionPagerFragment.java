package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.WarrantType;
import com.tradehero.th.fragments.base.DashboardFragment;

public class WarrantCompetitionPagerFragment extends DashboardFragment
{
    private static final String BUNDLE_PROVIDER_ID = WarrantCompetitionPagerFragment.class.getName()+".providerId";

    @InjectView(R.id.android_tabs) SlidingTabLayout slidingTabLayout;
    @InjectView(R.id.pager) ViewPager pager;
    private ProviderId providerId;

    public static void putProviderId(Bundle args, ProviderId providerId)
    {
        args.putBundle(BUNDLE_PROVIDER_ID, providerId.getArgs());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = new ProviderId(getArguments().getBundle(BUNDLE_PROVIDER_ID));
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_contest_center, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        pager.setAdapter(new WarrantPagerAdapter(getChildFragmentManager()));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_blue));
        slidingTabLayout.setCustomTabView(R.layout.th_tab_indicator, android.R.id.title);
        slidingTabLayout.setViewPager(pager);
    }

    protected class WarrantPagerAdapter extends FragmentPagerAdapter
    {
        private final int[] EXTRA_PAGE_TITLE;

        public WarrantPagerAdapter(FragmentManager fm)
        {
            super(fm);
            EXTRA_PAGE_TITLE = new int[]{R.string.warrants_all};
        }

        @Override public Fragment getItem(int position)
        {
            Fragment warrant = new ProviderWarrantListRxFragment();
            Bundle b = new Bundle(getArguments());
            ProviderWarrantListRxFragment.putProviderId(b, providerId);
            warrant.setArguments(b);
            return warrant;
        }

        @Override public int getCount()
        {
            return EXTRA_PAGE_TITLE.length + WarrantType.values().length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            if (position < EXTRA_PAGE_TITLE.length)
            {
                return getString(EXTRA_PAGE_TITLE[position]);
            }
            else
            {
                int adjusted = position - EXTRA_PAGE_TITLE.length;
                return getString(WarrantType.values()[adjusted].stringResId);
            }
        }
    }
}
