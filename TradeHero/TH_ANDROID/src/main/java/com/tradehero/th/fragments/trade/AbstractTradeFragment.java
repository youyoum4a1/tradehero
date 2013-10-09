package com.tradehero.th.fragments.trade;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/9/13 Time: 11:14 AM To change this template use File | Settings | File Templates. */
abstract public class AbstractTradeFragment extends DashboardFragment
        implements DTOCache.Listener<SecurityId, SecurityPositionDetailDTO>,
        FreshQuoteHolder.FreshQuoteListener
{
    private final static String TAG = AbstractTradeFragment.class.getSimpleName();

    public final static String BUNDLE_KEY_IS_BUY = BuyFragment.class.getName() + ".isBuy";
    public final static String BUNDLE_KEY_QUANTITY_BUY = BuyFragment.class.getName() + ".quantityBuy";
    public final static String BUNDLE_KEY_QUANTITY_SELL = BuyFragment.class.getName() + ".quantitySell";

    public final static long MILLISEC_QUOTE_REFRESH = 30000;
    public final static long MILLISEC_QUOTE_COUNTDOWN_PRECISION = 50;

    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected boolean querying = false;
    protected AsyncTask<Void, Void, SecurityPositionDetailDTO> fetchPositionDetailTask;

    protected FreshQuoteHolder freshQuoteHolder;
    protected QuoteDTO quoteDTO;
    protected boolean refreshingQuote = false;

    protected int mBuyQuantity;
    protected int mSellQuantity;
    protected boolean isTransactionTypeBuy = true;

    protected void initViews(View view)
    {
        // Prevent reuse of previous values when changing securities
        securityCompactDTO = null;
        securityPositionDetailDTO = null;
        quoteDTO = null;
        isTransactionTypeBuy = true;
    }

    @Override public void onResume()
    {
        THLog.d(TAG, "onResume");
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new SecurityId(args), true);
            isTransactionTypeBuy = args.getBoolean(BUNDLE_KEY_IS_BUY, true);
            mBuyQuantity = args.getInt(BUNDLE_KEY_QUANTITY_BUY, 0);
            mSellQuantity = args.getInt(BUNDLE_KEY_QUANTITY_SELL, 0);
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

    @Override public void onDestroyView()
    {
        if (fetchPositionDetailTask != null)
        {
            fetchPositionDetailTask.cancel(false);
        }
        fetchPositionDetailTask = null;
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

    public int getMaxPurchasableShares()
    {
        return getMaxPurchasableShares(this.quoteDTO);
    }

    public static int getMaxPurchasableShares(QuoteDTO quoteDTO)
    {
        if (quoteDTO == null || quoteDTO.ask == null || quoteDTO.ask == 0 || quoteDTO.toUSDRate == null || quoteDTO.toUSDRate == 0)
        {
            return 0;
        }
        return (int) Math.floor(THUser.getCurrentUser().portfolio.cashBalance / (quoteDTO.ask * quoteDTO.toUSDRate));
    }

    public int getMaxSellableShares()
    {
        return getMaxSellableShares(this.securityPositionDetailDTO);
    }

    public static int getMaxSellableShares(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        if (securityPositionDetailDTO == null || securityPositionDetailDTO.positions == null ||
                securityPositionDetailDTO.positions.size() == 0 || securityPositionDetailDTO.positions.get(0) == null ||
                securityPositionDetailDTO.positions.get(0).shares == null || securityPositionDetailDTO.positions.get(0).shares == 0)
        {
            return 0;
        }
        // TODO handle more portfolios
        return securityPositionDetailDTO.positions.get(0).shares;
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
                "US$", // TODO Have this currency taken from somewhere
                10, // TODO Have this value taken from somewhere
                "US$", // TODO Have this currency taken from somewhere
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
            linkWith(detailDTO, false);
        }
        else
        {
            SecurityCompactDTO compactDTO = securityCompactCache.get().get(this.securityId);
            if (compactDTO != null)
            {
                linkWith(compactDTO, false);
            }

            if (fetchPositionDetailTask != null)
            {
                fetchPositionDetailTask.cancel(false);
            }
            fetchPositionDetailTask = securityPositionDetailCache.get().getOrFetch(this.securityId, false, this);
            fetchPositionDetailTask.execute();
        }

        if (andDisplay)
        {
            display();
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
            display();
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
            this.securityCompactDTO = securityPositionDetailDTO.security;
        }
        else
        {
            this.securityCompactDTO = null;
        }

        if (andDisplay)
        {
            display();
        }
    }

    protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        this.quoteDTO = quoteDTO;
        if (andDisplay)
        {
            display();
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

    @Override public void onDTOReceived(SecurityId key, SecurityPositionDetailDTO value)
    {
        if (key.equals(this.securityId))
        {
            linkWith(value, true);
        }
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
}
