package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THToast;
import com.tradehero.route.InjectRoute;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.persistence.position.PositionCompactListCacheRx;
import com.tradehero.th.persistence.prefs.ShowMarketClosed;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.route.THRouter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Actions;
import timber.log.Timber;

public class AbstractBuySellFragment extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".securityId";
    private final static String BUNDLE_KEY_IS_BUY = AbstractBuySellFragment.class.getName() + ".isBuy";
    private final static String BUNDLE_KEY_QUANTITY_BUY = AbstractBuySellFragment.class.getName() + ".quantityBuy";
    private final static String BUNDLE_KEY_QUANTITY_SELL = AbstractBuySellFragment.class.getName() + ".quantitySell";
    private final static String BUNDLE_KEY_PROVIDER_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".providerId";

    private final static long MILLISECOND_QUOTE_REFRESH = 30000;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected QuoteServiceWrapper quoteServiceWrapper;
    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject protected PositionCompactListCacheRx positionCompactListCache;
    @Inject protected THRouter thRouter;
    @Inject @ShowMarketClosed TimingIntervalPreference showMarketClosedIntervalPreference;

    protected ProviderId providerId;
    @InjectRoute protected SecurityId securityId;
    @Nullable protected QuoteDTO quoteDTO;
    @Nullable protected SecurityCompactDTO securityCompactDTO;

    @Nullable protected PositionDTOCompactList positionDTOCompactList;
    @Nullable protected PositionDTOCompact positionDTOCompact;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;

    protected boolean isTransactionTypeBuy = true;
    protected Integer mBuyQuantity;
    protected Integer mSellQuantity;

    protected MenuItem marketCloseIcon;

    public static class Param extends BasePurchaseManagerFragment.Param
    {
        @NonNull public final SecurityId securityId;
        @Nullable public final ProviderId providerId;

        public Param(
                @Nullable OwnedPortfolioId ownedPortfolioId,
                @NonNull SecurityId securityId,
                @Nullable ProviderId providerId)
        {
            super(ownedPortfolioId);
            this.securityId = securityId;
            this.providerId = providerId;
        }

        @Override protected void populate(@NonNull Bundle args)
        {
            super.populate(args);
            args.putBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            if (providerId != null)
            {
                args.putBundle(BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
            }
        }

        @Nullable static SecurityId getSecurityId(@NonNull Bundle args)
        {
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            if (securityIdBundle == null)
            {
                return null;
            }
            return new SecurityId(securityIdBundle);
        }

        @Nullable static ProviderId getProviderId(@NonNull Bundle args)
        {
            Bundle providerIdBundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID_BUNDLE);
            if (providerIdBundle == null)
            {
                return null;
            }
            return new ProviderId(providerIdBundle);
        }
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        collectFromParameters(getArguments());
        collectFromParameters(savedInstanceState);
        securityId = Param.getSecurityId(getArguments());
        if (securityId == null)
        {
            thRouter.inject(this);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        collectFromParameters(savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        marketCloseIcon = menu.findItem(R.id.buy_sell_menu_market_status);
        displayMarketClose();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchQuote();
        fetchSecurityCompact();
        fetchPositionCompactList();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
        if (mBuyQuantity != null)
        {
            outState.putInt(BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
        }
        if (mSellQuantity != null)
        {
            outState.putInt(BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
        }
    }

    @Override public void onStop()
    {
        quoteDTO = null;
        super.onStop();
    }

    protected void collectFromParameters(Bundle args)
    {
        if (args != null)
        {
            isTransactionTypeBuy = args.getBoolean(BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
            if (args.containsKey(BUNDLE_KEY_QUANTITY_BUY))
            {
                linkWithBuyQuantity(args.getInt(BUNDLE_KEY_QUANTITY_BUY));
            }
            if (args.containsKey(BUNDLE_KEY_QUANTITY_SELL))
            {
                linkWithSellQuantity(args.getInt(BUNDLE_KEY_QUANTITY_SELL));
            }

            ProviderId collected = Param.getProviderId(args);
            if (collected != null)
            {
                providerId = collected;
            }
        }
    }

    protected long getMillisecondQuoteRefresh()
    {
        return MILLISECOND_QUOTE_REFRESH;
    }

    protected void fetchQuote()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                quoteServiceWrapper.getQuoteRx(securityId)
                        .repeatWhen(observable -> observable.delay(getMillisecondQuoteRefresh(), TimeUnit.MILLISECONDS)))
                .subscribe(
                        this::linkWith,
                        new ToastOnErrorAction()));
    }

    protected void linkWith(QuoteDTO quoteDTO)
    {
        this.quoteDTO = quoteDTO;
    }

    public void setTransactionTypeBuy(boolean transactionTypeBuy)
    {
        this.isTransactionTypeBuy = transactionTypeBuy;
    }

    protected void fetchSecurityCompact()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, securityCompactCache
                .get(this.securityId))
                .subscribe(new Action1<Pair<SecurityId, SecurityCompactDTO>>()
                           {
                               @Override public void call(Pair<SecurityId, SecurityCompactDTO> pair)
                               {
                                   linkWith(pair.second, true);
                               }
                           },
                        Actions.empty()));
    }

    public void linkWith(final SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        if (!securityCompactDTO.getSecurityId().equals(this.securityId))
        {
            throw new IllegalArgumentException("This security compact is not for " + this.securityId);
        }
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            displayMarketClose();
        }
    }

    protected void fetchPositionCompactList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                positionCompactListCache.get(securityId)
                        .map(pair -> pair.second))
                .subscribe(
                        this::linkWith,
                        e -> {
                            THToast.show("Failed to fetch positions for this security");
                            Timber.e(e, "Failed to fetch positions for this security");
                        }));
    }

    public void linkWith(final PositionDTOCompactList positionDTOCompacts)
    {
        this.positionDTOCompactList = positionDTOCompacts;
        selectPositionDTO();
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        selectPositionDTO();
    }

    protected void selectPositionDTO()
    {
        if (positionDTOCompactList != null && portfolioCompactDTO != null)
        {
            this.positionDTOCompact = positionDTOCompactList.findFirstWhere(position -> position.portfolioId == portfolioCompactDTO.id);
        }
    }

    protected void clampBuyQuantity()
    {
        linkWithBuyQuantity(mBuyQuantity);
    }

    protected void linkWithBuyQuantity(Integer buyQuantity)
    {
        this.mBuyQuantity = getClampedBuyQuantity(buyQuantity);
    }

    protected Integer getClampedBuyQuantity(Integer candidate)
    {
        Integer maxPurchasable = getMaxPurchasableShares();
        if (candidate == null || maxPurchasable == null)
        {
            return candidate;
        }
        return Math.min(candidate, maxPurchasable);
    }

    public Integer getMaxPurchasableShares()
    {
        return PortfolioCompactDTOUtil.getMaxPurchasableShares(
                this.portfolioCompactDTO,
                this.quoteDTO,
                this.positionDTOCompact);
    }

    protected void clampSellQuantity()
    {
        linkWithSellQuantity(mSellQuantity);
    }

    protected void linkWithSellQuantity(Integer sellQuantity)
    {
        this.mSellQuantity = getClampedSellQuantity(sellQuantity);
    }

    protected Integer getClampedSellQuantity(Integer candidate)
    {
        Integer maxSellable = getMaxSellableShares();
        if (candidate == null || maxSellable == null || maxSellable == 0)
        {
            return candidate;
        }
        return Math.min(candidate, maxSellable);
    }

    @Nullable public Integer getMaxSellableShares()
    {
        return PortfolioCompactDTOUtil.getMaxSellableShares(
                this.portfolioCompactDTO,
                this.quoteDTO,
                this.positionDTOCompact);
    }

    public Double getUnRealizedPLRefCcy()
    {
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        if (ownedPortfolioId != null && positionDTOCompactList != null
                && this.quoteDTO != null && portfolioCompactDTO != null)
        {
            return positionDTOCompactList.getUnRealizedPLRefCcy(
                    this.quoteDTO,
                    this.portfolioCompactDTO
            );
        }
        return null;
    }

    protected void displayMarketClose()
    {
        boolean marketIsOpen = securityCompactDTO == null || securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen;
        if (!marketIsOpen)
        {
            notifyOnceMarketClosed();
        }
        if (marketCloseIcon != null)
        {
            marketCloseIcon.setVisible(!marketIsOpen);
        }
    }

    protected void notifyOnceMarketClosed()
    {
        if (showMarketClosedIntervalPreference.isItTime())
        {
            notifyMarketClosed();
            showMarketClosedIntervalPreference.justHandled();
        }
    }

    protected void notifyMarketClosed()
    {
        AlertDialogBuySellRxUtil.popMarketClosed(getActivity(), securityId)
                .subscribe(Actions.empty(), Actions.empty());
    }
}
