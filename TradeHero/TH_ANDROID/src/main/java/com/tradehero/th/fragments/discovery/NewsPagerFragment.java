package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;
import com.viewpagerindicator.TitlePageIndicator;

public class NewsPagerFragment extends SherlockFragment
{
    @InjectView(R.id.news_pager) ViewPager mViewPager;
    @InjectView(R.id.news_indicator) TitlePageIndicator mPageIndicator;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_news_pager, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mViewPager.setAdapter(new DiscoveryNewsFragmentAdapter(((Fragment) this).getChildFragmentManager()));
        mPageIndicator.setViewPager(mViewPager);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    private class DiscoveryNewsFragmentAdapter extends FragmentPagerAdapter
    {
        public DiscoveryNewsFragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int i)
        {
            return FeaturedNewsHeadlineFragment.newInstance(NewsType.values()[i]);
        }

        @Override public int getCount()
        {
            return NewsType.values().length;
        }

        @Override public CharSequence getPageTitle(int position)
        {
            NewsType newsType = NewsType.values()[position];
            return getString(newsType.titleResourceId);
        }
    }
}
