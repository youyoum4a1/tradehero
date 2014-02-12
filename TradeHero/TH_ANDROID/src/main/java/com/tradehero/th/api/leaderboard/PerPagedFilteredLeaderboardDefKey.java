package com.tradehero.th.api.leaderboard;

import android.os.Bundle;

/**
 * Created by xavier on 2/12/14.
 */
public class PerPagedFilteredLeaderboardDefKey extends PerPagedLeaderboardDefKey
{
    public static final String TAG = PerPagedFilteredLeaderboardDefKey.class.getSimpleName();

    public static final String BUNDLE_KEY_WIN_RATIO = PerPagedFilteredLeaderboardDefKey.class.getName() + ".winRatio";
    public static final String BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT = PerPagedFilteredLeaderboardDefKey.class.getName() + ".averageMonthlyTradeCount";
    public static final String BUNDLE_KEY_AVERAGE_HOLDING_DAYS = PerPagedFilteredLeaderboardDefKey.class.getName() + ".averageHoldingDays";
    public static final String BUNDLE_KEY_MIN_SHARPE_RATIO = PerPagedFilteredLeaderboardDefKey.class.getName() + ".minSharpeRatio";
    public static final String BUNDLE_KEY_MAX_POS_ROI_VOLATILITY = PerPagedFilteredLeaderboardDefKey.class.getName() + ".maxPosRoiVolatility";

    public final Float winRatio;
    public final Float averageMonthlyTradeCount;
    public final Float averageHoldingDays;
    public final Float minSharpeRatio;
    public final Float maxPosRoiVolatility;

    //<editor-fold desc="Constructors">
    public PerPagedFilteredLeaderboardDefKey(Integer leaderboardDefKey, int page, int perPage,
            Float winRatio, Float averageMonthlyTradeCount, Float averageHoldingDays, Float minSharpeRatio, Float maxPosRoiVolatility)
    {
        super(leaderboardDefKey, page, perPage);
        this.winRatio = winRatio;
        this.averageMonthlyTradeCount = averageMonthlyTradeCount;
        this.averageHoldingDays = averageHoldingDays;
        this.minSharpeRatio = minSharpeRatio;
        this.maxPosRoiVolatility = maxPosRoiVolatility;
    }

    public PerPagedFilteredLeaderboardDefKey(Integer leaderboardDefKey, int page, int perPage)
    {
        super(leaderboardDefKey, page, perPage);
        this.winRatio = null;
        this.averageMonthlyTradeCount = null;
        this.averageHoldingDays = null;
        this.minSharpeRatio = null;
        this.maxPosRoiVolatility = null;
    }

    public PerPagedFilteredLeaderboardDefKey(Bundle args)
    {
        super(args);
        this.winRatio = args.containsKey(BUNDLE_KEY_WIN_RATIO) ? args.getFloat(BUNDLE_KEY_WIN_RATIO) : null;
        this.averageMonthlyTradeCount = args.containsKey(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) ? args.getFloat(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) : null;
        this.averageHoldingDays = args.containsKey(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) ? args.getFloat(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) : null;
        this.minSharpeRatio = args.containsKey(BUNDLE_KEY_MIN_SHARPE_RATIO) ? args.getFloat(BUNDLE_KEY_MIN_SHARPE_RATIO) : null;
        this.maxPosRoiVolatility = args.containsKey(BUNDLE_KEY_MAX_POS_ROI_VOLATILITY) ? args.getFloat(BUNDLE_KEY_MAX_POS_ROI_VOLATILITY) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (winRatio == null ? 0 : winRatio.hashCode())
                ^ (averageMonthlyTradeCount == null ? 0 : averageMonthlyTradeCount.hashCode())
                ^ (averageHoldingDays == null ? 0 : averageHoldingDays.hashCode())
                ^ (minSharpeRatio == null ? 0 : minSharpeRatio.hashCode())
                ^ (maxPosRoiVolatility == null ? 0 : maxPosRoiVolatility.hashCode());
    }

    @Override public boolean equals(PerPagedLeaderboardDefKey other)
    {
        return super.equals(other) && other instanceof PerPagedFilteredLeaderboardDefKey &&
                equals((PerPagedFilteredLeaderboardDefKey) other);
    }

    public boolean equals(PerPagedFilteredLeaderboardDefKey other)
    {
        return other != null &&
                super.equals(other) &&
                (winRatio == null ? other.winRatio == null : winRatio.equals(other.winRatio)) &&
                (averageMonthlyTradeCount == null ? other.averageMonthlyTradeCount == null : averageMonthlyTradeCount.equals(other.averageMonthlyTradeCount)) &&
                (averageHoldingDays == null ? other.averageHoldingDays == null : averageHoldingDays.equals(other.averageHoldingDays)) &&
                (minSharpeRatio == null ? other.minSharpeRatio == null : minSharpeRatio.equals(other.minSharpeRatio)) &&
                (maxPosRoiVolatility == null ? other.maxPosRoiVolatility == null : maxPosRoiVolatility.equals(other.maxPosRoiVolatility));
    }

    public int compareTo(PerPagedFilteredLeaderboardDefKey other)
    {
        // It looks like it does not compare well with all the subclasses
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        return (winRatio == null ? (other.winRatio == null ? 0 : 1) : winRatio.compareTo(other.winRatio)) *
                (averageMonthlyTradeCount == null ? (other.averageMonthlyTradeCount == null ? 0 : 1) : averageMonthlyTradeCount.compareTo(other.averageMonthlyTradeCount)) *
                (averageHoldingDays == null ? (other.averageHoldingDays == null ? 0 : 1) : averageHoldingDays.compareTo(other.averageHoldingDays)) *
                (minSharpeRatio == null ? (other.minSharpeRatio == null ? 0 : 1) : minSharpeRatio.compareTo(other.minSharpeRatio)) *
                (maxPosRoiVolatility == null ? (other.maxPosRoiVolatility == null ? 0 : 1) : maxPosRoiVolatility.compareTo(other.maxPosRoiVolatility));
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (winRatio == null)
        {
            args.remove(BUNDLE_KEY_WIN_RATIO);
        }
        else
        {
            args.putFloat(BUNDLE_KEY_WIN_RATIO, winRatio);
        }

        if (averageMonthlyTradeCount == null)
        {
            args.remove(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT);
        }
        else
        {
            args.putFloat(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT, averageMonthlyTradeCount);
        }

        if (averageHoldingDays == null)
        {
            args.remove(BUNDLE_KEY_AVERAGE_HOLDING_DAYS);
        }
        else
        {
            args.putFloat(BUNDLE_KEY_AVERAGE_HOLDING_DAYS, averageHoldingDays);
        }

        if (minSharpeRatio == null)
        {
            args.remove(BUNDLE_KEY_MIN_SHARPE_RATIO);
        }
        else
        {
            args.putFloat(BUNDLE_KEY_MIN_SHARPE_RATIO, minSharpeRatio);
        }

        if (maxPosRoiVolatility == null)
        {
            args.remove(BUNDLE_KEY_MAX_POS_ROI_VOLATILITY);
        }
        else
        {
            args.putFloat(BUNDLE_KEY_MAX_POS_ROI_VOLATILITY, maxPosRoiVolatility);
        }
    }
}
