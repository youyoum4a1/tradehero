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
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.tradehero.th.wxapi.WXEntryActivity;

import org.ocpsoft.prettytime.PrettyTime;

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
        setHeadViewMiddleSub("999 +10.10% +10.10");
        setHeadViewMiddleSubTextColor(upColor);
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
                    SecurityUserOptDTO securityUserOptDTO = adapter.getItem(i);
                    jumpToUserPage(securityUserOptDTO.userId);
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
                adapter.notifyDataSetChanged();
                optsLV.setMode(PullToRefreshBase.Mode.BOTH);
                onFinish();
            }

            @Override
            public void failure(RetrofitError error) {
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
                adapter.notifyDataSetChanged();
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
        weChatDTO.title = "一个股票涨涨涨";
        weChatDTO.type = WeChatMessageType.Trade;
        Intent gotoShareToWeChatIntent = new Intent(getActivity(), WXEntryActivity.class);
        gotoShareToWeChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WXEntryActivity.putWeChatDTO(gotoShareToWeChatIntent, weChatDTO);
        Application.context().startActivity(gotoShareToWeChatIntent);
    }

    private void jumpToUserPage(int userId){
        Bundle bundle = new Bundle();
        bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, userId);
        pushFragment(PortfolioFragment.class, bundle);
    }

    private Double getLatestPrice() {
        try {
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                return quoteDetail.last;
            } else {
                return securityCompactDTO.lastPrice;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private Double getRise() {
        try {
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                return quoteDetail.last - quoteDetail.prec;
            } else {
                return securityCompactDTO.lastPrice - securityCompactDTO.previousClose;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private THSignedNumber getRisePercentage() {
        try {
            double roi = 0;
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                roi = quoteDetail.last - quoteDetail.prec;
            } else {
                roi = securityCompactDTO.lastPrice - securityCompactDTO.previousClose;
            }
            THSignedNumber roiPercentage = THSignedPercentage.builder(roi * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            return roiPercentage;
        } catch (Exception e) {
            return null;
        }
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
                }

                @Override
                public void failure(RetrofitError error) {
                    quoteDetail = null;
                    Timber.e(error, "Error to get QuoteDetail");
                }
            };
            quoteServiceWrapper.getQuoteDetails(securityId, callback);
        } else {
            Callback<SecurityCompactDTO> callback = new Callback<SecurityCompactDTO>() {
                @Override
                public void success(SecurityCompactDTO dto, Response response) {
                    securityCompactDTO = dto;
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.e(error, "Error to get SecurityCompactDTO");
                    securityCompactDTO = null;
                }
            };
            quoteServiceWrapper.getSecurityCompactDTO(securityId, callback);
        }
    }
}
