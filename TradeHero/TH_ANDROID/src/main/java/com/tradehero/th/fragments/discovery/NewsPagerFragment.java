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
import butterknife.OnClick;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;

public class NewsPagerFragment extends SherlockFragment
{
    @InjectView(R.id.news_pager) ViewPager mViewPager;
    @InjectView(R.id.news_carousel) ViewPager mNewsCarousel;
    @OnClick(R.id.previous_filter) void handlePreviousFilterClick()
    {
        int currentItem = mNewsCarousel.getCurrentItem();
        int size = mNewsCarousel.getAdapter().getCount();
        mNewsCarousel.setCurrentItem((currentItem + size - 1) % size);
    }

    @OnClick(R.id.next_filter) void handleNextFilterClick()
    {
        int currentItem = mNewsCarousel.getCurrentItem();
        int size = mNewsCarousel.getAdapter().getCount();
        mNewsCarousel.setCurrentItem((currentItem + 1) % size);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_news_pager, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        int totalPage = NewsType.values().length;
        mViewPager.setAdapter(new DiscoveryNewsFragmentAdapter(((Fragment) this).getChildFragmentManager()));
        mNewsCarousel.setAdapter(new DiscoveryNewsCarouselFragmentAdapter(((Fragment) this).getChildFragmentManager()));

        // all pages need to be cached, coz they are both circle pagers
        mViewPager.setOffscreenPageLimit(totalPage);
        mNewsCarousel.setOffscreenPageLimit(totalPage);

        mNewsCarousel.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override public void onPageSelected(int position)
            {
                mViewPager.setCurrentItem(position);
            }
        });
        //mPageIndicator.setViewPager(mViewPager);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    private class DiscoveryNewsFragmentAdapter extends DiscoveryNewsAdapter
    {
        public DiscoveryNewsFragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int i)
        {
            return NewsHeadlineFragment.newInstance(NewsType.values()[i]);
        }
    }

    private class DiscoveryNewsCarouselFragmentAdapter extends DiscoveryNewsAdapter
    {
        public DiscoveryNewsCarouselFragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int i)
        {
            return NewsCarouselFragment.newInstance(NewsType.values()[i]);
        }
    }

    private abstract class DiscoveryNewsAdapter extends FragmentPagerAdapter
    {
        public DiscoveryNewsAdapter(FragmentManager fm)
        {
            super(fm);
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
