package com.tradehero.chinabuild.mainTab;

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
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionCreateFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMineFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionsFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.viewpagerindicator.TabPageIndicator;
import javax.inject.Inject;

public class MainTabFragmentCompetition extends AbsBaseFragment
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    @Inject Analytics analytics;
    private FragmentPagerAdapter adapter;
    private String dialogContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_tab_fragment_competition_layout, container, false);
        ButterKnife.inject(this, view);
        InitView();
        return view;
    }

    @OnClick(R.id.imgSearch)
    public void CompetitionSearchClicked()
    {
        Bundle bundle =  new Bundle();
        bundle.putInt(SearchUnitFragment.BUNDLE_DEFAULT_TAB_PAGE, SearchUnitFragment.TAB_SEARCH_COMPETITION);
        gotoDashboard(SearchUnitFragment.class.getName(),bundle);
        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,AnalyticsConstants.BUTTON_COMPETITION_DETAIL_SEARCH));
    }

    private void InitView()
    {
        adapter = new CustomAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        indicator.setViewPager(pager);
    }

    public int getCurrentFragmentItem(){
        if(pager == null){
            return -1;
        }
        return pager.getCurrentItem();
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
                    return new CompetitionsFragment();

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
        //判断是否是游客，如果是游客不能创建比赛
        if (userProfileDTO != null && userProfileDTO.isVisitor)
        {
            dialogContent = getString(R.string.dialog_competition_suggest_login);
            showSuggestLoginDialogFragment(dialogContent);
        }
        else
        {
            gotoDashboard(CompetitionCreateFragment.class.getName(), new Bundle());
        }

        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,AnalyticsConstants.BUTTON_COMPETITION_DETAIL_CREATE));
    }
}
