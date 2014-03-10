package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.position.SecurityPositionDetailDTOUtil;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 10/9/13 Time: 11:14 AM To change this template use File | Settings | File Templates. */
abstract public class AbstractBuySellFragment extends BasePurchaseManagerFragment
{
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_IS_BUY = AbstractBuySellFragment.class.getName() + ".isBuy";
    public final static String BUNDLE_KEY_QUANTITY_BUY = AbstractBuySellFragment.class.getName() + ".quantityBuy";
    public final static String BUNDLE_KEY_QUANTITY_SELL = AbstractBuySellFragment.class.getName() + ".quantitySell";
    public final static String BUNDLE_KEY_PROVIDER_ID_BUNDLE = AbstractBuySellFragment.class.getName() + ".providerId";

    public final static long MILLISEC_QUOTE_REFRESH = 30000;
    public final static long MILLISEC_QUOTE_COUNTDOWN_PRECISION = 50;

    public static boolean alreadyNotifiedMarketClosed = false;

    @Inject AlertDialogUtil alertDialogUtil;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject SecurityPositionDetailDTOUtil securityPositionDetailDTOUtil;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected PositionDTOCompactList positionDTOCompactList;
    protected PortfolioCompactDTO portfolioCompactDTO;
    protected boolean querying = false;
    protected DTOCache.Listener<SecurityId, SecurityPositionDetailDTO> securityPositionDetailCacheListener;
    protected DTOCache.GetOrFetchTask<SecurityId, SecurityPositionDetailDTO> fetchPositionDetailTask;

    protected ProviderId providerId;

    @Inject protected Lazy<UserProfileCache> userProfileCache;
    protected UserProfileDTO userProfileDTO;
    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> fetchUserProfileTask;

    protected FreshQuoteHolder freshQuoteHolder;
    protected FreshQuoteHolder.FreshQuoteListener freshQuoteListener;
    protected QuoteDTO quoteDTO;
    protected boolean refreshingQuote = false;

    protected boolean isTransactionTypeBuy = true;
    protected Integer mBuyQuantity;
    protected Integer mSellQuantity;

    protected MenuItem marketCloseIcon;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        collectFromParameters(getArguments());
        collectFromParameters(savedInstanceState);
        userProfileCacheListener = new AbstractBuySellUserProfileCacheListener(currentUserId.toUserBaseKey()); // We need to keep a strong reference because the cache does not
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

    @Override protected void initViews(View view)
    {
        // Prevent reuse of previous values when changing securities
        securityCompactDTO = null;
        quoteDTO = null;
        freshQuoteListener = createFreshQuoteListener();
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

        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
        linkWith(new SecurityId(securityIdBundle), true);

        UserProfileDTO profileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (profileDTO != null)
        {
            linkWith(profileDTO, true);
        }
        else
        {
            requestUserProfile();
        }
    }

    @Override public void onPause()
    {
        //THLog.d(TAG, "onPause");
        if (freshQuoteHolder != null)
        {
            freshQuoteHolder.cancel();
        }
        freshQuoteHolder = null;

        super.onPause();
    }

    //<editor-fold desc="ActionBar">
    protected void displayMarketClose()
    {
        if (marketCloseIcon != null)
        {
            boolean marketIsOpen = securityCompactDTO == null || securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen;
            marketCloseIcon.setVisible(!marketIsOpen);
            if (!marketIsOpen)
            {
                notifyOnceMarketClosed();
            }
        }
    }

    public void displayExchangeSymbol(ActionBar actionBar)
    {
        if (actionBar != null)
        {
            actionBar.setTitle(
                    securityId == null ? "-:-": String.format("%s:%s", securityId.exchange, securityId.securitySymbol));
        }
    }
    //</editor-fold>

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

    @Override public void onDestroyView()
    {
        detachFetchPositionDetailTask();
        detachFetchUserProfileTask();

        freshQuoteListener = null;
        querying = false;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        securityPositionDetailCacheListener = null;
        userProfileCacheListener = null;
        super.onDestroy();
    }

    protected void detachFetchPositionDetailTask()
    {
        if (fetchPositionDetailTask != null)
        {
            fetchPositionDetailTask.setListener(null);
        }
        fetchPositionDetailTask = null;
    }

