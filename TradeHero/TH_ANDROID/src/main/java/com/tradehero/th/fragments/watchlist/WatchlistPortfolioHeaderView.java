package com.tradehero.th.fragments.watchlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

public class WatchlistPortfolioHeaderView extends LinearLayout
        implements DTOView<UserBaseKey>
{
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject protected Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;

    private WatchlistHeaderItem gainLoss;
    private WatchlistHeaderItem valuation;
    private TextView marking;
    private SecurityIdList securityIdList;
    private UserBaseKey userBaseKey;
    private PortfolioCompactDTO portfolioCompactDTO;
    private SimpleDateFormat markingDateFormat;
    private BroadcastReceiver watchlistItemDeletedReceiver;

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

        LinearLayout container = (LinearLayout) getChildAt(0);

        valuation = (WatchlistHeaderItem) container.getChildAt(0);
        if (valuation != null)
        {
            valuation.setFirstTitle(getContext().getString(R.string.watchlist_current_value));
            valuation.setSecondTitle(getContext().getString(R.string.watchlist_original_value));
        }
        gainLoss = (WatchlistHeaderItem) container.getChildAt(1);
        if (gainLoss != null)
        {
            gainLoss.setTitle(getContext().getString(R.string.watchlist_gain_loss));
        }

        marking = (TextView) findViewById(R.id.watchlist_position_list_marking);
        markingDateFormat = new SimpleDateFormat(getResources().getString(R.string.watchlist_marking_date_format));
    }

    @Override public void display(UserBaseKey userBaseKey)
    {
        linkWith(userBaseKey, true);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        watchlistItemDeletedReceiver = createWatchlistItemDeletedReceiver();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(watchlistItemDeletedReceiver, new IntentFilter(WatchlistItemView.WATCHLIST_ITEM_DELETED));
    }

    @Override protected void onDetachedFromWindow()
    {
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(watchlistItemDeletedReceiver);
        watchlistItemDeletedReceiver = null;
        super.onDetachedFromWindow();
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

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        if (andDisplay)
        {
            displayMarkingDate();
        }
    }

    private void displayGainLoss()
    {
        THSignedNumber firstNumber = new THSignedNumber(
                THSignedNumber.TYPE_PERCENTAGE,
                getAbsoluteGain(),
                THSignedNumber.WITHOUT_SIGN);

        THSignedNumber secondNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, getAbsoluteGain());
        gainLoss.setFirstValue(firstNumber.toString());
        gainLoss.setSecondValue(secondNumber.toString(0));
        gainLoss.setTitle(getContext().getString(getAbsoluteGain() >= 0 ? R.string.watchlist_gain : R.string.watchlist_loss));
        gainLoss.invalidate();
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
        valuation.setFirstValue(formatDisplayValue(getTotalValue()));
        valuation.setSecondValue(formatDisplayValue(getTotalInvested()));
        valuation.invalidate();
    }

    private String formatDisplayValue(double value)
    {
        return String.format("%s %s", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, new DecimalFormat("#,###").format(value));
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
                    totalInvested += (watchlistItem.watchlistPrice * watchlistItem.securityDTO.toUSDRate) * watchlistItem.shares;
                }
            }
        }
        return totalInvested;
    }

    private void displayMarkingDate()
    {
        if (marking != null)
        {
            marking.setText(
                    getResources().getString(R.string.watchlist_marking_date, getMarkingDate()));
        }
    }

    private String getMarkingDate()
    {
        if (portfolioCompactDTO != null && portfolioCompactDTO.markingAsOfUtc != null)
        {
            return markingDateFormat.format(portfolioCompactDTO.markingAsOfUtc);
        }
        return getResources().getString(R.string.na);
    }

    private BroadcastReceiver createWatchlistItemDeletedReceiver()
    {
        return new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                display(userBaseKey);
            }
        };
    }
}
