package com.tradehero.th.fragments.chinabuild;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionAllFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionCreateFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionMineFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionSearchFragment;
import com.viewpagerindicator.TabPageIndicator;
import timber.log.Timber;

public class MainTabFragmentCompetition extends AbsBaseFragment
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    @InjectView(R.id.imgSearch) ImageView imgSearch;
    @InjectView(R.id.tvCreateCompetition) TextView tvCreateCompetition;//创建 浮标
    private FragmentPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_tab_fragment_competition_layout, container, false);
        ButterKnife.inject(this, view);
        InitView();
        return view;
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @OnClick(R.id.imgSearch)
    public void CompetitionSearchClicked()
    {
        Timber.d("CompetitionSearchClicked!");
        gotoDashboard(CompetitionSearchFragment.class.getName());
    }

    private void InitView()
    {
        adapter = new CustomAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        indicator.setViewPager(pager);
    }

    private static final String[] CONTENT = new String[] {"所有比赛", "我的比赛"};

    class CustomAdapter extends FragmentPagerAdapter
    {
        public CustomAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new CompetitionAllFragment();

                case 1:
                    return new CompetitionMineFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount()
        {
            return CONTENT.length;
        }
    }

    @OnClick(R.id.tvCreateCompetition)
    public void createCompetitionClicked()
    {
        gotoDashboard(CompetitionCreateFragment.class.getName());
    }
}
