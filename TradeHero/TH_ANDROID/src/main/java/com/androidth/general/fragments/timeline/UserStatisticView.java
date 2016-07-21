package com.androidth.general.fragments.timeline;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.BindView;
import android.support.annotation.Nullable;
import com.androidth.general.common.widget.GaugeView;
import com.androidth.general.common.widget.NumericalAnimatedTextView;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.models.number.THSignedNumber;
import timber.log.Timber;

public class UserStatisticView extends LinearLayout
    implements DTOView<UserStatisticView.DTO>
{
    @BindView(R.id.leaderboard_dayshold_tv) @Nullable NumericalAnimatedTextView daysHoldTv;
    @BindView(R.id.leaderboard_position_tv) @Nullable NumericalAnimatedTextView positionsCountTv;
    @BindView(R.id.leaderboard_tradecount_tv) @Nullable NumericalAnimatedTextView tradeCountTv;

    @BindView(R.id.leaderboard_gauge_performance)
    @Nullable GaugeView performanceGauge;
    @BindView(R.id.leaderboard_gauge_tradeconsistency)
    @Nullable GaugeView tradeConsistencyGauge;
    @BindView(R.id.leaderboard_gauge_winrate)
    @Nullable GaugeView winRateGauge;

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
        ButterKnife.bind(this);
    }

    @Override public void display(@Nullable DTO dto)
    {
        if (dto != null)
        {
            // Statistic text view
            displayTradeCount(dto);
            displayDaysHold(dto);
            displayPositionsCount(dto);
            showValueWithoutAnimation(dto);
            showExpandAnimation(dto);
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

    private void showExpandAnimation(@NonNull DTO dto)
    {
        if (winRateGauge != null)
        {
            winRateGauge.setContentText(dto.digitsWinRatio);
            winRateGauge.setSubText(dto.winRateGaugeSubText);
            winRateGauge.setAnimiationFlag(true);
            winRateGauge.setTargetValue(dto.winRateGaugeWinRatioValue);
        }

        if (performanceGauge != null)
        {
            performanceGauge.setTopText(dto.performanceGaugeTopText);
            performanceGauge.setSubText(dto.performanceGaugeSubText);
            performanceGauge.setAnimiationFlag(true);
            performanceGauge.setDrawStartValue(50f);
            performanceGauge.setTargetValue(dto.normalisedPerformance);
        }

        if (tradeConsistencyGauge != null)
        {
            tradeConsistencyGauge.setSubText(dto.tradeConsistencyGaugeSubText);
            tradeConsistencyGauge.setAnimiationFlag(true);
            tradeConsistencyGauge.setTargetValue(dto.normalisedConsistency);
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

    private void showValueWithoutAnimation(@NonNull DTO dto)
    {
        if (winRateGauge != null)
        {
            winRateGauge.setContentText(dto.digitsWinRatio);
            winRateGauge.setSubText(dto.winRateGaugeSubText);
            winRateGauge.setAnimiationFlag(false);
            winRateGauge.setCurrentValue(dto.winRateGaugeWinRatioValue);
        }

        if (performanceGauge != null)
        {
            performanceGauge.setTopText(dto.performanceGaugeTopText);
            performanceGauge.setSubText(dto.performanceGaugeSubText);
            performanceGauge.setAnimiationFlag(false);
            performanceGauge.setDrawStartValue(50f);
            performanceGauge.setCurrentValue(dto.normalisedPerformance);
        }

        if (tradeConsistencyGauge != null)
        {
            tradeConsistencyGauge.setSubText(dto.tradeConsistencyGaugeSubText);
            tradeConsistencyGauge.setAnimiationFlag(false);
            tradeConsistencyGauge.setCurrentValue(dto.normalisedConsistency);
        }
        Timber.d("showValueWithoutAnimation normalizeConsistency %s", dto.normalisedConsistency);

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

    private void displayPositionsCount(@NonNull DTO dto)
    {
        if (positionsCountTv != null)
        {
            positionsCountTv.setEndValue(dto.leaderboardUserDTO.numberOfPositionsInPeriod);
            positionsCountTv.setFractionDigits(0);
        }
    }

    private void displayDaysHold(@NonNull DTO dto)
    {
        if (daysHoldTv != null)
        {
            daysHoldTv.setEndValue(dto.daysHoldTvEndValue);
            daysHoldTv.setFractionDigits(2);
        }
    }

    private void displayTradeCount(@NonNull DTO dto)
    {
        if (tradeCountTv != null)
        {
            tradeCountTv.setEndValue(dto.tradeCountTvEndValue);
            tradeCountTv.setFractionDigits(2);
        }
    }

    public static class DTO
    {
        @NonNull final LeaderboardUserDTO leaderboardUserDTO;
        final float daysHoldTvEndValue;
        final float tradeCountTvEndValue;
        final String performanceGaugeTopText;
        final String performanceGaugeSubText;
        final float normalisedPerformance;
        final float normalisedConsistency;
        @NonNull final String digitsWinRatio;
        @NonNull String winRateGaugeSubText;
        final float winRateGaugeWinRatioValue;
        @NonNull final String tradeConsistencyGaugeSubText;

        public DTO(@NonNull Resources resources,
                @NonNull LeaderboardUserDTO leaderboardUserDTO,
                @Nullable LeaderboardDTO mostSkilledLeaderboardDTO)
        {
            this.leaderboardUserDTO = leaderboardUserDTO;
            this.daysHoldTvEndValue = leaderboardUserDTO.avgHoldingPeriodMins * 1.0f / (60 * 24);
            this.tradeCountTvEndValue = leaderboardUserDTO.avgNumberOfTradesPerMonth == null ? 0 : leaderboardUserDTO.avgNumberOfTradesPerMonth.floatValue();
            this.performanceGaugeTopText = resources.getString(R.string.leaderboard_SP_500);
            this.performanceGaugeSubText = resources.getString(R.string.leaderboard_performance_title);
            double r = 0;
            if (leaderboardUserDTO.sharpeRatioInPeriodVsSP500 != null)
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
                r = 100 * (v - min) / (max - min);
            }
            this.normalisedPerformance = (float) r;

            double avgConsistency = LeaderboardUserDTO.MIN_CONSISTENCY;
            if (mostSkilledLeaderboardDTO != null)
            {
                avgConsistency = mostSkilledLeaderboardDTO.getAvgConsistency();
            }
            double result;
            try
            {
                Double minConsistency = LeaderboardUserDTO.MIN_CONSISTENCY;
                Double maxConsistency = avgConsistency;
                Double consistency = leaderboardUserDTO.getConsistency();
                consistency = (consistency < minConsistency) ? minConsistency : consistency;
                consistency = (consistency > maxConsistency) ? maxConsistency : consistency;

                result = 100 * (consistency - minConsistency) / (maxConsistency - minConsistency);
            }
            catch (Exception e)
            {
                result = avgConsistency;
                Timber.e("normalizeConsistency", e);
            }
            this.normalisedConsistency = (float) result;

            digitsWinRatio = THSignedNumber.builder(leaderboardUserDTO.getWinRatio() * 100)
                    .relevantDigitCount(3)
                    .withOutSign()
                    .build().toString();
            winRateGaugeSubText = resources.getString(R.string.leaderboard_win_ratio_title);
            winRateGaugeWinRatioValue = (float) leaderboardUserDTO.getWinRatio() * 100;

            tradeConsistencyGaugeSubText = resources.getString(R.string.leaderboard_consistency_title);

        }
    }
}
