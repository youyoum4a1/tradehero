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
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.trade.quote.FreshQuoteHolder;
import com.tradehero.th.fragments.trade.quote.FreshQuoteInfo;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.prefs.ShowMarketClosed;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

abstract public class AbstractBuySellFragment extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_IS_BUY = AbstractBuySellFragment.class.getName() + ".isBuy";
    public final static String BUNDLE_KEY_QUANTITY_BUY = AbstractBuySellFragment.class.getName() + ".quantityBuy";
    public final static String BUNDLE_KEY_QUANTITY_SELL = AbstractBuySellFragment.class.getName() + ".quantitySell";
    public final static String BUNDLE_KEY_PROVIDER_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".providerId";

    public final static long MILLISEC_QUOTE_REFRESH = 30000;
    public final static long MILLISEC_QUOTE_COUNTDOWN_PRECISION = 50;

    @Inject AlertDialogUtil alertDialogUtil;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<SecurityCompactCacheRx> securityCompactCache;
    protected Subscription securityCompactSubscription;
    @Inject Lazy<SecurityPositionDetailCacheRx> securityPositionDetailCache;
    protected Subscription securityPositionDetailSubscription;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @Inject protected Lazy<UserProfileCacheRx> userProfileCache;
    @Inject THRouter thRouter;
    @Inject @ShowMarketClosed TimingIntervalPreference showMarketClosedIntervalPreference;

    @InjectRoute protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected PositionDTOCompactList positionDTOCompactList;
    protected PortfolioCompactDTO portfolioCompactDTO;
    protected boolean querying = false;

    protected ProviderId providerId;
    protected UserProfileDTO userProfileDTO;

    protected FreshQuoteHolder freshQuoteHolder;
    @Nullable Subscription quoteSubscription;
    @Nullable protected QuoteDTO quoteDTO;
    protected boolean refreshingQuote = false;

    protected boolean isTransactionTypeBuy = true;
    protected Integer mBuyQuantity;
    protected Integer mSellQuantity;

    protected MenuItem marketCloseIcon;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
    }

    @Nullable public static SecurityId getSecurityId(@NonNull Bundle args)
    {
        Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
        if (securityIdBundle == null)
        {
            return null;
        }
        return new SecurityId(securityIdBundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        collectFromParameters(getArguments());
        collectFromParameters(savedInstanceState);
        freshQuoteHolder = new FreshQuoteHolder(getActivity(), securityId, MILLISEC_QUOTE_REFRESH, MILLISEC_QUOTE_COUNTDOWN_PRECISION);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        collectFromParameters(savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void collectFromParameters(Bundle args)
    {
        if (args != null)
        {
            securityId = getSecurityId(getArguments());

            isTransactionTypeBuy = args.getBoolean(BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
            if (args.containsKey(BUNDLE_KEY_QUANTITY_BUY))
            {
                linkWithBuyQuantity(args.getInt(BUNDLE_KEY_QUANTITY_BUY), true);
            }
            if (args.containsKey(BUNDLE_KEY_QUANTITY_SELL))
            {
                linkWithSellQuantity(args.getInt(BUNDLE_KEY_QUANTITY_SELL), true);
            }

            Bundle providerIdBundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID_BUNDLE);
            if (providerIdBundle != null)
            {
                providerId = new ProviderId(providerIdBundle);
            }
        }
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        quoteSubscription = AndroidObservable.bindFragment(
                this,
                freshQuoteHolder.startObs())
                .subscribe(createFreshQuoteObserver());
    }

    @Override protected void initViews(View view)
    {
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        marketCloseIcon = menu.findItem(R.id.buy_sell_menu_market_status);
        displayMarketClose();
    }

    @Override public void onResume()
    {
        super.onResume();
        SecurityId securityIdFromArgs = getSecurityId(getArguments());
        if (securityIdFromArgs != null)
        {
            linkWith(securityIdFromArgs, true);
        }
        else
        {
            thRouter.inject(this);
            linkWith(securityId, true);
        }

        requestUserProfile();
    }

    //<editor-fold desc="ActionBar">
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
    //</editor-fold>

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        detachSecurityPositionDetailSubscription();

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

    @Override public void onDestroyView()
    {
        unsubscribe(quoteSubscription);
        quoteSubscription = null;
        detachSecurityCompactSubscription();
        detachSecurityPositionDetailSubscription();
        querying = false;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        freshQuoteHolder = null;
        super.onDestroy();
    }

    protected void detachSecurityCompactSubscription()
    {
        Subscription subscriptionCopy = securityCompactSubscription;
        if (subscriptionCopy != null)
        {
            subscriptionCopy.unsubscribe();
        }
        securityCompactSubscription = null;
    }

    protected void detachSecurityPositionDetailSubscription()
    {
        Subscription subscriptionCopy = securityPositionDetailSubscription;
        if (subscriptionCopy != null)
        {
            subscriptionCopy.unsubscribe();
        }
        securityPositionDetailSubscription = null;
    }

    public void setTransactionTypeBuy(boolean transactionTypeBuy)
    {
        this.isTransactionTypeBuy = transactionTypeBuy;
    }

    protected void setRefreshingQuote(boolean refreshingQuote)
    {
        this.refreshingQuote = refreshingQuote;
    }

    public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableShares(
                this.portfolioCompactDTO,
                this.quoteDTO);
    }

    public Integer getMaxSellableShares()
    {
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        if (ownedPortfolioId != null && positionDTOCompactList != null)
        {
            return positionDTOCompactList.getMaxSellableShares(
                    this.quoteDTO,
                    this.portfolioCompactDTO);
        }
        return 0;
    }

    protected void requestCompactDetail()
    {
        detachSecurityCompactSubscription();
        securityCompactSubscription = securityCompactCache.get()
                .get(this.securityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                {
                    @Override public void onCompleted()
                    {

                    }

                    @Override public void onError(Throwable e)
                    {

                    }

                    @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        linkWith(pair.second, true);
                    }
                });
    }

    protected void requestPositionDetail()
    {
        detachSecurityPositionDetailSubscription();
        securityPositionDetailSubscription = securityPositionDetailCache.get()
                .get(this.securityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<SecurityId, SecurityPositionDetailDTO>>()
                {
                    @Override public void onCompleted()
                    {
                        Timber.d("Completed security position detail");
                    }

                    @Override public void onError(Throwable e)
                    {
                        Timber.e(e, "getting %s", securityId);
                    }

                    @Override public void onNext(Pair<SecurityId, SecurityPositionDetailDTO> pair)
                    {
                        linkWith(pair.second, true);
                    }
                });
    }

    protected void requestUserProfile()
    {
        AndroidObservable.bindFragment(this, userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileCacheObserver());
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
        this.securityCompactDTO = null;

        if (securityId == null)
        {
            return;
        }

        requestCompactDetail();
        requestPositionDetail();

        if (andDisplay)
        {
            // Nothing to do in this class
        }
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

    public void linkWith(@NonNull final SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        linkWith(securityPositionDetailDTO.security, andDisplay);
        linkWith(securityPositionDetailDTO.positions, andDisplay);

        if (andDisplay)
        {
            // Nothing to do in this class
        }
    }

    public void linkWith(final PositionDTOCompactList positionDTOCompacts, boolean andDisplay)
    {
        this.positionDTOCompactList = positionDTOCompacts;
        if (andDisplay)
        {

        }
    }

    public void linkWith(final UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        if (andDisplay)
        {
            // Nothing to do really in this class
        }
    }

    protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        this.quoteDTO = quoteDTO;
        if (andDisplay)
        {
            // Nothing to do in this class
        }
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        if (andDisplay)
        {
            // TODO slider and max purchasable shares
        }
    }

    protected void clampBuyQuantity(boolean andDisplay)
    {
        linkWithBuyQuantity(mBuyQuantity, andDisplay);
    }

    protected void linkWithBuyQuantity(Integer buyQuantity, boolean andDisplay)
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

    protected void clampSellQuantity(boolean andDisplay)
    {
        linkWithSellQuantity(mSellQuantity, andDisplay);
    }

    protected void linkWithSellQuantity(Integer sellQuantity, boolean andDisplay)
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

    abstract public void display();

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
        alertDialogUtil.popMarketClosed(getActivity(), securityId);
    }

    abstract protected Observer<FreshQuoteInfo> createFreshQuoteObserver();

    abstract protected class AbstractBuySellFreshQuoteObserver implements Observer<FreshQuoteInfo>
    {
        @Override public void onNext(FreshQuoteInfo freshQuoteInfo)
        {
            setRefreshingQuote(freshQuoteInfo.isRefreshing);
            if (freshQuoteInfo.freshQuote != null)
            {
                linkWith(freshQuoteInfo.freshQuote, true);
            }
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "Failed fetching quote");
            THToast.show(R.string.error_fetch_quote);
        }
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new AbstractBuySellUserProfileCacheObserver();
    }

    protected class AbstractBuySellUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Error fetching the user profile", e);
        }
    }
}
