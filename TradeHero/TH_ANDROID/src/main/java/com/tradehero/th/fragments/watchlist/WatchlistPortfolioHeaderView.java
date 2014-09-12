package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.inject.HierarchyInjector;
import java.text.SimpleDateFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WatchlistPortfolioHeaderView extends LinearLayout
{
    private WatchlistHeaderItem gainLoss;
    private WatchlistHeaderItem valuation;
    private TextView marking;
    private WatchlistPositionDTOList watchlistPositionDTOs;
    @Nullable private PortfolioCompactDTO portfolioCompactDTO;
    @NotNull private SimpleDateFormat markingDateFormat;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public WatchlistPortfolioHeaderView(Context context)
    {
        super(context);
        init();
    }

    @SuppressWarnings("UnusedDeclaration")
    public WatchlistPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    @SuppressWarnings("UnusedDeclaration")
    public WatchlistPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    private void init()
    {
        HierarchyInjector.inject(this);
        markingDateFormat = new SimpleDateFormat(getResources().getString(R.string.watchlist_marking_date_format));
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initView();
    }

    private void initView()
    {
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

    public void linkWith(WatchlistPositionDTOList value, boolean andDisplay)
    {
        this.watchlistPositionDTOs = value;
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
        Double totalValueUsd = getTotalValueUsd();
        Double totalInvestedUsd = getTotalInvestedUsd();

        if (totalValueUsd == null || totalInvestedUsd == null)
        {
            gainLoss.setFirstTitle("-");
            gainLoss.setSecondTitle("-");
        }
        else
        {
            double absoluteGain = totalValueUsd - totalInvestedUsd;

            THSignedNumber firstNumber = THSignedPercentage.builder(100 * absoluteGain / totalInvestedUsd)
                    .withOutSign()
                    .build();

            THSignedNumber secondNumber = THSignedMoney.builder(absoluteGain).build();
            gainLoss.setFirstValue(firstNumber.toString());
            gainLoss.setSecondValue(secondNumber.toString());
            gainLoss.setTitle(getContext().getString(absoluteGain >= 0 ? R.string.watchlist_gain : R.string.watchlist_loss));
        }
        gainLoss.invalidate();
    }

    private void displayValuation()
    {
        Double totalValueUsd = getTotalValueUsd();
        Double totalInvestedUsd = getTotalInvestedUsd();

        if (totalValueUsd == null || totalInvestedUsd == null)
        {
            valuation.setFirstValue("-");
            valuation.setSecondValue("-");
        }
        else
        {
            valuation.setFirstValue(formatDisplayValue(totalValueUsd));
            valuation.setSecondValue(formatDisplayValue(totalInvestedUsd));
        }
        valuation.invalidate();
    }

    private String formatDisplayValue(double value)
    {
        return THSignedMoney.builder(value).build().toString();
    }

    @Nullable private Double getTotalValueUsd()
    {
        if (watchlistPositionDTOs != null)
        {
            return watchlistPositionDTOs.getCurrentValueUsd();
        }
        return null;
    }

    @Nullable private Double getTotalInvestedUsd()
    {
        if (watchlistPositionDTOs != null)
        {
            return watchlistPositionDTOs.getInvestedUsd();
        }
        return null;
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
}
