package com.tradehero.chinabuild;

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
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionAllFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionCreateFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMineFragment;
import com.tradehero.chinabuild.fragment.search.SearchUniteFragment;
import com.tradehero.th.R;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.viewpagerindicator.TabPageIndicator;

import javax.inject.Inject;

public class MainTabFragmentCompetition extends AbsBaseFragment
{
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) TabPageIndicator indicator;
    @InjectView(R.id.imgSearch) ImageView imgSearch;
    @InjectView(R.id.tvCreateCompetition) TextView tvCreateCompetition;//创建 浮标
    private FragmentPagerAdapter adapter;

    private String dialogContent;

    @Inject Analytics analytics;
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

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnClick(R.id.imgSearch)
    public void CompetitionSearchClicked()
    {
        Bundle bundle =  new Bundle();
        bundle.putInt(SearchUniteFragment.BUNDLE_DEFAULT_TAB_PAGE,SearchUniteFragment.TAB_SEARCH_COMPETITION);
        gotoDashboard(SearchUniteFragment.class.getName(),bundle);
        analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,AnalyticsConstants.BUTTON_COMPETITION_DETAIL_SEARCH));
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
        //判断是否是游客，如果是游客不能创建比赛
        if (userProfileDTO != null && userProfileDTO.isVisitor)
        {
            dialogContent = getActivity().getResources().getString(R.string.dialog_competition_suggest_login);
            showSuggestLoginDialogFragment(dialogContent);
        }
        else
        {
            gotoDashboard(CompetitionCreateFragment.class.getName());
        }

        analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,AnalyticsConstants.BUTTON_COMPETITION_DETAIL_CREATE));
    }
}
