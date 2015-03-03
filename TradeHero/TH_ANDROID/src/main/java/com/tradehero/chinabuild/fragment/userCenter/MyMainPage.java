package com.tradehero.chinabuild.fragment.userCenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.fragment.MyFragmentPagerAdapter;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

import java.util.ArrayList;

/**
 * Created by palmer on 15/2/26.
 */
public class MyMainPage extends DashboardFragment implements View.OnClickListener {

    private TextView tvDiscuss;
    private TextView tvTradeHistory;
    private View viewDiscussLine;
    private View viewTradeHistoryLine;

    private int blueColor;
    private int blackColor;

    private ViewPager viewPager;

    private ArrayList<Fragment> fragmentList = new ArrayList();
    private MyMainSubPage discussFragment = new MyMainSubPage();
    private MyMainSubPage tradeHistoryFragment = new MyMainSubPage();

    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.user_my_main_page, container, false);
        tvDiscuss = (TextView) view.findViewById(R.id.textview_discuss_subtitle);
        tvTradeHistory = (TextView) view.findViewById(R.id.textview_tradehistory_subtitle);
        tvDiscuss.setOnClickListener(this);
        tvTradeHistory.setOnClickListener(this);
        viewDiscussLine = view.findViewById(R.id.view_discuss_line);
        viewTradeHistoryLine = view.findViewById(R.id.view_tradehistory_line);

        blueColor = getActivity().getResources().getColor(R.color.tradehero_blue);
        blackColor = getActivity().getResources().getColor(R.color.black2);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_my_history_page);

        initViewPager();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("我的动态");
    }

    private void initViewPager() {
        if (myFragmentPagerAdapter == null) {
            fragmentList.clear();
            Bundle discussBundle = new Bundle();
            discussBundle.putInt(MyMainSubPage.MY_MAIN_SUB_PAGE_TYPE, MyMainSubPage.TYPE_DISCUSS);
            discussFragment.setArguments(discussBundle);
            Bundle tradeHistoryBundle = new Bundle();
            tradeHistoryBundle.putInt(MyMainSubPage.MY_MAIN_SUB_PAGE_TYPE, MyMainSubPage.TYPE_TRADE_HISTORY);
            tradeHistoryFragment.setArguments(tradeHistoryBundle);
            fragmentList.add(discussFragment);
            fragmentList.add(tradeHistoryFragment);
            myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager(), fragmentList);
        }
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                refreshTabViews(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.textview_discuss_subtitle:
                viewPager.setCurrentItem(0);
                break;
            case R.id.textview_tradehistory_subtitle:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    private void refreshTabViews(int position) {
        if (position == 0) {
            tvDiscuss.setTextColor(blueColor);
            tvTradeHistory.setTextColor(blackColor);
            viewDiscussLine.setVisibility(View.VISIBLE);
            viewTradeHistoryLine.setVisibility(View.GONE);
        }
        if (position == 1) {
            tvDiscuss.setTextColor(blackColor);
            tvTradeHistory.setTextColor(blueColor);
            viewDiscussLine.setVisibility(View.GONE);
            viewTradeHistoryLine.setVisibility(View.VISIBLE);
        }
    }
}
