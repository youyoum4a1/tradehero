package com.tradehero.th.fragments.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import butterknife.Bind;
import com.android.common.SlidingTabLayout;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.security.BuySellBottomStockPagerAdapter;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.utils.metrics.events.BuySellEvent;
import com.tradehero.th.utils.metrics.events.ChartTimeEvent;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@Routable({
        "stock-security/:exchange/:symbol"
})
public class BuySellStockFragment extends AbstractBuySellFragment
{
    @Bind(R.id.tabs) protected SlidingTabLayout mSlidingTabLayout;
    @Bind(R.id.stock_details_header) ViewGroup stockDetailHeader;

    @Bind(R.id.chart_frame) protected RelativeLayout mInfoFrame;
    @Bind(R.id.trade_bottom_pager) protected ViewPager mBottomViewPager;

    @Inject Analytics analytics;

    @RouteProperty("exchange") String exchange;
    @RouteProperty("symbol") String symbol;

    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stock_buy_sell, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mSlidingTabLayout.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
    }

    @Override public void onStart()
    {
        super.onStart();
        analytics.fireEvent(new ChartTimeEvent(requisite.securityId, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan()));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        onDestroyOptionsMenuSubscriptions.add(securityObservable.startWith(securityCompactDTO)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<SecurityCompactDTO>()
                        {
                            @Override public void call(SecurityCompactDTO securityCompactDTO)
                            {
                                if(securityCompactDTO != null)
                                {
                                    setActionBarTitle(securityCompactDTO.name);
                                    setActionBarSubtitle(securityCompactDTO.getExchangeSymbol());
                                }
                            }
                        }, new TimberOnErrorAction1("Failed to update title and subtitle"))
        );
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return false;
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        bottomViewPagerAdapter = null;
        defaultPortfolio = null;
        super.onDestroyView();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (bottomViewPagerAdapter != null)
        {
            bottomViewPagerAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @NonNull @Override protected Observable<PortfolioCompactDTO> getDefaultPortfolio()
    {
        return portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                .map(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, PortfolioCompactDTO>()
                {
                    @Override public PortfolioCompactDTO call(Pair<UserBaseKey, PortfolioCompactDTOList> userBaseKeyPortfolioCompactDTOListPair)
                    {
                        PortfolioCompactDTO found = userBaseKeyPortfolioCompactDTOListPair.second.getDefaultPortfolio();
                        defaultPortfolio = found;
                        return found;
                    }
                })
                .share();
    }

    @Override protected void linkWith(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        // Nothing to do
    }

    @NonNull @Override protected Requisite createRequisite()
    {
        if (exchange != null && symbol != null)
        {
            return new Requisite(new SecurityId(exchange, symbol), getArguments(), portfolioCompactListCache, currentUserId);
        }
        return super.createRequisite();
    }

    @Override protected void linkWith(@NonNull OwnedPortfolioId purchaseApplicablePortfolioId)
    {
        if (bottomViewPagerAdapter == null)
        {
            bottomViewPagerAdapter = new BuySellBottomStockPagerAdapter(
                    getActivity(),
                    this.getChildFragmentManager(),
                    purchaseApplicablePortfolioId,
                    requisite.securityId,
                    currentUserId.toUserBaseKey());
            mBottomViewPager.setAdapter(bottomViewPagerAdapter);
            mSlidingTabLayout.setViewPager(mBottomViewPager);
        }
    }

    @NonNull @Override protected Observable<Boolean> getSupportSell()
    {
        return getPortfolioCompactObservable()
                .flatMap(new Func1<PortfolioCompactDTO, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final PortfolioCompactDTO portfolioCompactDTO)
                    {
                        return Observable.combineLatest(
                                quoteObservable,
                                getCloseablePositionObservable()
                                        .filter(new Func1<PositionDTO, Boolean>()
                                        {
                                            @Override public Boolean call(PositionDTO positionDTO)
                                            {
                                                return positionDTO == null || positionDTO.portfolioId == portfolioCompactDTO.id;
                                            }
                                        }),
                                new Func2<QuoteDTO, PositionDTO, Boolean>()
                                {
                                    @Override public Boolean call(
                                            @NonNull QuoteDTO quoteDTO,
                                            @Nullable PositionDTO closeablePositionDTO)
                                    {
                                        Integer max = getMaxSellableShares(portfolioCompactDTO, quoteDTO, closeablePositionDTO);
                                        return max != null && max > 0;
                                    }
                                });
                    }
                });
    }

    @Override public void displayBuySellPrice(@NonNull SecurityCompactDTO securityCompactDTO, @NonNull QuoteDTO quoteDTO)
    {
        //Nothing to do, no longer displaying price here.
    }

    @Override protected void handleBuySellButtonsClicked(View view)
    {
        boolean isTransactionTypeBuy;
        switch (view.getId())
        {
            case R.id.btn_buy:
                isTransactionTypeBuy = true;
                break;
            case R.id.btn_sell:
                isTransactionTypeBuy = false;
                break;
            default:
                throw new IllegalArgumentException("Unhandled button " + view.getId());
        }
        analytics.fireEvent(new BuySellEvent(isTransactionTypeBuy, requisite.securityId));
        super.handleBuySellButtonsClicked(view);
    }
}
