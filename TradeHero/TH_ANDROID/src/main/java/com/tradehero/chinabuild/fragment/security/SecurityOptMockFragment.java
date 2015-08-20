package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import java.util.ArrayList;

public class SecurityOptMockFragment extends Fragment implements View.OnClickListener {

    private SubFragmentPagerAdapter subFragmentPagerAdapter;
    private ArrayList<Fragment> subFragments = new ArrayList();
    private FragmentManager fragmentManager;
    private SecurityDetailSubViewPager viewPager;

    private int index = 0;

    private Button buyBtn;
    private Button sellBtn;
    private Button recallBtn;
    private Button queryBtn;

    private View buyFocus;
    private View sellFocus;
    private View recallFocus;
    private View queryFocus;

    private int blue_color;
    private int black_color;

    private String type = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle();
        blue_color = getResources().getColor(R.color.color_blue);
        black_color = getResources().getColor(R.color.black);

        fragmentManager = getChildFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_opt_mock, container, false);
        initViews(view);
        initSubViewPager();
        return view;
    }

    private void initViews(View view) {
        buyBtn = (Button) view.findViewById(R.id.btnTabBuy);
        sellBtn = (Button) view.findViewById(R.id.btnTabSell);
        recallBtn = (Button) view.findViewById(R.id.btnRecall);
        queryBtn = (Button) view.findViewById(R.id.btnQuery);

        buyFocus = view.findViewById(R.id.view_buy_focus);
        sellFocus = view.findViewById(R.id.view_sell_focus);
        recallFocus = view.findViewById(R.id.view_recall_focus);
        queryFocus = view.findViewById(R.id.view_query_focus);

        buyBtn.setOnClickListener(this);
        sellBtn.setOnClickListener(this);
        recallBtn.setOnClickListener(this);
        queryBtn.setOnClickListener(this);

        viewPager = (SecurityDetailSubViewPager) view.findViewById(R.id.securityoptmocksubviewpager);
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        switch (viewId) {
            case R.id.btnTabBuy:
                if (index == 0) {
                    return;
                }
                index = 0;
                initSubTabs();
                viewPager.setCurrentItem(0);
                break;
            case R.id.btnTabSell:
                if (index == 1) {
                    return;
                }
                index = 1;
                initSubTabs();
                viewPager.setCurrentItem(1);
                break;
            case R.id.btnRecall:
                if (index == 2) {
                    return;
                }
                index = 2;
                initSubTabs();
                viewPager.setCurrentItem(2);
                break;
            case R.id.btnQuery:
                if (index == 3) {
                    return;
                }
                index = 3;
                initSubTabs();
                viewPager.setCurrentItem(3);
                break;
        }
    }

    private void initSubViewPager() {
        initSubFragments();
        if (subFragmentPagerAdapter == null) {
            subFragmentPagerAdapter = new SubFragmentPagerAdapter(fragmentManager);
        }
        viewPager.setAdapter(subFragmentPagerAdapter);
        viewPager.setCurrentItem(index);
        initSubTabs();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                initSubTabs();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initSubFragments() {
        if (subFragments.size() <= 0) {
            subFragments.clear();
            SecurityOptMockSubBuyFragment securityOptMockSubBuyFragment = new SecurityOptMockSubBuyFragment();
            securityOptMockSubBuyFragment.setArguments(getArguments());
            SecurityOptMockSubSellFragment securityOptMockSubSellFragment = new SecurityOptMockSubSellFragment();
            securityOptMockSubSellFragment.setArguments(getArguments());
            SecurityOptMockSubDelegationFragment securityOptMockSubDelegationFragment = new SecurityOptMockSubDelegationFragment();
            securityOptMockSubDelegationFragment.setArguments(getArguments());
            SecurityOptMockSubQueryFragment securityOptMockSubQueryFragment = new SecurityOptMockSubQueryFragment();
            securityOptMockSubQueryFragment.setArguments(getArguments());
            subFragments.add(securityOptMockSubBuyFragment);
            subFragments.add(securityOptMockSubSellFragment);
            subFragments.add(securityOptMockSubDelegationFragment);
            subFragments.add(securityOptMockSubQueryFragment);
        }
    }

    public class SubFragmentPagerAdapter extends FragmentPagerAdapter {

        public SubFragmentPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return subFragments.get(position);
        }

        @Override
        public int getCount() {
            return subFragments.size();
        }
    }


    private void initSubTabs() {
        if (index == 0) {
            buyBtn.setTextColor(blue_color);
            sellBtn.setTextColor(black_color);
            recallBtn.setTextColor(black_color);
            queryBtn.setTextColor(black_color);

            buyFocus.setVisibility(View.VISIBLE);
            sellFocus.setVisibility(View.GONE);
            recallFocus.setVisibility(View.GONE);
            queryFocus.setVisibility(View.GONE);
        }
        if (index == 1) {
            buyBtn.setTextColor(black_color);
            sellBtn.setTextColor(blue_color);
            recallBtn.setTextColor(black_color);
            queryBtn.setTextColor(black_color);

            buyFocus.setVisibility(View.GONE);
            sellFocus.setVisibility(View.VISIBLE);
            recallFocus.setVisibility(View.GONE);
            queryFocus.setVisibility(View.GONE);
        }
        if (index == 2) {
            buyBtn.setTextColor(black_color);
            sellBtn.setTextColor(black_color);
            recallBtn.setTextColor(blue_color);
            queryBtn.setTextColor(black_color);

            buyFocus.setVisibility(View.GONE);
            sellFocus.setVisibility(View.GONE);
            recallFocus.setVisibility(View.VISIBLE);
            queryFocus.setVisibility(View.GONE);
        }
        if (index == 3) {
            buyBtn.setTextColor(black_color);
            sellBtn.setTextColor(black_color);
            recallBtn.setTextColor(black_color);
            queryBtn.setTextColor(blue_color);

            buyFocus.setVisibility(View.GONE);
            sellFocus.setVisibility(View.GONE);
            recallFocus.setVisibility(View.GONE);
            queryFocus.setVisibility(View.VISIBLE);
        }
    }

    private void initBundle(){
        Bundle bundle = getArguments();
        type = bundle.getString(SecurityOptActivity.BUNDLE_FROM_TYPE);
        if(type ==null || type.equals("")){
            index = 0;
        } else {
            if(type.equals(SecurityOptActivity.TYPE_BUY)){
                index = 0;
            }
            if(type.equals(SecurityOptActivity.TYPE_SELL)){
                index = 1;
            }
            if(type.equals(SecurityOptActivity.TYPE_RECALL)){
                index = 2;
            }
            if(type.equals(SecurityOptActivity.TYPE_SEARCH)){
                index = 3;
            }
        }
    }
}
