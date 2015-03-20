package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;

public final class NewsPagerFragment extends Fragment
{
    @InjectView(R.id.news_pager) ViewPager mViewPager;
    @InjectView(R.id.spinner_news) Spinner newsSpinner;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_news_pager, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mViewPager.setAdapter(new DiscoveryNewsFragmentAdapter(this.getChildFragmentManager()));
        newsSpinner.setAdapter(new NewsSpinnerAdapter(getActivity(),
                new NewsType[] { NewsType.SeekingAlpha, NewsType.MotleyFool, NewsType.Region, NewsType.Global}));
        newsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mViewPager.setCurrentItem(position);
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {
                //
            }
        });
    }

    class NewsSpinnerAdapter extends ArrayAdapter<NewsType> {
        public NewsSpinnerAdapter(Context context, NewsType[] objects)
        {
            super(context, 0, objects);
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            NewsType type = getItem(position);
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(type.titleViewResourceId, parent, false);
            }

            return convertView;
        }

        @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            NewsType type = getItem(position);
            convertView = getActivity().getLayoutInflater().inflate(type.titleViewResourceId, parent, false);
            View view = convertView.findViewById(R.id.spinner_arrow);
            if (view != null) {
                view.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override public int getItemViewType(int position)
        {
            NewsType type = getItem(position);
            return type.ordinal();
        }

        @Override public int getViewTypeCount()
        {
            return getCount();
        }
    }


    private class DiscoveryNewsFragmentAdapter extends FragmentPagerAdapter
    {
        public DiscoveryNewsFragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int i)
        {
            return NewsHeadlineFragment.newInstance(NewsType.values()[i]);
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
