package com.ayondo.academy.fragments.leaderboard.filter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ayondo.academy.R;
import com.ayondo.academy.api.leaderboard.LeaderboardDTO;
import com.ayondo.academy.api.leaderboard.key.PerPagedFilteredLeaderboardKey;
import com.ayondo.academy.inject.HierarchyInjector;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LeaderboardFilterSliderContainer extends LinearLayout
{
    //TODO Change Analytics
    //@Inject Analytics analytics;

    @Bind(R.id.leaderboard_filter_monthly_activity_container) protected LeaderboardFilterValueSlider minMonthlyActivityView;
    @Bind(R.id.leaderboard_filter_win_ratio_container) protected LeaderboardFilterValueSlider minWinRatioView;
    @Bind(R.id.leaderboard_filter_holding_period_container) protected LeaderboardFilterValueSlider minHoldingPeriodView;
    @Bind(R.id.leaderboard_filter_relative_performance_container) protected LeaderboardFilterValueSlider minRelativePerformanceView;
    @Bind(R.id.leaderboard_filter_consistency_container) protected MinConsistencyLeaderboardFilterValueSlider minConsistencyView;
    @Bind(R.id.leaderboard_filter_reset_button) protected View buttonResetFilters;

    @NonNull protected PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey;
    @Nullable protected LeaderboardDTO leaderboardDTO;

    //<editor-fold desc="Constructors">
    public LeaderboardFilterSliderContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        buttonResetFilters.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                setParameters(getStartingFilter(getResources(), perPagedFilteredLeaderboardKey.id), leaderboardDTO);
                //TODO Change Analytics
                //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_FilterReset));
            }
        });
        displayPerPagedFilteredLeaderboardKey();
    }

    @Override protected void onDetachedFromWindow()
    {
        buttonResetFilters.setOnClickListener(null);
        super.onDetachedFromWindow();
    }

    public void setParameters(@NonNull PerPagedFilteredLeaderboardKey perPagedFilteredLeaderboardKey, @Nullable LeaderboardDTO leaderboardDTO)
    {
        this.perPagedFilteredLeaderboardKey = perPagedFilteredLeaderboardKey;
        this.leaderboardDTO = leaderboardDTO;
        displayPerPagedFilteredLeaderboardKey();
    }

    @NonNull
    public PerPagedFilteredLeaderboardKey getFilteredLeaderboardKey()
    {
        this.perPagedFilteredLeaderboardKey = new PerPagedFilteredLeaderboardKey(
                this.perPagedFilteredLeaderboardKey.id,
                null, // Page but we don't care
                null, // PerPage but we don't care
                minWinRatioView.getCurrentValue(),
                minMonthlyActivityView.getCurrentValue(),
                minHoldingPeriodView.getCurrentValue(),
                minRelativePerformanceView.getCurrentValue(),
                minConsistencyView.getCurrentValue()
        );
        return this.perPagedFilteredLeaderboardKey;
    }

    protected void displayPerPagedFilteredLeaderboardKey()
    {
        displayMonthlyActivity();
        displayWinRatio();
        displayHoldingPeriod();
        displayRelativePerformance();
        displayConsistency();
    }

    protected void displayMonthlyActivity()
    {
        if (minMonthlyActivityView != null)
        {
            if (perPagedFilteredLeaderboardKey.averageMonthlyTradeCount != null)
            {
                minMonthlyActivityView.setCurrentValue(Math.round(perPagedFilteredLeaderboardKey.averageMonthlyTradeCount));
            }
            else
            {
                minMonthlyActivityView.setDefaultCurrentValue();
            }
        }
    }

    protected void displayWinRatio()
    {
        if (minWinRatioView != null)
        {
            if (perPagedFilteredLeaderboardKey.winRatio != null)
            {
                minWinRatioView.setCurrentValue(Math.round(perPagedFilteredLeaderboardKey.winRatio));
            }
            else
            {
                minWinRatioView.setDefaultCurrentValue();
            }
        }
    }

    protected void displayHoldingPeriod()
    {
        if (minHoldingPeriodView != null)
        {
            if (perPagedFilteredLeaderboardKey.averageHoldingDays != null)
            {
                minHoldingPeriodView.setCurrentValue(Math.round(perPagedFilteredLeaderboardKey.averageHoldingDays));
            }
            else
            {
                minHoldingPeriodView.setDefaultCurrentValue();
            }
        }
    }

    protected void displayRelativePerformance()
    {
        if (minRelativePerformanceView != null)
        {
            if (perPagedFilteredLeaderboardKey.minSharpeRatio != null)
            {
                minRelativePerformanceView.setCurrentValue(Math.round(100 * perPagedFilteredLeaderboardKey.minSharpeRatio) / 100);
            }
            else
            {
                minRelativePerformanceView.setCurrentValue(0);
            }
        }
    }

    protected void displayConsistency()
    {
        if (minConsistencyView != null)
        {
            if (perPagedFilteredLeaderboardKey.minConsistency != null)
            {
                minConsistencyView.setCurrentValue(Math.round(100 * perPagedFilteredLeaderboardKey.minConsistency) / 100);
            }
            else
            {
                minConsistencyView.setDefaultCurrentValue();
            }
            if (leaderboardDTO != null && leaderboardDTO.avgStdDevPositionRoiInPeriod > 0)
            {
                minConsistencyView.setMaxValue((float) (1 / leaderboardDTO.avgStdDevPositionRoiInPeriod));
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
                 0f,
                 0f,
                 (float) resources.getInteger(R.integer.leaderboard_filter_consistency_min)
         );
    }

    public static boolean areInnerValuesEqualToStarting(Resources resources, PerPagedFilteredLeaderboardKey other)
    {
        return getStartingFilter(resources, 0).areInnerValuesEqual(other);
    }
}