    protected void detachFetchUserProfileTask()
    {
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.setListener(null);
        }
        fetchUserProfileTask = null;
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
        return portfolioCompactDTOUtil.getMaxPurchasableShares(this.portfolioCompactDTO, this.quoteDTO);
    }

    public Integer getMaxSellableShares()
    {
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        if (ownedPortfolioId != null && ownedPortfolioId.portfolioId != null && positionDTOCompactList != null)
        {
            return positionDTOCompactList.getMaxSellableShares(
                    this.quoteDTO,
                    this.portfolioCompactDTO);
        }
        return 0;
    }

    protected void requestPositionDetail()
    {
        if (fetchPositionDetailTask != null)
        {
            fetchPositionDetailTask.setListener(null);
        }
        securityPositionDetailCacheListener = new AbstractBuySellSecurityPositionCacheListener(this.securityId); // We need to keep a strong reference because the cache does not
        fetchPositionDetailTask = securityPositionDetailCache.get().getOrFetch(this.securityId, false, securityPositionDetailCacheListener);
        fetchPositionDetailTask.execute();
    }

    protected void requestUserProfile()
    {
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.cancel(false);
        }
        UserBaseKey baseKey = currentUserId.toUserBaseKey();
        fetchUserProfileTask = userProfileCache.get().getOrFetch(baseKey, false, userProfileCacheListener);
        fetchUserProfileTask.execute();
    }

    protected boolean hasValidInfoForBuy()
    {
        return securityId != null && securityCompactDTO != null && quoteDTO != null && quoteDTO.ask != null;
    }

    protected boolean hasValidInfoForSell()
    {
        return securityId != null && securityCompactDTO != null && quoteDTO != null && quoteDTO.bid != null;
    }

    protected Double getTotalCostForBuy()
    {
        if (mBuyQuantity == null)
        {
            return null;
        }
        if (quoteDTO.toUSDRate == null)
        {
            return mBuyQuantity * quoteDTO.ask;
        }
        return mBuyQuantity * quoteDTO.ask * quoteDTO.toUSDRate + SecurityUtils.DEFAULT_TRANSACTION_COST;
    }

    protected Double getNetProceedsForSell()
    {
        if (mSellQuantity == null)
        {
            return null;
        }
        if (quoteDTO.toUSDRate == null)
        {
            return mSellQuantity * quoteDTO.bid;
        }
        return mSellQuantity * quoteDTO.bid * quoteDTO.toUSDRate - SecurityUtils.DEFAULT_TRANSACTION_COST;
    }

    public String getBuyDetails()
    {
        if (!hasValidInfoForBuy())
        {
            return getResources().getString(R.string.buy_sell_buy_details_unavailable);
        }

        return String.format(
                getResources().getString(R.string.buy_sell_buy_details),
                mBuyQuantity,
                securityId.exchange,
                securityId.securitySymbol,
                securityCompactDTO.currencyDisplay,
                quoteDTO.ask,
                SecurityUtils.DEFAULT_TRANSACTION_CURRENCY_DISPLAY, // TODO Have this currencyDisplay taken from somewhere else
                SecurityUtils.DEFAULT_TRANSACTION_COST, // TODO Have this value taken from somewhere else
                SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, // TODO Have this currencyDisplay taken from somewhere else
                getTotalCostForBuy());
    }

    public String getSellDetails()
    {
        if (!hasValidInfoForSell())
        {
            return getResources().getString(R.string.buy_sell_sell_details_unavailable);
        }

        return String.format(
                getResources().getString(R.string.buy_sell_sell_details),
                mSellQuantity,
                securityId.exchange,
                securityId.securitySymbol,
                securityCompactDTO.currencyDisplay,
                quoteDTO.bid,
                SecurityUtils.DEFAULT_TRANSACTION_CURRENCY_DISPLAY, // TODO Have this currencyDisplay taken from somewhere
                SecurityUtils.DEFAULT_TRANSACTION_COST, // TODO Have this value taken from somewhere
                SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, // TODO Have this currencyDisplay taken from somewhere
                getNetProceedsForSell());
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
        this.securityCompactDTO = null;

        if (securityId == null)
        {
            return;
        }

        prepareFreshQuoteHolder();

        SecurityPositionDetailDTO detailDTO = securityPositionDetailCache.get().get(this.securityId);
        if (detailDTO != null)
        {
            linkWith(detailDTO, andDisplay);
        }
        else
        {
            SecurityCompactDTO compactDTO = securityCompactCache.get().get(this.securityId);
            if (compactDTO != null)
            {
                linkWith(compactDTO, andDisplay);
            }

            requestPositionDetail();
        }

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

    public void linkWith(final SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        if (securityPositionDetailDTO != null)
        {
            linkWith(securityPositionDetailDTO.security, andDisplay);
            linkWith(securityPositionDetailDTO.positions, andDisplay);
        }
        else
        {
            linkWith((SecurityCompactDTO) null, andDisplay);
            linkWith((PositionDTOCompactList) null, andDisplay);
        }

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

    protected void linkWithBuyOrSellQuantity(Integer newQuantity, boolean andDisplay)
    {
        if (isTransactionTypeBuy)
        {
            linkWithBuyQuantity(newQuantity, andDisplay);
        }
        else
        {
            linkWithSellQuantity(newQuantity, andDisplay);
        }
    }

    protected void linkWithBuyQuantity(Integer buyQuantity, boolean andDisplay)
    {
        this.mBuyQuantity = clampedBuyQuantity(buyQuantity);
    }

    protected Integer clampedBuyQuantity(Integer candidate)
    {
        Integer maxPurchasable = getMaxPurchasableShares();
        if (candidate == null || maxPurchasable == null)
        {
            return candidate;
        }
        return Math.min(candidate, maxPurchasable);
    }

    protected void clampBuyQuantity(boolean andDisplay)
    {
        linkWithBuyQuantity(mBuyQuantity, andDisplay);
    }

    protected void linkWithSellQuantity(Integer sellQuantity, boolean andDisplay)
    {
        this.mSellQuantity = clampedSellQuantity(sellQuantity);
    }

    protected Integer clampedSellQuantity(Integer candidate)
    {
        Integer maxSellable = getMaxSellableShares();
        if (candidate == null || maxSellable == null)
        {
            return candidate;
        }
        return Math.min(candidate, maxSellable);
    }

    protected void clampSellQuantity(boolean andDisplay)
    {
        linkWithSellQuantity(mSellQuantity, andDisplay);
    }

    protected void prepareFreshQuoteHolder()
    {
        if (freshQuoteHolder != null)
        {
            Timber.e("We should not have been cancelling here %s", freshQuoteHolder.identifier, new IllegalStateException());
            freshQuoteHolder.cancel();
        }
        freshQuoteHolder = new FreshQuoteHolder(securityId, MILLISEC_QUOTE_REFRESH, MILLISEC_QUOTE_COUNTDOWN_PRECISION);
        freshQuoteHolder.registerListener(freshQuoteListener);
        freshQuoteHolder.start();
    }

    abstract public void display();

    protected void notifyOnceMarketClosed()
    {
        if (!alreadyNotifiedMarketClosed)
        {
            alreadyNotifiedMarketClosed = true;
            notifyMarketClosed();
        }
    }

    protected void handleMarketCloseClicked()
    {
        notifyMarketClosed();
    }

    protected void notifyMarketClosed()
    {
        alertDialogUtil.popMarketClosed(getActivity(), securityId);
    }

    abstract protected FreshQuoteHolder.FreshQuoteListener createFreshQuoteListener();

    abstract protected class AbstractBuySellFreshQuoteListener implements FreshQuoteHolder.FreshQuoteListener
    {
        @Override abstract public void onMilliSecToRefreshQuote(long milliSecToRefresh);

        @Override public void onIsRefreshing(boolean refreshing)
        {
            setRefreshingQuote(refreshing);
        }

        @Override public void onFreshQuote(QuoteDTO quoteDTO)
        {
            linkWith(quoteDTO, true);
        }
    }

    private class AbstractBuySellSecurityPositionCacheListener implements DTOCache.Listener<SecurityId, SecurityPositionDetailDTO>
    {
        private final SecurityId securityId;

        public AbstractBuySellSecurityPositionCacheListener(final SecurityId securityId)
        {
            this.securityId = securityId;
        }

        @Override public void onDTOReceived(final SecurityId key, final SecurityPositionDetailDTO value, boolean fromCache)
        {
            if (key.equals(this.securityId))
            {
                linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(SecurityId key, Throwable error)
        {
            THToast.show(R.string.error_fetch_detailed_security_info);
            Timber.e("Error fetching the security position detail %s", key, error);
        }
    }

    private class AbstractBuySellUserProfileCacheListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        private final UserBaseKey userBaseKey;

        public AbstractBuySellUserProfileCacheListener(final UserBaseKey userBaseKey)
        {
            this.userBaseKey = userBaseKey;
        }

        @Override public void onDTOReceived(final UserBaseKey key, final UserProfileDTO value, boolean fromCache)
        {
            if (key.equals(userBaseKey))
            {
                linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Error fetching the user profile %s", key, error);
        }
    }
}
