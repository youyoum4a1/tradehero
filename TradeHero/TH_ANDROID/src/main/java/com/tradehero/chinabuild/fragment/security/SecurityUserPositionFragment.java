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
import com.tradehero.chinabuild.data.SecurityUserPositionDTO;
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
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.tradehero.th.wxapi.WXEntryActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by palmer on 15/6/9.
 */
public class SecurityUserPositionFragment extends DashboardFragment {

    @Inject QuoteServiceWrapper quoteServiceWrapper;

    private int upColor;
    private int normalColor;
    private int downColor;
    private TradeHeroProgressBar tradeHeroProgressBar;
    private PullToRefreshListView positionsLV;
    private ImageView emptyIV;

    private SecurityPostionAdapter adapter;
    private ArrayList<SecurityUserPositionDTO> opts = new ArrayList();

    private SecurityId securityId;
    private String securityName;

    private SecurityCompactDTO securityCompactDTO;
    private QuoteDetail quoteDetail;

    private int currentPage = 1;
    private final int perPage = 20;

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
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(securityId!=null) {
            setHeadViewMiddleMain(securityName + "(" + securityId.getSecuritySymbol() + ")");
        }
        setHeadViewRight0(R.drawable.search);
        setHeadViewRight1(R.drawable.share);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_position, container, false);
        tradeHeroProgressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_position);
        emptyIV = (ImageView)view.findViewById(R.id.imgEmpty);
        positionsLV = (PullToRefreshListView)view.findViewById(R.id.listPositions);
        if(adapter==null){
            adapter = new SecurityPostionAdapter(getActivity(), opts);
        }
        positionsLV.setAdapter(adapter);

        positionsLV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveUserPositions();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveUserPositionsMore();
            }


        });
        positionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter != null) {
                    int index = --i ;
                    if (i < 0) {
                        i = 0;
                    }
                    SecurityUserPositionDTO securityUserPositionDTO = adapter.getItem(index);
                    enterUserMainPage(securityUserPositionDTO.userId);
                }
            }
        });

        if(adapter.getCount() > 0){
            tradeHeroProgressBar.stopLoading();
            tradeHeroProgressBar.setVisibility(View.GONE);
            positionsLV.getRefreshableView().setEmptyView(emptyIV);
            positionsLV.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            tradeHeroProgressBar.startLoading();
            tradeHeroProgressBar.setVisibility(View.VISIBLE);
            positionsLV.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            retrieveUserPositions();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveUserPositions();
        retriveLatestPriceInfo();
    }

    private void retrieveUserPositions() {
        Callback<List<SecurityUserPositionDTO>> callback = new Callback<List<SecurityUserPositionDTO>>() {
            @Override
            public void success(List<SecurityUserPositionDTO> optList, Response response) {
                adapter.setData(optList);
                positionsLV.setMode(PullToRefreshBase.Mode.BOTH);
                onFinish();
            }

            @Override
            public void failure(RetrofitError error) {
                positionsLV.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                onFinish();
            }

            private void onFinish() {
                tradeHeroProgressBar.stopLoading();
                tradeHeroProgressBar.setVisibility(View.GONE);
                positionsLV.getRefreshableView().setEmptyView(emptyIV);
                positionsLV.onRefreshComplete();
            }
        };
        currentPage = 1;
        quoteServiceWrapper.getSharePosition(securityId, currentPage, perPage, callback);
    }

    private void retrieveUserPositionsMore() {
        Callback<List<SecurityUserPositionDTO>> callback = new Callback<List<SecurityUserPositionDTO>>() {
            @Override
            public void success(List<SecurityUserPositionDTO> optList, Response response) {
                adapter.addMoreData(optList);
                onFinish();
            }

            @Override
            public void failure(RetrofitError error) {
                currentPage--;
                onFinish();
            }

            private void onFinish() {
                positionsLV.onRefreshComplete();
            }
        };
        currentPage++;
        quoteServiceWrapper.getSharePosition(securityId, currentPage, perPage, callback);
    }

    private void enterWechatSharePage(){
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.title = "一个股票涨涨涨";
        weChatDTO.type = WeChatMessageType.Trade;
        Intent gotoShareToWeChatIntent = new Intent(getActivity(), WXEntryActivity.class);
        gotoShareToWeChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WXEntryActivity.putWeChatDTO(gotoShareToWeChatIntent, weChatDTO);
        Application.context().startActivity(gotoShareToWeChatIntent);
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
        String raise = df.format(getRise());
        if (quoteDetail.getRiseRate() > 0) {
            setHeadViewMiddleSubTextColor(upColor);
            setHeadViewMiddleSub(price + " +" + raisePercent + " +" + raise);
        }
        if (quoteDetail.getRiseRate() == 0) {
            setHeadViewMiddleSubTextColor(normalColor);
            setHeadViewMiddleSub(price + " 0 0");
        }
        if (quoteDetail.getRiseRate() < 0) {
            setHeadViewMiddleSubTextColor(downColor);
            setHeadViewMiddleSub(price + " " + raisePercent + " " + raise);
        }
    }
}