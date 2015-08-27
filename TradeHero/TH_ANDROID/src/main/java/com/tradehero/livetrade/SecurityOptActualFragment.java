package com.tradehero.livetrade;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.security.SecurityDetailSubViewPager;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.thirdPartyServices.haitong.HaitongUtils;
import com.tradehero.th.R;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.utils.DaggerUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import cn.htsec.data.pkg.trade.OnlineListener;
import cn.htsec.data.pkg.trade.TradeManager;

public class SecurityOptActualFragment extends Fragment implements View.OnClickListener {

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

    private int red_color;
    private int black_color;

    private String type = "";

    private TradeManager tradeManager;

    //First Enter Actual, show Bank Transfer Dialog
    private Dialog bankTransferHintDialog;
    @Inject CurrentUserId currentUserId;
    private TextView dlgConfirmTV;
    private TextView dlgCancelTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBundle();
        red_color = getResources().getColor(R.color.number_up);
        black_color = getResources().getColor(R.color.black);

        fragmentManager = getChildFragmentManager();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_opt_mock, container, false);
        initViews(view);
        initSubViewPager();

        tradeManager = TradeManager.getInstance(getActivity());
        tradeManager.setOnlineListener(new OnlineListener() {
            @Override
            public void onTimeout() {
                THToast.show("交易已超时，请重新登录！");
                if(getActivity()!=null) {
                    getActivity().finish();
                    if(!TradeManager.getInstance(getActivity()).isLogined()){

                    }
                }
            }
        });

        return view;
    }

    public void onResume(){
        super.onResume();
        if(!THSharePreferenceManager.isFirstTimeEnteredActualOptPage(getActivity(), currentUserId.get())){
            showBankTransferHintDialog();
        }
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

        buyFocus.setBackgroundColor(red_color);
        sellFocus.setBackgroundColor(red_color);
        recallFocus.setBackgroundColor(red_color);
        queryFocus.setBackgroundColor(red_color);

        buyBtn.setOnClickListener(this);
        sellBtn.setOnClickListener(this);
        recallBtn.setOnClickListener(this);
        queryBtn.setOnClickListener(this);

        viewPager = (SecurityDetailSubViewPager) view.findViewById(R.id.securityoptmocksubviewpager);
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();
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
            SecurityOptActualSubBuyFragment securityOptActualSubBuyFragment = new SecurityOptActualSubBuyFragment();
            securityOptActualSubBuyFragment.setArguments(getArguments());
            SecurityOptActualSubSellFragment securityOptActualSubSellFragment = new SecurityOptActualSubSellFragment();
            securityOptActualSubSellFragment.setArguments(getArguments());
            SecurityOptActualSubDelegationFragment securityOptActualSubDelegationFragment = new SecurityOptActualSubDelegationFragment();
            SecurityOptActualSubQueryFragment securityOptActualSubQueryFragment = new SecurityOptActualSubQueryFragment();
            subFragments.add(securityOptActualSubBuyFragment);
            subFragments.add(securityOptActualSubSellFragment);
            subFragments.add(securityOptActualSubDelegationFragment);
            subFragments.add(securityOptActualSubQueryFragment);
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
            buyBtn.setTextColor(red_color);
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
            sellBtn.setTextColor(red_color);
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
            recallBtn.setTextColor(red_color);
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
            queryBtn.setTextColor(red_color);

            buyFocus.setVisibility(View.GONE);
            sellFocus.setVisibility(View.GONE);
            recallFocus.setVisibility(View.GONE);
            queryFocus.setVisibility(View.VISIBLE);
        }
    }

    private void initBundle(){
        Bundle bundle = getArguments();
        type = bundle.getString(SecurityOptActivity.BUNDLE_FROM_TYPE, "");
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

    private void showBankTransferHintDialog(){
        if(bankTransferHintDialog == null){
            bankTransferHintDialog = new Dialog(getActivity());
            bankTransferHintDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bankTransferHintDialog.setCanceledOnTouchOutside(false);
            bankTransferHintDialog.setCancelable(false);
            bankTransferHintDialog.setContentView(R.layout.dialog_security_opt_bank_transfer_hint);
            dlgCancelTV = (TextView)bankTransferHintDialog.findViewById(R.id.dialog_cancel);
            dlgConfirmTV = (TextView)bankTransferHintDialog.findViewById(R.id.dialog_confirm);
            dlgConfirmTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bankTransferHintDialog != null && getActivity() != null){
                        bankTransferHintDialog.dismiss();
                        THSharePreferenceManager.setEnteredActualPageStatus(getActivity(), currentUserId.get(), true);
                        HaitongUtils.bankTransfer(getActivity());
                    }
                }
            });
            dlgCancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bankTransferHintDialog != null && getActivity() != null){
                        bankTransferHintDialog.dismiss();
                        THSharePreferenceManager.setEnteredActualPageStatus(getActivity(), currentUserId.get(), true);
                    }
                }
            });
        }
        if(!bankTransferHintDialog.isShowing()){
            bankTransferHintDialog.show();
        }
    }
}