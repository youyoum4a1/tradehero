package com.tradehero.th.fragments.trade;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/9/13 Time: 11:14 AM To change this template use File | Settings | File Templates. */
abstract public class AbstractTradeFragment extends DashboardFragment
        implements FreshQuoteHolder.FreshQuoteListener, BaseFragment.ArgumentsChangeListener
{
    private final static String TAG = AbstractTradeFragment.class.getSimpleName();

    public final static String BUNDLE_KEY_IS_BUY = BuyFragment.class.getName() + ".isBuy";
    public final static String BUNDLE_KEY_POSITION_INDEX = BuyFragment.class.getName() + ".positionIndex";
    public final static String BUNDLE_KEY_QUANTITY_BUY = BuyFragment.class.getName() + ".quantityBuy";
    public final static String BUNDLE_KEY_QUANTITY_SELL = BuyFragment.class.getName() + ".quantitySell";

    public final static int DEFAULT_POSITION_INDEX = 0;

    public final static long MILLISEC_QUOTE_REFRESH = 30000;
    public final static long MILLISEC_QUOTE_COUNTDOWN_PRECISION = 50;

    private Bundle desiredArguments;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected boolean querying = false;
    protected DTOCache.Listener<SecurityId, SecurityPositionDetailDTO> securityPositionDetailCacheListener;
    protected AsyncTask<Void, Void, SecurityPositionDetailDTO> fetchPositionDetailTask;

    @Inject protected Lazy<UserProfileCache> userProfileCache;
    protected UserProfileDTO userProfileDTO;
    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected AsyncTask<Void, Void, UserProfileDTO> fetchUserProfileTask;

    protected FreshQuoteHolder freshQuoteHolder;
    protected QuoteDTO quoteDTO;
    protected boolean refreshingQuote = false;

    protected boolean isTransactionTypeBuy = true;
    protected int mBuyQuantity;
    protected int mSellQuantity;
    protected int mPositionIndex = DEFAULT_POSITION_INDEX;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        collectFromParameters(savedInstanceState);
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
            mBuyQuantity = args.getInt(BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
            mSellQuantity = args.getInt(BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
            mPositionIndex = args.getInt(BUNDLE_KEY_POSITION_INDEX, mPositionIndex);
        }
    }

    protected void initViews(View view)
    {
        // Prevent reuse of previous values when changing securities
        securityCompactDTO = null;
        securityPositionDetailDTO = null;
        quoteDTO = null;
    }

    @Override public void onResume()
    {
        THLog.d(TAG, "onResume");
        super.onResume();
        if (desiredArguments == null)
        {
            desiredArguments = getArguments();
        }

        if (desiredArguments != null)
        {
            linkWith(new SecurityId(desiredArguments), true);
            isTransactionTypeBuy = desiredArguments.getBoolean(BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
            mBuyQuantity = desiredArguments.getInt(BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
            mSellQuantity = desiredArguments.getInt(BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
            mPositionIndex = desiredArguments.getInt(BUNDLE_KEY_POSITION_INDEX, mPositionIndex);
        }

        UserProfileDTO profileDTO = userProfileCache.get().get(THUser.getCurrentUserBase().getBaseKey());
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
        THLog.d(TAG, "onPause");
        if (freshQuoteHolder != null)
        {
            freshQuoteHolder.cancel();
        }
        freshQuoteHolder = null;

        super.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_IS_BUY, isTransactionTypeBuy);
        outState.putInt(BUNDLE_KEY_QUANTITY_BUY, mBuyQuantity);
        outState.putInt(BUNDLE_KEY_QUANTITY_SELL, mSellQuantity);
        outState.putInt(BUNDLE_KEY_POSITION_INDEX, mPositionIndex);
    }

    @Override public void onDestroyView()
    {
        if (fetchPositionDetailTask != null)
        {
            fetchPositionDetailTask.cancel(false);
        }
        fetchPositionDetailTask = null;
        securityPositionDetailCacheListener = null;

        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.cancel(false);
        }
        fetchUserProfileTask = null;
        userProfileCacheListener = null;

        querying = false;

        super.onDestroyView();
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
        return getMaxPurchasableShares(this.quoteDTO, userProfileDTO);
    }

    public static Integer getMaxPurchasableShares(QuoteDTO quoteDTO, UserProfileDTO userProfileDTO)
    {
        if (quoteDTO == null || quoteDTO.ask == null || quoteDTO.ask == 0 || quoteDTO.toUSDRate == null || quoteDTO.toUSDRate == 0 ||
                userProfileDTO == null || userProfileDTO.portfolio == null)
        {
            return null;
        }
        return (int) Math.floor(userProfileDTO.portfolio.cashBalance / (quoteDTO.ask * quoteDTO.toUSDRate));
    }

    public Integer getMaxSellableShares()
    {
        // TODO handle more portfolios
        return getMaxSellableShares(this.securityPositionDetailDTO, mPositionIndex);
    }

    public static Integer getMaxSellableShares(SecurityPositionDetailDTO securityPositionDetailDTO, int positionIndex)
    {
        if (securityPositionDetailDTO == null || securityPositionDetailDTO.positions == null ||
                securityPositionDetailDTO.positions.size() == 0 || securityPositionDetailDTO.positions.get(positionIndex) == null ||
                securityPositionDetailDTO.positions.get(positionIndex).shares == null || securityPositionDetailDTO.positions.get(positionIndex).shares == 0)
        {
            return null;
        }
        return securityPositionDetailDTO.positions.get(positionIndex).shares;
    }

    protected void requestPositionDetail()
    {
        if (fetchPositionDetailTask != null)
        {
            fetchPositionDetailTask.cancel(false);
        }
        securityPositionDetailCacheListener = createSecurityPositionDetailListener(this.securityId); // We need to keep a strong reference because the cache does not
        fetchPositionDetailTask = securityPositionDetailCache.get().getOrFetch(this.securityId, false, securityPositionDetailCacheListener);
        fetchPositionDetailTask.execute();
    }

    protected void requestUserProfile()
    {
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.cancel(false);
        }
        UserBaseKey baseKey = THUser.getCurrentUserBase().getBaseKey();
        userProfileCacheListener = createUserProfileListener(baseKey); // We need to keep a strong reference because the cache does not
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
        if (quoteDTO.toUSDRate == null)
        {
            return mBuyQuantity * quoteDTO.ask;
        }
        return mBuyQuantity * quoteDTO.ask * quoteDTO.toUSDRate;
    }

    protected Double getTotalCostForSell()
    {
        if (quoteDTO.toUSDRate == null)
        {
            return mSellQuantity * quoteDTO.bid;
        }
        return mSellQuantity * quoteDTO.bid * quoteDTO.toUSDRate;
    }

    public String getBuyDetails()
    {
        if (!hasValidInfoForBuy())
        {
            return getResources().getString(R.string.buy_details_unavailable);
        }

        return String.format(
                getResources().getString(R.string.buy_details),
                mBuyQuantity,
                securityId.exchange,
                securityId.securitySymbol,
                securityCompactDTO.currencyDisplay,
                quoteDTO.ask,
                SecurityUtils.DEFAULT_TRANSACTION_CURRENCY_DISPLAY, // TODO Have this currency taken from somewhere else
                SecurityUtils.DEFAULT_TRANSACTION_COST, // TODO Have this value taken from somewhere else
                SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, // TODO Have this currency taken from somewhere else
                getTotalCostForBuy());
    }

    public String getSellDetails()
    {
        if (!hasValidInfoForSell())
        {
            return getResources().getString(R.string.sell_details_unavailable);
        }

        return String.format(
                getResources().getString(R.string.sell_details),
                mSellQuantity,
                securityId.exchange,
                securityId.securitySymbol,
                securityCompactDTO.currencyDisplay,
                quoteDTO.bid,
                "US$", // TODO Have this currency taken from somewhere
                10, // TODO Have this value taken from somewhere
                "US$", // TODO Have this currency taken from somewhere
                getTotalCostForSell());
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
        this.securityCompactDTO = null;
        this.securityPositionDetailDTO = null;

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
        THLog.d(TAG, "Display compact isNull: " + (securityCompactDTO == null ? "true" : "false"));
        if (andDisplay)
        {
            // Nothing to do in this class
        }
    }

    public void linkWith(final SecurityPositionDetailDTO securityPositionDetailDTO, boolean andDisplay)
    {
        if (!securityPositionDetailDTO.getSecurityId().equals(this.securityId))
        {
            throw new IllegalArgumentException("This security compact is not for " + this.securityId);
        }

        this.securityPositionDetailDTO = securityPositionDetailDTO;

        if (securityPositionDetailDTO != null)
        {
            linkWith(securityPositionDetailDTO.security, andDisplay);
        }
        else
        {
            linkWith((SecurityCompactDTO) null, andDisplay);
        }

        if (andDisplay)
        {
            // Nothing to do in this class
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

    protected void prepareFreshQuoteHolder()
    {
        if (freshQuoteHolder != null)
        {
            THLog.e(TAG, "We should not have been cancelling here " + freshQuoteHolder.identifier, new IllegalStateException());
            freshQuoteHolder.cancel();
        }
        freshQuoteHolder = new FreshQuoteHolder(securityId, MILLISEC_QUOTE_REFRESH, MILLISEC_QUOTE_COUNTDOWN_PRECISION);
        freshQuoteHolder.registerListener(this);
        freshQuoteHolder.start();
    }

    abstract public void display();

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> createUserProfileListener(final UserBaseKey userBaseKey)
    {
        return new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
            {
                if (key.equals(userBaseKey))
                {
                    linkWith(value, true);
                }
            }
        };
    }

    private DTOCache.Listener<SecurityId, SecurityPositionDetailDTO> createSecurityPositionDetailListener(final SecurityId securityId)
    {
        return new DTOCache.Listener<SecurityId, SecurityPositionDetailDTO>()
        {
            @Override public void onDTOReceived(SecurityId key, SecurityPositionDetailDTO value)
            {
                if (key.equals(securityId))
                {
                    linkWith(value, true);
                }
            }
        };
    }

    //<editor-fold desc="FreshQuoteHolder.FreshQuoteListener">
    @Override abstract public void onMilliSecToRefreshQuote(long milliSecToRefresh);

    @Override public void onIsRefreshing(boolean refreshing)
    {
        setRefreshingQuote(refreshing);
    }

    @Override public void onFreshQuote(QuoteDTO quoteDTO)
    {
        linkWith(quoteDTO, true);
    }
    //</editor-fold>

    //<editor-fold desc="BaseFragment.ArgumentsChangeListener">
    @Override public void onArgumentsChanged(Bundle args)
    {
        desiredArguments = args;
    }
    //</editor-fold>
}
