package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xavier on 2/12/14.
 */
public class PerPagedFilteredLeaderboardKey extends PerPagedLeaderboardKey
{
    public static final String TAG = PerPagedFilteredLeaderboardKey.class.getSimpleName();

    public static final String BUNDLE_KEY_WIN_RATIO = PerPagedFilteredLeaderboardKey.class.getName() + ".winRatio";
    public static final String BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT = PerPagedFilteredLeaderboardKey.class.getName() + ".averageMonthlyTradeCount";
    public static final String BUNDLE_KEY_AVERAGE_HOLDING_DAYS = PerPagedFilteredLeaderboardKey.class.getName() + ".averageHoldingDays";
    public static final String BUNDLE_KEY_MIN_SHARPE_RATIO = PerPagedFilteredLeaderboardKey.class.getName() + ".minSharpeRatio";
    public static final String BUNDLE_KEY_MAX_POS_ROI_VOLATILITY = PerPagedFilteredLeaderboardKey.class.getName() + ".maxPosRoiVolatility";
    public static final String STRING_SET_LEFT_WIN_RATIO = "winRatio";
    public static final String STRING_SET_LEFT_AVERAGE_MONTHLY_TRADE_COUNT = "averageMonthlyTradeCount";
    public static final String STRING_SET_LEFT_AVERAGE_HOLDING_DAYS = "averageHoldingDays";
    public static final String STRING_SET_LEFT_MIN_SHARPE_RATIO = "minSharpeRatio";
    public static final String STRING_SET_LEFT_MAX_POS_ROI_VOLATILITY = "maxPosRoiVolatility";

    public final Float winRatio;
    public final Float averageMonthlyTradeCount;
    public final Float averageHoldingDays;
    public final Float minSharpeRatio;
    public final Float maxPosRoiVolatility;

    //<editor-fold desc="Constructors">
    public PerPagedFilteredLeaderboardKey(Integer leaderboardKey, Integer page, Integer perPage,
            Float winRatio, Float averageMonthlyTradeCount, Float averageHoldingDays, Float minSharpeRatio, Float maxPosRoiVolatility)
    {
        super(leaderboardKey, page, perPage);
        this.winRatio = winRatio;
        this.averageMonthlyTradeCount = averageMonthlyTradeCount;
        this.averageHoldingDays = averageHoldingDays;
        this.minSharpeRatio = minSharpeRatio;
        this.maxPosRoiVolatility = maxPosRoiVolatility;
    }

    public PerPagedFilteredLeaderboardKey(Integer leaderboardKey, Integer page, Integer perPage)
    {
        super(leaderboardKey, page, perPage);
        this.winRatio = null;
        this.averageMonthlyTradeCount = null;
        this.averageHoldingDays = null;
        this.minSharpeRatio = null;
        this.maxPosRoiVolatility = null;
    }

    public PerPagedFilteredLeaderboardKey(PerPagedFilteredLeaderboardKey other, Integer overrideKey, Integer page)
    {
        this(other, overrideKey, page, other.perPage);
    }

    public PerPagedFilteredLeaderboardKey(PerPagedFilteredLeaderboardKey other, Integer overrideKey, Integer page, Integer perPage)
    {
        super(overrideKey, page, perPage);
        this.winRatio = other.winRatio;
        this.averageMonthlyTradeCount = other.averageMonthlyTradeCount;
        this.averageHoldingDays = other.averageHoldingDays;
        this.minSharpeRatio = other.minSharpeRatio;
        this.maxPosRoiVolatility = other.maxPosRoiVolatility;
    }

    public PerPagedFilteredLeaderboardKey(Bundle args)
    {
        super(args);
        this.winRatio = args.containsKey(BUNDLE_KEY_WIN_RATIO) ? args.getFloat(BUNDLE_KEY_WIN_RATIO) : null;
        this.averageMonthlyTradeCount = args.containsKey(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) ? args.getFloat(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) : null;
        this.averageHoldingDays = args.containsKey(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) ? args.getFloat(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) : null;
        this.minSharpeRatio = args.containsKey(BUNDLE_KEY_MIN_SHARPE_RATIO) ? args.getFloat(BUNDLE_KEY_MIN_SHARPE_RATIO) : null;
        this.maxPosRoiVolatility = args.containsKey(BUNDLE_KEY_MAX_POS_ROI_VOLATILITY) ? args.getFloat(BUNDLE_KEY_MAX_POS_ROI_VOLATILITY) : null;
    }

