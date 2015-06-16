package com.tradehero.chinabuild.fragment.security;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.data.SecurityUserOptDTO;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.tradehero.th.wxapi.WXEntryActivity;
import com.tradehero.th.wxapi.WXMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by palmer on 15/6/9.
 */
public class SecurityUserOptFragment extends DashboardFragment{
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    private int upColor;
    private int normalColor;
    private int downColor;
    private TradeHeroProgressBar tradeHeroProgressBar;
    private PullToRefreshListView optsLV;
    private ImageView emptyIV;

    private SecurityId securityId;
    private String securityName;

    private SecurityOptAdapter adapter;
    private ArrayList<SecurityUserOptDTO> opts = new ArrayList();

    private int currentPage = 0;
    private final int perPage = 20;

    SecurityCompactDTO securityCompactDTO;
    QuoteDetail quoteDetail;

    private String subHead = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upColor = getResources().getColor(R.color.bar_up);
        normalColor = getResources().getColor(R.color.bar_normal);
        downColor = getResources().getColor(R.color.bar_down);

        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE);
        securityName = args.getString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME);
        if (securityIdBundle != null) {
            securityId = new SecurityId(securityIdBundle);
        } else {
            popCurrentFragment();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(securityId!=null) {
            subHead = securityName + "(" + securityId.getSecuritySymbol() + ")";
            setHeadViewMiddleMain(subHead);
        }
        setHeadViewRight0(R.drawable.search);
        setHeadViewRight1(R.drawable.share);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_opts, container, false);
        tradeHeroProgressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_opt);
        emptyIV = (ImageView)view.findViewById(R.id.imgEmpty);
        optsLV = (PullToRefreshListView)view.findViewById(R.id.listOpts);
        if(adapter==null){
            adapter = new SecurityOptAdapter(getActivity(), opts);
        }

        optsLV.setAdapter(adapter);

        optsLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveUserOpts();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveUserOptsMore();
            }

        });

        optsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter != null) {
                    int index = --i;
                    if (i < 0) {
                        i = 0;
                    }
                    SecurityUserOptDTO securityUserOptDTO = adapter.getItem(index);
                    enterUserMainPage(securityUserOptDTO.userId);
                }
            }
        });
        if(adapter.getCount() <= 0) {
            retrieveUserOpts();
            tradeHeroProgressBar.startLoading();
            tradeHeroProgressBar.setVisibility(View.VISIBLE);
            optsLV.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }else{
            optsLV.getRefreshableView().setEmptyView(emptyIV);
            optsLV.setMode(PullToRefreshBase.Mode.BOTH);
            tradeHeroProgressBar.stopLoading();
            tradeHeroProgressBar.setVisibility(View.GONE);
        }

        retriveLatestPriceInfo();
        return view;
    }

    private void retrieveUserOpts() {
        Callback<List<SecurityUserOptDTO>> callback = new Callback<List<SecurityUserOptDTO>>() {
            @Override
            public void success(List<SecurityUserOptDTO> optList, Response response) {
                adapter.setData(optList);
                optsLV.setMode(PullToRefreshBase.Mode.BOTH);
                onFinish();
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed to get user operations.");
                optsLV.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                onFinish();
            }


            private void onFinish() {
                tradeHeroProgressBar.stopLoading();
                tradeHeroProgressBar.setVisibility(View.GONE);
                optsLV.getRefreshableView().setEmptyView(emptyIV);
                optsLV.onRefreshComplete();
            }
        };
        currentPage = 1;
        quoteServiceWrapper.getTradeRecords(securityId, currentPage, perPage, callback);
    }

    private void retrieveUserOptsMore() {
        Callback<List<SecurityUserOptDTO>> callback = new Callback<List<SecurityUserOptDTO>>() {
            @Override
            public void success(List<SecurityUserOptDTO> optList, Response response) {
                adapter.addMoreData(optList);
                onFinish();
            }

            @Override
            public void failure(RetrofitError error) {
                currentPage--;
                onFinish();
            }


            private void onFinish() {
                optsLV.onRefreshComplete();
            }
        };
        currentPage++;
        quoteServiceWrapper.getTradeRecords(securityId, currentPage, perPage, callback);
    }

    private void enterSearchPage(){
        pushFragment(SearchUnitFragment.class, new Bundle());
    }

    @Override
    public void onClickHeadRight0() {
        enterSearchPage();
    }

    @Override
    public void onClickHeadRight1() {
        enterWechatSharePage();
    }

    private void enterWechatSharePage(){
        WeChatDTO weChatDTO = new WeChatDTO();
        String message = WXMessage.getSecurityShareMessage(getRisePercent(), subHead);
        weChatDTO.title = message + Constants.WECHAT_SHARE_URL_INSTALL_APP;
        weChatDTO.type = WeChatMessageType.Trade;
        Intent gotoShareToWeChatIntent = new Intent(getActivity(), WXEntryActivity.class);
        gotoShareToWeChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WXEntryActivity.putWeChatDTO(gotoShareToWeChatIntent, weChatDTO);
        Application.context().startActivity(gotoShareToWeChatIntent);
    }

    private void enterUserMainPage(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
        bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
        pushFragment(UserMainPage.class, bundle);
    }

    private double getLatestPrice() {
        try {
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                return quoteDetail.last;
            } else {
                return securityCompactDTO.lastPrice;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double getRise() {
        try {
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                return quoteDetail.last - quoteDetail.prec;
            } else {
                return securityCompactDTO.lastPrice - securityCompactDTO.previousClose;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String getRisePercentage() {
        double roi = 0;
        if (QuoteServiceWrapper.isChinaStock(securityId)) {
            roi = (quoteDetail.last - quoteDetail.prec)/quoteDetail.prec;
        } else {
            roi = (securityCompactDTO.lastPrice - securityCompactDTO.previousClose)/securityCompactDTO.previousClose;
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        String percentage = df.format(roi * 100)  + "%";
        return percentage;
    }

    private int getRisePercent(){
        double roi;
        if (QuoteServiceWrapper.isChinaStock(securityId)) {
            if(quoteDetail == null){
                return 0;
            }
            roi = (quoteDetail.last - quoteDetail.prec)/quoteDetail.prec;
        } else {
            if(securityCompactDTO == null){
                return 0;
            }
            roi = (securityCompactDTO.lastPrice - securityCompactDTO.previousClose)/securityCompactDTO.previousClose;
        }
        return (int)(roi * 100);
    }

    private void retriveLatestPriceInfo() {
        if (securityId == null) {
            return;
        }

        if (QuoteServiceWrapper.isChinaStock(securityId)) {
            Callback<QuoteDetail> callback = new Callback<QuoteDetail>() {
                @Override
                public void success(QuoteDetail detail, Response response) {
                    quoteDetail = detail;
                    updateSubHead();
                }

                @Override
                public void failure(RetrofitError error) {
                    quoteDetail = null;
                }
            };
            quoteServiceWrapper.getQuoteDetails(securityId, callback);
        } else {
            Callback<SecurityCompactDTO> callback = new Callback<SecurityCompactDTO>() {
                @Override
                public void success(SecurityCompactDTO dto, Response response) {
                    securityCompactDTO = dto;
                    updateSubHead();
                }

                @Override
                public void failure(RetrofitError error) {
                    securityCompactDTO = null;
                }
            };
            quoteServiceWrapper.getSecurityCompactDTO(securityId, callback);
        }
    }

    private void updateSubHead() {
        DecimalFormat df = new DecimalFormat("######0.00");
        String price = df.format(getLatestPrice());
        String raisePercent = getRisePercentage();
        double r = getRise();
        String raise = df.format(r);
        if (r > 0) {
            setHeadViewMiddleSubTextColor(upColor);
            setHeadViewMiddleSub(price + " +" + raisePercent + " +" + raise);
        }
        if (r == 0) {
            setHeadViewMiddleSubTextColor(normalColor);
            setHeadViewMiddleSub(price + " 0 0");
        }
        if (r < 0) {
            setHeadViewMiddleSubTextColor(downColor);
            setHeadViewMiddleSub(price + " " + raisePercent + " " + raise);
        }
    }

}
