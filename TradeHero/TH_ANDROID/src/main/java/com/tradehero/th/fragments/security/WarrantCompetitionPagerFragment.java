package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private static final String BUNDLE_PROVIDER_ID = WarrantCompetitionPagerFragment.class.getName() + ".providerId";

    @InjectView(R.id.android_tabs) SlidingTabLayout slidingTabLayout;
    @InjectView(R.id.pager) ViewPager pager;
    @NonNull private ProviderId providerId;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull private static ProviderId getProviderId(@NonNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = getProviderId(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_tabbed, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        pager.setAdapter(new WarrantPagerAdapter(getChildFragmentManager()));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        slidingTabLayout.setCustomTabView(R.layout.th_tab_indicator, android.R.id.title);
        slidingTabLayout.setViewPager(pager);
    }

    protected class WarrantPagerAdapter extends FragmentPagerAdapter
    {
        //<editor-fold desc="Constructors">
        public WarrantPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        //</editor-fold>

        @Override public Fragment getItem(int position)
        {
            Fragment warrant = new ProviderWarrantListRxFragment();
            Bundle args = new Bundle(getArguments());
            ProviderWarrantListRxFragment.putProviderId(args, providerId);
            WarrantType type = WarrantTabType.values()[position].warrantType;
            if (type != null)
            {
                ProviderWarrantListRxFragment.putWarrantType(args, type);
            }
            warrant.setArguments(args);
            return warrant;
        }

        @Override public int getCount()
        {
            return WarrantTabType.values().length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(WarrantTabType.values()[position].title);
        }
    }
}