    public PerPagedFilteredLeaderboardKey(Set<String> catValues)
    {
        super(catValues);
        Iterator<String> iterator = catValues.iterator();
        String catValue;
        Float tempWinRatio = null;
        Float tempAverageMonthlyTradeCount = null;
        Float tempAverageHoldingDays = null;
        Float tempMinSharpeRatio = null;
        Float tempMaxPosRoiVolatility = null;
        while (iterator.hasNext())
        {
            catValue = iterator.next();
            String[] split = catValue.split(STRING_SET_VALUE_SEPARATOR);
            Float value = Float.valueOf(split[1]);
            switch (split[0])
            {
                case STRING_SET_LEFT_WIN_RATIO:
                    tempWinRatio = value;
                    break;
                case STRING_SET_LEFT_AVERAGE_MONTHLY_TRADE_COUNT:
                    tempAverageMonthlyTradeCount = value;
                    break;
                case STRING_SET_LEFT_AVERAGE_HOLDING_DAYS:
                    tempAverageHoldingDays = value;
                    break;
                case STRING_SET_LEFT_MIN_SHARPE_RATIO:
                    tempMinSharpeRatio = value;
                    break;
                case STRING_SET_LEFT_MAX_POS_ROI_VOLATILITY:
                    tempMaxPosRoiVolatility = value;
                    break;
            }
        }
        winRatio = tempWinRatio;
        averageMonthlyTradeCount = tempAverageMonthlyTradeCount;
        averageHoldingDays = tempAverageHoldingDays;
        minSharpeRatio = tempMinSharpeRatio;
        maxPosRoiVolatility = tempMaxPosRoiVolatility;
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

    @Override public boolean equals(PerPagedLeaderboardKey other)
    {
        return super.equals(other) && other instanceof PerPagedFilteredLeaderboardKey &&
                equals((PerPagedFilteredLeaderboardKey) other);
    }

    public boolean equals(PerPagedFilteredLeaderboardKey other)
    {
        return other != null &&
                super.equals(other) &&
                (winRatio == null ? other.winRatio == null : winRatio.equals(other.winRatio)) &&
                (averageMonthlyTradeCount == null ? other.averageMonthlyTradeCount == null : averageMonthlyTradeCount.equals(other.averageMonthlyTradeCount)) &&
                (averageHoldingDays == null ? other.averageHoldingDays == null : averageHoldingDays.equals(other.averageHoldingDays)) &&
                (minSharpeRatio == null ? other.minSharpeRatio == null : minSharpeRatio.equals(other.minSharpeRatio)) &&
                (maxPosRoiVolatility == null ? other.maxPosRoiVolatility == null : maxPosRoiVolatility.equals(other.maxPosRoiVolatility));
    }

    public boolean areInnerValuesEqual(PerPagedFilteredLeaderboardKey other)
    {
        return other != null &&
                (winRatio == null ? other.winRatio == null : winRatio.equals(other.winRatio)) &&
                (averageMonthlyTradeCount == null ? other.averageMonthlyTradeCount == null : averageMonthlyTradeCount.equals(other.averageMonthlyTradeCount)) &&
                (averageHoldingDays == null ? other.averageHoldingDays == null : averageHoldingDays.equals(other.averageHoldingDays)) &&
                (minSharpeRatio == null ? other.minSharpeRatio == null : minSharpeRatio.equals(other.minSharpeRatio)) &&
                (maxPosRoiVolatility == null ? other.maxPosRoiVolatility == null : maxPosRoiVolatility.equals(other.maxPosRoiVolatility));
    }

    public int compareTo(PerPagedFilteredLeaderboardKey other)
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

        // TODO this is very confusing
        return (winRatio == null ? (other.winRatio == null ? 0 : 1) : winRatio.compareTo(other.winRatio)) *
                (averageMonthlyTradeCount == null ? (other.averageMonthlyTradeCount == null ? 0 : 1) : averageMonthlyTradeCount.compareTo(
                        other.averageMonthlyTradeCount)) *
                (averageHoldingDays == null ? (other.averageHoldingDays == null ? 0 : 1) : averageHoldingDays.compareTo(other.averageHoldingDays)) *
                (minSharpeRatio == null ? (other.minSharpeRatio == null ? 0 : 1) : minSharpeRatio.compareTo(other.minSharpeRatio)) *
                (maxPosRoiVolatility == null ? (other.maxPosRoiVolatility == null ? 0 : 1) : maxPosRoiVolatility.compareTo(other.maxPosRoiVolatility));
    }

    @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new PerPagedFilteredLeaderboardKey(this, key, page);
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

    @Override public void putParameters(Set<String> catValues)
    {
        super.putParameters(catValues);
        putWinRatio(catValues, this.winRatio);
        putAverageMonthlyTradeCount(catValues, this.averageMonthlyTradeCount);
        putAverageHoldingDays(catValues, this.averageHoldingDays);
        putMinSharpeRatio(catValues, this.minSharpeRatio);
        putMaxPosRoiVolatility(catValues, this.maxPosRoiVolatility);
    }

    public static void putWinRatio(Set<String> catValues, Float winRatio)
    {
        if (winRatio != null)
        {
            catValues.add(STRING_SET_LEFT_WIN_RATIO + STRING_SET_VALUE_SEPARATOR + winRatio);
        }
    }

    public static void putAverageMonthlyTradeCount(Set<String> catValues, Float averageMonthlyTradeCount)
    {
        if (averageMonthlyTradeCount != null)
        {
            catValues.add(STRING_SET_LEFT_AVERAGE_MONTHLY_TRADE_COUNT + STRING_SET_VALUE_SEPARATOR + averageMonthlyTradeCount);
        }
    }

    public static void putAverageHoldingDays(Set<String> catValues, Float averageHoldingDays)
    {
        if (averageHoldingDays != null)
        {
            catValues.add(STRING_SET_LEFT_AVERAGE_HOLDING_DAYS + STRING_SET_VALUE_SEPARATOR + averageHoldingDays);
        }
    }

    public static void putMinSharpeRatio(Set<String> catValues, Float minSharpeRatio)
    {
        if (minSharpeRatio != null)
        {
            catValues.add(STRING_SET_LEFT_MIN_SHARPE_RATIO + STRING_SET_VALUE_SEPARATOR + minSharpeRatio);
        }
    }

    public static void putMaxPosRoiVolatility(Set<String> catValues, Float maxPosRoiVolatility)
    {
        if (maxPosRoiVolatility != null)
        {
            catValues.add(STRING_SET_LEFT_MAX_POS_ROI_VOLATILITY + STRING_SET_VALUE_SEPARATOR + maxPosRoiVolatility);
        }
    }
}
