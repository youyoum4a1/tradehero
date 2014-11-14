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
import com.tradehero.th.R;
import com.tradehero.th.inject.HierarchyInjector;

public class NewsPagerFragment extends Fragment
{
    @InjectView(R.id.news_carousel) ViewPager mNewsCarousel;

    @OnClick(R.id.previous_filter) void handlePreviousFilterClick()
    {
        int currentItem = mNewsCarousel.getCurrentItem();
        int size = mNewsCarousel.getAdapter().getCount();
        mNewsCarousel.setCurrentItem((currentItem + size - 1) % size, currentItem != 0);
    }

    @OnClick(R.id.next_filter) void handleNextFilterClick()
    {
        int currentItem = mNewsCarousel.getCurrentItem();
        int size = mNewsCarousel.getAdapter().getCount();
        mNewsCarousel.setCurrentItem((currentItem + 1) % size, currentItem + 1 != size);
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

        mNewsCarousel.setAdapter(new DiscoveryNewsCarouselFragmentAdapter(this.getChildFragmentManager()));
        mNewsCarousel.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override public void onPageSelected(int position)
            {
                replaceFragment(position);
            }
        });
        replaceFragment(0);
    }

    private void replaceFragment(int position)
    {
        NewsHeadlineFragment f = NewsHeadlineFragment.newInstance(NewsType.values()[position]);
        if(f != null)
        {
            getChildFragmentManager().beginTransaction().replace(R.id.news_pager, f).commit();
        }
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
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
