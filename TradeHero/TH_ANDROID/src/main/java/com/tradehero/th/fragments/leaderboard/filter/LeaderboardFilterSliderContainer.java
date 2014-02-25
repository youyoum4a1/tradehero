package com.tradehero.th.fragments.leaderboard.filter;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.PerPagedFilteredLeaderboardKey;

/**
 * Created by xavier on 2/12/14.
 */
public class LeaderboardFilterSliderContainer extends LinearLayout
{
    public static final String TAG = LeaderboardFilterSliderContainer.class.getSimpleName();

    protected PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey;

    //<editor-fold desc="Constructors">
    public LeaderboardFilterSliderContainer(Context context)
    {
        super(context);
    }

    public LeaderboardFilterSliderContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardFilterSliderContainer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @InjectView(R.id.leaderboard_filter_monthly_activity_container) protected LeaderboardFilterValueSlider monthlyActivityView;
    @InjectView(R.id.leaderboard_filter_win_ratio_container) protected LeaderboardFilterValueSlider winRatioView;
    @InjectView(R.id.leaderboard_filter_holding_period_container) protected LeaderboardFilterValueSlider holdingPeriodView;
    @InjectView(R.id.leaderboard_filter_reset_button) protected View buttonResetFilters;

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (buttonResetFilters != null)
        {
            buttonResetFilters.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    int leaderboardKey = perPagedFilteredLeaderboardKey != null ? perPagedFilteredLeaderboardKey.key : 0;
                    setFilteredLeaderboardKey(getStartingFilter(getResources(), leaderboardKey));
                }
            });
        }
        displayPerPagedFilteredLeaderboardKey();
    }

    @Override protected void onDetachedFromWindow()
    {
        if (buttonResetFilters != null)
        {
            buttonResetFilters.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    public void setFilteredLeaderboardKey(PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey)
    {
        this.perPagedFilteredLeaderboardKey = perPagedFilteredLeaderboardKey;
        displayPerPagedFilteredLeaderboardKey();
    }

    public PerPagedFilteredLeaderboardKey getFilteredLeaderboardKey()
    {
        this.perPagedFilteredLeaderboardKey = new PerPagedFilteredLeaderboardKey(
                this.perPagedFilteredLeaderboardKey.key,
                null, // Page but we don't care
                null, // PerPage but we don't care
                winRatioView.getCurrentValue(),
                monthlyActivityView.getCurrentValue(),
                holdingPeriodView.getCurrentValue(),
                null, // MinSharpeRatio but we don't care
                null // MaxPosRoiVolatility but we don't care
        );
        return this.perPagedFilteredLeaderboardKey;
    }

    public void displayPerPagedFilteredLeaderboardKey()
    {
        if (perPagedFilteredLeaderboardKey != null)
        {
            if (perPagedFilteredLeaderboardKey.averageMonthlyTradeCount != null && monthlyActivityView != null)
            {
                monthlyActivityView.setCurrentValue(Math.round(perPagedFilteredLeaderboardKey.averageMonthlyTradeCount));
            }
            else if (monthlyActivityView != null)
            {
                monthlyActivityView.setDefaultCurrentValue();
            }

            if (perPagedFilteredLeaderboardKey.winRatio != null && winRatioView != null)
            {
                winRatioView.setCurrentValue(Math.round(perPagedFilteredLeaderboardKey.winRatio));
            }
            else if (winRatioView != null)
            {
                winRatioView.setDefaultCurrentValue();
            }

            if (perPagedFilteredLeaderboardKey.averageHoldingDays != null && holdingPeriodView != null)
            {
                holdingPeriodView.setCurrentValue(Math.round(perPagedFilteredLeaderboardKey.averageHoldingDays));
            }
            else if (holdingPeriodView != null)
            {
                holdingPeriodView.setDefaultCurrentValue();
            }
        }
    }

    public static PerPagedFilteredLeaderboardKey getStartingFilter(Resources resources, Integer key)
    {
         return new PerPagedFilteredLeaderboardKey(
                 key,
                 null, // Page but we don't care
                 null, // PerPage but we don't care
                 (float) resources.getInteger(R.integer.leaderboard_filter_win_ratio_min),
                 (float) resources.getInteger(R.integer.leaderboard_filter_monthly_activity_min),
                 (float) 0,
                 null, // MinSharpeRatio but we don't care
                 null // MaxPosRoiVolatility but we don't care
         );
    }

    public static boolean areInnerValuesEqualToStarting(Resources resources, PerPagedFilteredLeaderboardKey other)
    {
        return getStartingFilter(resources, 0).areInnerValuesEqual(other);
    }
}
