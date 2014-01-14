package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/14/14 Time: 11:14 AM Copyright (c) TradeHero
 */
public class WatchlistPortfolioHeaderView extends LinearLayout
        implements DTOView<UserBaseKey>
{
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject protected Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;

    private WatchlistHeaderItem gainLoss;
    private WatchlistHeaderItem valuation;
    private SecurityIdList securityIdList;
    private UserBaseKey userBaseKey;

    //<editor-fold desc="Constructors">
    public WatchlistPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public WatchlistPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WatchlistPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    public void setOnStateChangeListener(TwoStateView.OnStateChange onStateChangeListener)
    {
        if (gainLoss != null)
        {
            gainLoss.setOnStateChange(onStateChangeListener);
        }

        // Only handle when user click on gain/loss area
        //
        //if (valuation != null)
        //{
        //    valuation.setOnStateChange(onStateChangeListener);
        //}
    }

    private void init()
    {
        DaggerUtils.inject(this);

        if (getChildCount() != 2)
        {
            throw new IllegalAccessError("Watchlist header view should have only 2 children, both are TwoStateView");
        }

        valuation = (WatchlistHeaderItem) getChildAt(0);
        if (valuation != null)
        {
            valuation.setFirstTitle(getContext().getString(R.string.current_value));
            valuation.setSecondTitle(getContext().getString(R.string.original_value));
        }
        gainLoss = (WatchlistHeaderItem) getChildAt(1);
        if (gainLoss != null)
        {
            gainLoss.setTitle(getContext().getString(R.string.gain_loss));
        }
    }

    @Override public void display(UserBaseKey userBaseKey)
    {
        linkWith(userBaseKey, true);
    }

    private void linkWith(UserBaseKey userBaseKey, boolean andDisplay)
    {
        this.userBaseKey = userBaseKey;
        securityIdList = userWatchlistPositionCache.get().get(this.userBaseKey);

        if (andDisplay)
        {
            displayValuation();
            displayGainLoss();
        }
    }

    private void displayGainLoss()
    {
        THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, getAbsoluteGain());
        gainLoss.setFirstValue(String.format("%.2f%%", getPercentageGain()));
        gainLoss.setSecondValue(thSignedNumber.toString());
    }

    private double getPercentageGain()
    {
        double totalInvested = getTotalInvested();
        if (totalInvested != 0.0)
        {
            double gainPercentage = (getTotalValue() - totalInvested) * 100 / totalInvested;
            if (gainPercentage < -100.0)
            {
                gainPercentage = -100.0;
            }
            return gainPercentage;
        }
        return 0.0;
    }

    private void displayValuation()
    {
        valuation.setFirstValue(SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY + getTotalValue());
        valuation.setSecondValue(SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY + getTotalInvested());
    }

    private double getAbsoluteGain()
    {
        return getTotalValue() - getTotalInvested();
    }

    private double getTotalValue()
    {
        double totalValue = 0.0;
        if (securityIdList != null)
        {
            for (SecurityId securityId: securityIdList)
            {
                WatchlistPositionDTO watchlistItem = watchlistPositionCache.get().get(securityId);
                if (watchlistItem != null && watchlistItem.securityDTO != null)
                {
                    totalValue += watchlistItem.securityDTO.getLastPriceInUSD() * watchlistItem.shares;
                }
            }
        }
        return totalValue;
    }

    private double getTotalInvested()
    {
        double totalInvested = 0.0;

        if (securityIdList != null)
        {
            for (SecurityId securityId: securityIdList)
            {
                WatchlistPositionDTO watchlistItem = watchlistPositionCache.get().get(securityId);
                if (watchlistItem != null && watchlistItem.securityDTO != null)
                {
                    totalInvested += (watchlistItem.getWatchlistPrice() * watchlistItem.securityDTO.toUSDRate) * watchlistItem.shares;
                }
            }
        }
        return totalInvested;
    }
}
