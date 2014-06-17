package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PerPagedFilteredLeaderboardKey extends PerPagedLeaderboardKey
{
    public static final String BUNDLE_KEY_WIN_RATIO = PerPagedFilteredLeaderboardKey.class.getName() + ".winRatio";
    public static final String BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT = PerPagedFilteredLeaderboardKey.class.getName() + ".averageMonthlyTradeCount";
    public static final String BUNDLE_KEY_AVERAGE_HOLDING_DAYS = PerPagedFilteredLeaderboardKey.class.getName() + ".averageHoldingDays";
    public static final String BUNDLE_KEY_MIN_SHARPE_RATIO = PerPagedFilteredLeaderboardKey.class.getName() + ".minSharpeRatio";
    public static final String BUNDLE_KEY_MIN_CONSISTENCY = PerPagedFilteredLeaderboardKey.class.getName() + ".minConsistency";
    public static final String STRING_SET_LEFT_WIN_RATIO = "winRatio";
    public static final String STRING_SET_LEFT_AVERAGE_MONTHLY_TRADE_COUNT = "averageMonthlyTradeCount";
    public static final String STRING_SET_LEFT_AVERAGE_HOLDING_DAYS = "averageHoldingDays";
    public static final String STRING_SET_LEFT_MIN_SHARPE_RATIO = "minSharpeRatio";
    public static final String STRING_SET_LEFT_MIN_CONSISTENCY = "minConsistency";

    public final Float winRatio;
    public final Float averageMonthlyTradeCount;
    public final Float averageHoldingDays;
    public final Float minSharpeRatio;
    public final Float minConsistency;

    @Contract("null -> null; !null -> !null")
    private static Float clampConsistency(@Nullable Float consistency)
    {
        if (consistency == null)
        {
            return null;
        }
        return Math.max((float) (double) LeaderboardUserDTO.MIN_CONSISTENCY, consistency);
    }

    //<editor-fold desc="Constructors">
    public PerPagedFilteredLeaderboardKey(Integer leaderboardKey, Integer page, Integer perPage,
            Float winRatio, Float averageMonthlyTradeCount, Float averageHoldingDays, Float minSharpeRatio, Float minConsistency)
    {
        super(leaderboardKey, page, perPage);
        this.winRatio = winRatio;
        this.averageMonthlyTradeCount = averageMonthlyTradeCount;
        this.averageHoldingDays = averageHoldingDays;
        this.minSharpeRatio = minSharpeRatio;
        this.minConsistency = clampConsistency(minConsistency);
    }

    public PerPagedFilteredLeaderboardKey(Integer leaderboardKey, Integer page, Integer perPage)
    {
        super(leaderboardKey, page, perPage);
        this.winRatio = null;
        this.averageMonthlyTradeCount = null;
        this.averageHoldingDays = null;
        this.minSharpeRatio = null;
        this.minConsistency = null;
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
        this.minConsistency = clampConsistency(other.minConsistency);
    }

    public PerPagedFilteredLeaderboardKey(@NotNull Bundle args, @Nullable PerPagedFilteredLeaderboardKey defaultValues)
    {
        super(args, defaultValues);
        this.winRatio = args.containsKey(BUNDLE_KEY_WIN_RATIO) ? (Float) args.getFloat(BUNDLE_KEY_WIN_RATIO) : ((defaultValues != null) ? defaultValues.winRatio : null);
        this.averageMonthlyTradeCount = args.containsKey(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) ? (Float) args.getFloat(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) : ((defaultValues != null) ? defaultValues.averageMonthlyTradeCount : null);
        this.averageHoldingDays = args.containsKey(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) ? (Float) args.getFloat(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) : ((defaultValues != null) ? defaultValues.averageHoldingDays : null);
        this.minSharpeRatio = args.containsKey(BUNDLE_KEY_MIN_SHARPE_RATIO) ? (Float) args.getFloat(BUNDLE_KEY_MIN_SHARPE_RATIO) : ((defaultValues != null) ? defaultValues.minSharpeRatio : null);
        this.minConsistency = clampConsistency(args.containsKey(BUNDLE_KEY_MIN_CONSISTENCY) ? (Float) args.getFloat(BUNDLE_KEY_MIN_CONSISTENCY)
                : ((defaultValues != null) ? defaultValues.minConsistency : null));
    }

    public PerPagedFilteredLeaderboardKey(@NotNull Set<String> catValues,  @Nullable PerPagedFilteredLeaderboardKey defaultValues)
    {
        super(catValues, defaultValues);
        Iterator<String> iterator = catValues.iterator();
        String catValue;
        Float tempWinRatio = null;
        Float tempAverageMonthlyTradeCount = null;
        Float tempAverageHoldingDays = null;
        Float tempMinSharpeRatio = null;
        Float tempConsistency = null;
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
                case STRING_SET_LEFT_MIN_CONSISTENCY:
                    tempConsistency = value;
                    break;
            }
        }
        if (defaultValues != null)
        {
            if (tempWinRatio == null)
            {
                tempWinRatio = defaultValues.winRatio;
            }
            if (tempAverageMonthlyTradeCount == null)
            {
                tempAverageMonthlyTradeCount = defaultValues.averageMonthlyTradeCount;
            }
            if (tempAverageHoldingDays == null)
            {
                tempAverageHoldingDays = defaultValues.averageHoldingDays;
            }
            if (tempMinSharpeRatio == null)
            {
                tempMinSharpeRatio = defaultValues.minSharpeRatio;
            }
            if (tempConsistency == null)
            {
                tempConsistency = defaultValues.minConsistency;
            }
        }
        winRatio = tempWinRatio;
        averageMonthlyTradeCount = tempAverageMonthlyTradeCount;
        averageHoldingDays = tempAverageHoldingDays;
        minSharpeRatio = tempMinSharpeRatio;
        minConsistency = clampConsistency(tempConsistency);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (winRatio == null ? 0 : winRatio.hashCode())
                ^ (averageMonthlyTradeCount == null ? 0 : averageMonthlyTradeCount.hashCode())
                ^ (averageHoldingDays == null ? 0 : averageHoldingDays.hashCode())
                ^ (minSharpeRatio == null ? 0 : minSharpeRatio.hashCode())
                ^ (minConsistency == null ? 0 : minConsistency.hashCode());
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
                (minConsistency == null ? other.minConsistency == null : minConsistency.equals(other.minConsistency));
    }

    public boolean areInnerValuesEqual(PerPagedFilteredLeaderboardKey other)
    {
        return other != null &&
                (winRatio == null ? other.winRatio == null : winRatio.equals(other.winRatio)) &&
                (averageMonthlyTradeCount == null ? other.averageMonthlyTradeCount == null : averageMonthlyTradeCount.equals(other.averageMonthlyTradeCount)) &&
                (averageHoldingDays == null ? other.averageHoldingDays == null : averageHoldingDays.equals(other.averageHoldingDays)) &&
                (minSharpeRatio == null ? other.minSharpeRatio == null : minSharpeRatio.equals(other.minSharpeRatio)) &&
                (minConsistency == null ? other.minConsistency == null : minConsistency.equals(other.minConsistency));
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
                (minConsistency == null ? (other.minConsistency == null ? 0 : 1) : minConsistency.compareTo(other.minConsistency));
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

        if (minConsistency == null)
        {
            args.remove(BUNDLE_KEY_MIN_CONSISTENCY);
        }
        else
        {
            args.putFloat(BUNDLE_KEY_MIN_CONSISTENCY, minConsistency);
        }
    }

    @Override public void putParameters(Set<String> catValues)
    {
        super.putParameters(catValues);
        putWinRatio(catValues, this.winRatio);
        putAverageMonthlyTradeCount(catValues, this.averageMonthlyTradeCount);
        putAverageHoldingDays(catValues, this.averageHoldingDays);
        putMinSharpeRatio(catValues, this.minSharpeRatio);
        putMinConsistency(catValues, this.minConsistency);
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

    public static void putMinConsistency(Set<String> catValues, Float minConsistency)
    {
        if (minConsistency != null)
        {
            catValues.add(STRING_SET_LEFT_MIN_CONSISTENCY + STRING_SET_VALUE_SEPARATOR + minConsistency);
        }
    }
}
