package com.tradehero.chinabuild.fragment.stocklearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Stock Learning
 * <p/>
 * Created by palmer on 15/3/25.
 */
public class StockLearningMainFragment extends DashboardFragment {

    private ViewPager pager;
    private FragmentPagerAdapter adapter;

    private IntentFilter filter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(DashboardFragment.INTENT_TOOLBAR_PAGE_A_CLICK)){
                if(pager!=null){
                    pager.setCurrentItem(0);
                }
            } else if (action.equals(DashboardFragment.INTENT_TOOLBAR_PAGE_B_CLICK)){
                if(pager!=null) {
                    pager.setCurrentItem(1);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter = new IntentFilter();
        filter.addAction(DashboardFragment.INTENT_TOOLBAR_PAGE_A_CLICK);
        filter.addAction(DashboardFragment.INTENT_TOOLBAR_PAGE_B_CLICK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tab_fragment_learning_layout, container, false);
        pager = (ViewPager)view.findViewById(R.id.pager);
        initView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setPageView("入学宝典", "公开课");
    }

    @Override
    public void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private void initView() {
        adapter = new CustomAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    focusOnPageA();
                }
                if(position == 1){
                    focusOnPageB();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    class CustomAdapter extends FragmentPagerAdapter {
        public CustomAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new QuestionsFragment();
                case 1:
                    return new PublicClassFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
