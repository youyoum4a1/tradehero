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
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.prefs.ShowMarketClosed;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.route.THRouter;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.observers.EmptyObserver;
import timber.log.Timber;

abstract public class AbstractBuySellFragment extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_IS_BUY = AbstractBuySellFragment.class.getName() + ".isBuy";
    public final static String BUNDLE_KEY_QUANTITY_BUY = AbstractBuySellFragment.class.getName() + ".quantityBuy";
    public final static String BUNDLE_KEY_QUANTITY_SELL = AbstractBuySellFragment.class.getName() + ".quantitySell";
    public final static String BUNDLE_KEY_PROVIDER_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".providerId";

    public final static long MILLISEC_QUOTE_REFRESH = 30000;

    @Inject protected AlertDialogUtil alertDialogUtil;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected QuoteServiceWrapper quoteServiceWrapper;
    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected SecurityPositionDetailCacheRx securityPositionDetailCache;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @Inject THRouter thRouter;
    @Inject @ShowMarketClosed TimingIntervalPreference showMarketClosedIntervalPreference;
    @Inject ToastOnErrorAction toastOnErrorAction;

    protected ProviderId providerId;
    @InjectRoute protected SecurityId securityId;
    @Nullable Subscription quoteSubscription;
    @Nullable protected QuoteDTO quoteDTO;
    @Nullable protected Subscription securityCompactSubscription;
    @Nullable protected SecurityCompactDTO securityCompactDTO;

    protected Observable<SecurityPositionDetailDTO> securityPositionDetailObservable;
    @Nullable protected Subscription securityPositionDetailSubscription;
    @Nullable protected SecurityPositionDetailDTO securityPositionDetailDTO;

    @Nullable protected PositionDTOCompactList positionDTOCompactList;
    @Nullable protected PortfolioCompactDTO portfolioCompactDTO;
    @Nullable private Subscription userProfileSubscription;
    @Nullable protected UserProfileDTO userProfileDTO;
    protected boolean querying = false;

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
        securityId = getSecurityId(getArguments());
        if (securityId == null)
        {
            thRouter.inject(this);
        }
        securityPositionDetailObservable = securityPositionDetailCache
                        .get(this.securityId)
                        .map(pair -> pair.second)
                        .share()
                        .cache(1);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        collectFromParameters(savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        fetchQuote();
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

    @Override public void onStart()
    {
        super.onStart();
        fetchSecurityCompact();
        fetchSecurityPositionDetail();
        fetchUserProfile();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        unsubscribe(quoteSubscription);
        quoteSubscription = null;
        unsubscribe(securityPositionDetailSubscription);
        securityPositionDetailSubscription = null;
        unsubscribe(securityCompactSubscription);
        securityCompactSubscription = null;
        unsubscribe(userProfileSubscription);
        userProfileSubscription = null;
        unsubscribe(portfolioCompactListCacheSubscription);
        portfolioCompactListCacheSubscription = null;

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
        unsubscribe(quoteSubscription);
        quoteSubscription = null;
        unsubscribe(securityPositionDetailSubscription);
        securityPositionDetailSubscription = null;
        unsubscribe(securityCompactSubscription);
        securityCompactSubscription = null;
        unsubscribe(userProfileSubscription);
        userProfileSubscription = null;
        unsubscribe(portfolioCompactListCacheSubscription);
        portfolioCompactListCacheSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        querying = false;
        securityPositionDetailObservable = null;
        super.onDestroyView();
    }

    protected void collectFromParameters(Bundle args)
    {
        if (args != null)
        {
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

    protected void fetchQuote()
    {
        unsubscribe(quoteSubscription);
        quoteSubscription = AndroidObservable.bindFragment(
                this,
                quoteServiceWrapper.getQuoteRx(securityId)
                        .repeatWhen(observable -> observable.delay(MILLISEC_QUOTE_REFRESH, TimeUnit.MILLISECONDS)))
                .subscribe(quoteDTO -> linkWith(quoteDTO, true), toastOnErrorAction);
    }

    protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        this.quoteDTO = quoteDTO;
        if (andDisplay)
        {
            // Nothing to do in this class
        }
    }

    public void setTransactionTypeBuy(boolean transactionTypeBuy)
    {
        this.isTransactionTypeBuy = transactionTypeBuy;
    }

    protected void fetchSecurityCompact()
    {
        unsubscribe(securityCompactSubscription);
        securityCompactSubscription = AndroidObservable.bindFragment(this, securityCompactCache
                .get(this.securityId))
                .subscribe(new EmptyObserver<Pair<SecurityId, SecurityCompactDTO>>()
                {
                    @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        linkWith(pair.second, true);
                    }
                });
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

    protected void fetchSecurityPositionDetail()
    {
        unsubscribe(securityPositionDetailSubscription);
        securityPositionDetailSubscription = AndroidObservable.bindFragment(
                this,
                securityPositionDetailObservable)
                .subscribe(
                        this::linkWith,
                        e -> Timber.e(e, "getting %s", securityId));
    }

    public void linkWith(@NonNull final SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        this.securityPositionDetailDTO = securityPositionDetailDTO;
        linkWith(securityPositionDetailDTO.security, true);
        linkWith(securityPositionDetailDTO.positions, true);
    }

    protected void fetchUserProfile()
    {
        unsubscribe(userProfileSubscription);
        userProfileSubscription = AndroidObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .subscribe(createUserProfileCacheObserver());
    }

    @NonNull protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
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

    public void linkWith(final PositionDTOCompactList positionDTOCompacts, boolean andDisplay)
    {
        this.positionDTOCompactList = positionDTOCompacts;
    }

    public void linkWith(final UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
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

    public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableShares(
                this.portfolioCompactDTO,
                this.quoteDTO);
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

    abstract public void display();

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
        alertDialogUtil.popMarketClosed(getActivity(), securityId);
    }
}
