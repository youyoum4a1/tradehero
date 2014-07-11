package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.widget.GaugeView;
import com.tradehero.common.widget.NumericalAnimatedTextView;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class UserStatisticView extends LinearLayout
    implements DTOView<LeaderboardUserDTO>
{
    @InjectView(R.id.leaderboard_dayshold_tv) @Optional NumericalAnimatedTextView daysHoldTv;
    @InjectView(R.id.leaderboard_position_tv) @Optional NumericalAnimatedTextView positionsCountTv;
    @InjectView(R.id.leaderboard_tradecount_tv) @Optional NumericalAnimatedTextView tradeCountTv;

    @InjectView(R.id.leaderboard_gauge_performance) @Optional GaugeView performanceGauge;
    @InjectView(R.id.leaderboard_gauge_tradeconsistency) @Optional GaugeView tradeConsistencyGauge;
    @InjectView(R.id.leaderboard_gauge_winrate) @Optional GaugeView winRateGauge;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;

    private LeaderboardUserDTO leaderboardUserDTO;

    //<editor-fold desc="Constructors">
    public UserStatisticView(Context context)
    {
        super(context);
    }

    public UserStatisticView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UserStatisticView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override public void display(LeaderboardUserDTO dto)
    {
        this.leaderboardUserDTO = dto;
        display();
    }

    protected void display()
    {
        if (leaderboardUserDTO != null)
        {
            // Statistic text view
            displayTradeCount();
            displayDaysHold();
            displayPositionsCount();
            showValueWithoutAnimation();
            showExpandAnimation();
        }
        else
        {
            clearExpandAnimation();
        }
    }

    private void clearExpandAnimation()
    {
        if (winRateGauge != null)
        {
            winRateGauge.clear();
        }
        if (performanceGauge != null)
        {
            performanceGauge.clear();
        }
        if (tradeConsistencyGauge != null)
        {
            tradeConsistencyGauge.clear();
        }
    }

    private void showExpandAnimation()
    {
        String digitsWinRatio =
                NumberDisplayUtils.formatWithRelevantDigits(leaderboardUserDTO.getWinRatio() * 100, 3);
        if (winRateGauge != null)
        {
            winRateGauge.setContentText(digitsWinRatio + "%");
            winRateGauge.setSubText(getContext().getString(R.string.leaderboard_win_ratio_title));
            winRateGauge.setAnimiationFlag(true);
            winRateGauge.setTargetValue((float) leaderboardUserDTO.getWinRatio() * 100);
        }

        if (performanceGauge != null)
        {
            performanceGauge.setTopText(getContext().getString(R.string.leaderboard_SP_500));
            performanceGauge.setSubText(
                    getContext().getString(R.string.leaderboard_performance_title));
            performanceGauge.setAnimiationFlag(true);
            performanceGauge.setDrawStartValue(50f);
            performanceGauge.setTargetValue((float) normalizePerformance());
        }

        if (tradeConsistencyGauge != null)
        {
            tradeConsistencyGauge.setSubText(
                    getContext().getString(R.string.leaderboard_consistency_title));
            tradeConsistencyGauge.setAnimiationFlag(true);
            tradeConsistencyGauge.setTargetValue((float) normalizeConsistency());
        }

        if (tradeCountTv != null)
        {
            tradeCountTv.startAnimation();
        }
        if (daysHoldTv != null)
        {
            daysHoldTv.startAnimation();
        }
        if (positionsCountTv != null)
        {
            positionsCountTv.startAnimation();
        }
    }

    private void showValueWithoutAnimation()
    {
        String digitsWinRatio =
                NumberDisplayUtils.formatWithRelevantDigits(leaderboardUserDTO.getWinRatio() * 100, 3);
        if (winRateGauge != null)
        {
            winRateGauge.setContentText(digitsWinRatio + "%");
            winRateGauge.setSubText(getContext().getString(R.string.leaderboard_win_ratio_title));
            winRateGauge.setAnimiationFlag(false);
            winRateGauge.setCurrentValue((float) leaderboardUserDTO.getWinRatio() * 100);
        }

        if (performanceGauge != null)
        {
            performanceGauge.setTopText(getContext().getString(R.string.leaderboard_SP_500));
            performanceGauge.setSubText(getContext().getString(R.string.leaderboard_performance_title));
            performanceGauge.setAnimiationFlag(false);
            performanceGauge.setDrawStartValue(50f);
            performanceGauge.setCurrentValue((float) normalizePerformance());
        }

        if (tradeConsistencyGauge != null)
        {
            tradeConsistencyGauge.setSubText(
                    getContext().getString(R.string.leaderboard_consistency_title));
            tradeConsistencyGauge.setAnimiationFlag(false);
            tradeConsistencyGauge.setCurrentValue((float) normalizeConsistency());
        }
        Timber.d("showValueWithoutAnimation normalizeConsistency %s", normalizeConsistency());

        if (tradeCountTv != null)
        {
            tradeCountTv.showText();
        }
        if (daysHoldTv != null)
        {
            daysHoldTv.showText();
        }
        if (positionsCountTv != null)
        {
            positionsCountTv.showText();
        }
    }

    private void displayPositionsCount()
    {
        if (positionsCountTv != null)
        {
            positionsCountTv.setEndValue(leaderboardUserDTO.numberOfPositionsInPeriod);
            positionsCountTv.setFractionDigits(0);
        }
    }

    private void displayDaysHold()
    {
        if (daysHoldTv != null)
        {
            daysHoldTv.setEndValue(leaderboardUserDTO.avgHoldingPeriodMins * 1.0f / (60 * 24));
            daysHoldTv.setFractionDigits(2);
        }
    }

    private void displayTradeCount()
    {
        if (tradeCountTv != null && leaderboardUserDTO.avgNumberOfTradesPerMonth != null)
        {
            tradeCountTv.setEndValue(leaderboardUserDTO.avgNumberOfTradesPerMonth.floatValue());
            tradeCountTv.setFractionDigits(2);
        }
    }

    private double normalizeConsistency()
    {
        try
        {
            Double minConsistency = LeaderboardUserDTO.MIN_CONSISTENCY;
            Double maxConsistency = getAvgConsistency();
            Double consistency = leaderboardUserDTO.getConsistency();
            consistency = (consistency < minConsistency) ? minConsistency : consistency;
            consistency = (consistency > maxConsistency) ? maxConsistency : consistency;

            double result =
                    100 * (consistency - minConsistency) / (maxConsistency - minConsistency);
            return result;
        }
        catch (Exception e)
        {
            Timber.e("normalizeConsistency", e);
        }
        return getAvgConsistency();
    }

    private double normalizePerformance()
    {
        try
        {
            Double v = leaderboardUserDTO.sharpeRatioInPeriodVsSP500;
            Double min = (double) -2;
            Double max = (double) 2;

            if (v > max)
            {
                v = max;
            }
            else if (v < min)
            {
                v = min;
            }
            double r = 100 * (v - min) / (max - min);
            Timber.d("normalizePerformance sharpeRatioInPeriodVsSP500 %s result %s", leaderboardUserDTO.sharpeRatioInPeriodVsSP500, r);

            return r;
        }
        catch (Exception e)
        {
            Timber.e("normalizePerformance", e);
        }
        return 0;
    }

    private Double getAvgConsistency()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            return userProfileDTO.mostSkilledLbmu.getAvgConsistency();
        }
        return LeaderboardUserDTO.MIN_CONSISTENCY;
    }
}
