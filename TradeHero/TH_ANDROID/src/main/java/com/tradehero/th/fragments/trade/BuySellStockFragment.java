package com.ayondo.academy.fragments.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.common.SlidingTabLayout;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.ayondo.academy.R;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTO;
import com.ayondo.academy.api.portfolio.PortfolioCompactDTOList;
import com.ayondo.academy.api.position.PositionDTO;
import com.ayondo.academy.api.quote.QuoteDTO;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.fragments.security.BuySellBottomStockPagerAdapter;
import com.ayondo.academy.rx.TimberOnErrorAction1;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
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

    //TODO Change Analytics
    //@Inject Analytics analytics;

    @RouteProperty("exchange") String exchange;
    @RouteProperty("symbol") String symbol;

    private BuySellBottomStockPagerAdapter bottomViewPagerAdapter;

    protected StockDetailActionBarRelativeLayout actionBarLayout;

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
        //analytics.fireEvent(new ChartTimeEvent(requisite.securityId, BuySellBottomStockPagerAdapter.getDefaultChartTimeSpan()));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        final StockDetailActionBarRelativeLayout actionBarLayout =
                (StockDetailActionBarRelativeLayout) LayoutInflater.from(actionBar.getThemedContext())
                        .inflate(R.layout.stock_detail_custom_actionbar, null);
        this.actionBarLayout = actionBarLayout;
        onDestroyOptionsMenuSubscriptions.add(quoteObservable.observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<QuoteDTO, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(@NonNull QuoteDTO quoteDTO)
                    {
                        if (actionBarLayout.circleProgressBar != null)
                        {
                            return actionBarLayout.circleProgressBar.start(getMillisecondQuoteRefresh());
                        }
                        return Observable.just(true).delay(getMillisecondQuoteRefresh(), TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                                // Nothing to do
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to end of animation")));

        onDestroyOptionsMenuSubscriptions.add(
                securityObservable.startWith(securityCompactDTO)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<SecurityCompactDTO>()
                        {
                            @Override public void call(SecurityCompactDTO securityCompactDTO)
                            {
                                actionBarLayout.display(new StockDetailActionBarRelativeLayout.Requisite(
                                        requisite.securityId,
                                        securityCompactDTO,
                                        null, null));
                            }
                        }, new TimberOnErrorAction1("Failed to fetch list of watch list items")));
        actionBar.setCustomView(actionBarLayout);
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return false;
    }

    @Override public void onDestroyOptionsMenu()
    {
        actionBarLayout = null;
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
        //TODO Change Analytics
        //analytics.fireEvent(new BuySellEvent(isTransactionTypeBuy, requisite.securityId));
        super.handleBuySellButtonsClicked(view);
    }
}
