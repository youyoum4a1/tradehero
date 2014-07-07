package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.route.InjectRoute;
import com.tradehero.common.persistence.DTOCacheNew;
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
import com.tradehero.th.utils.THRouter;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

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
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject THRouter thRouter;

    @InjectRoute protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected PositionDTOCompactList positionDTOCompactList;
    protected PortfolioCompactDTO portfolioCompactDTO;
    protected boolean querying = false;
    protected DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO> securityPositionDetailListener;

    protected ProviderId providerId;
    protected UserProfileDTO userProfileDTO;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    protected FreshQuoteHolder freshQuoteHolder;
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
        securityPositionDetailListener = createSecurityPositionCacheListener();
        userProfileCacheListener = createUserProfileCacheListener();
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

        if (securityIdBundle != null)
        {
            linkWith(new SecurityId(securityIdBundle), true);
        }
        else
        {
            thRouter.inject(this);
            linkWith(securityId, true);
        }

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

        detachSecurityPositionDetailCache();
        detachUserProfileCache();
        destroyFreshQuoteHolder();

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
        detachSecurityPositionDetailCache();
        detachUserProfileCache();
        destroyFreshQuoteHolder();
        querying = false;

        super.onDestroyView();
    }

    protected void destroyFreshQuoteHolder()
    {
        if (freshQuoteHolder != null)
        {
            freshQuoteHolder.destroy();
        }
        freshQuoteHolder = null;
    }

    @Override public void onDestroy()
    {
        userProfileCacheListener = null;
        securityPositionDetailListener = null;
        super.onDestroy();
    }

    protected void detachSecurityPositionDetailCache()
    {
        securityPositionDetailCache.get().unregister(securityPositionDetailListener);
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
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
        detachSecurityPositionDetailCache();
        securityPositionDetailCache.get().register(this.securityId, securityPositionDetailListener);
        securityPositionDetailCache.get().getOrFetchAsync(this.securityId);
    }

    protected void requestUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
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
        this.securityPositionDetailDTO = securityPositionDetailDTO;
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
        if (candidate == null || maxSellable == null || maxSellable == 0)
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
        destroyFreshQuoteHolder();
        freshQuoteHolder = new FreshQuoteHolder(securityId, MILLISEC_QUOTE_REFRESH, MILLISEC_QUOTE_COUNTDOWN_PRECISION);
        freshQuoteHolder.setListener(createFreshQuoteListener());
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

    protected DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO> createSecurityPositionCacheListener()
    {
        return new AbstractBuySellSecurityPositionCacheListener();
    }

    protected class AbstractBuySellSecurityPositionCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO>
    {
        @Override public void onDTOReceived(final SecurityId key, final SecurityPositionDetailDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(SecurityId key, Throwable error)
        {
            THToast.show(R.string.error_fetch_detailed_security_info);
            Timber.e("Error fetching the security position detail %s", key, error);
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new AbstractBuySellUserProfileCacheListener();
    }

    protected class AbstractBuySellUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(final UserBaseKey key, final UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Error fetching the user profile %s", key, error);
        }
    }
}
