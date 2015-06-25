package com.tradehero.th.fragments.live;

import android.os.Bundle;
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
import com.tradehero.th.fragments.base.BaseFragment;

public class LiveSignUpMainFragment extends BaseFragment
{
    @InjectView(R.id.android_tabs) SlidingTabLayout tabLayout;
    @InjectView(R.id.pager) ViewPager viewPager;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_tabbed, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        viewPager.setAdapter(new SignUpLivePagerAdapter(getChildFragmentManager()));

        tabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        tabLayout.setViewPager(viewPager);
    }

    private class SignUpLivePagerAdapter extends FragmentPagerAdapter
    {
        Fragment[] fragments = new Fragment[] {
                new LiveSignUpStep1Fragment(),
                new LiveSignUpStep2Fragment(),
                new LiveSignUpStep3Fragment(),
                new LiveSignUpStep4Fragment(),
                new LiveSignUpStep5Fragment()};

        public SignUpLivePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public int getCount()
        {
            return fragments.length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return String.valueOf(position + 1);
        }

        @Override public Fragment getItem(int position)
        {
            Fragment f = fragments[position];
            f.setArguments(getArguments());
            return f;
        }
    }
}
