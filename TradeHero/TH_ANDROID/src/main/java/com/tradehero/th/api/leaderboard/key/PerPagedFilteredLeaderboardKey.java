package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import java.util.Iterator;
import java.util.Set;

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

    @Nullable public final Float winRatio;
    @Nullable public final Float averageMonthlyTradeCount;
    @Nullable public final Float averageHoldingDays;
    @Nullable public final Float minSharpeRatio;
    @Nullable public final Float minConsistency;

    @Nullable private static Float clampConsistency(@Nullable Float consistency)
    {
        if (consistency == null)
        {
            return null;
        }
        return Math.max((float) (double) LeaderboardUserDTO.MIN_CONSISTENCY, consistency);
    }

    //<editor-fold desc="Constructors">
    public PerPagedFilteredLeaderboardKey(
            @NonNull Integer leaderboardKey,
            @Nullable Integer page,
            @Nullable Integer perPage,
            @Nullable Float winRatio,
            @Nullable Float averageMonthlyTradeCount,
            @Nullable Float averageHoldingDays,
            @Nullable Float minSharpeRatio,
            @Nullable Float minConsistency)
    {
        super(leaderboardKey, page, perPage);
        this.winRatio = winRatio;
        this.averageMonthlyTradeCount = averageMonthlyTradeCount;
        this.averageHoldingDays = averageHoldingDays;
        this.minSharpeRatio = minSharpeRatio;
        this.minConsistency = clampConsistency(minConsistency);
    }

    public PerPagedFilteredLeaderboardKey(
            @NonNull PerPagedFilteredLeaderboardKey other,
            @NonNull Integer overrideKey,
            @Nullable Integer page)
    {
        this(other, overrideKey, page, other.perPage);
        setAssetClass(other.getAssetClass());
    }

    public PerPagedFilteredLeaderboardKey(
            @NonNull PerPagedFilteredLeaderboardKey other,
            @NonNull Integer overrideKey,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(overrideKey, page, perPage);
        this.winRatio = other.winRatio;
        this.averageMonthlyTradeCount = other.averageMonthlyTradeCount;
        this.averageHoldingDays = other.averageHoldingDays;
        this.minSharpeRatio = other.minSharpeRatio;
        this.minConsistency = clampConsistency(other.minConsistency);
    }

    public PerPagedFilteredLeaderboardKey(@NonNull Bundle args, @Nullable PerPagedFilteredLeaderboardKey defaultValues)
    {
        super(args, defaultValues);
        this.winRatio = args.containsKey(BUNDLE_KEY_WIN_RATIO) ? (Float) args.getFloat(BUNDLE_KEY_WIN_RATIO) : ((defaultValues != null) ? defaultValues.winRatio : null);
        this.averageMonthlyTradeCount = args.containsKey(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) ? (Float) args.getFloat(BUNDLE_KEY_AVERAGE_MONTHLY_TRADE_COUNT) : ((defaultValues != null) ? defaultValues.averageMonthlyTradeCount : null);
        this.averageHoldingDays = args.containsKey(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) ? (Float) args.getFloat(BUNDLE_KEY_AVERAGE_HOLDING_DAYS) : ((defaultValues != null) ? defaultValues.averageHoldingDays : null);
        this.minSharpeRatio = args.containsKey(BUNDLE_KEY_MIN_SHARPE_RATIO) ? (Float) args.getFloat(BUNDLE_KEY_MIN_SHARPE_RATIO) : ((defaultValues != null) ? defaultValues.minSharpeRatio : null);
        this.minConsistency = clampConsistency(args.containsKey(BUNDLE_KEY_MIN_CONSISTENCY) ? args.getFloat(BUNDLE_KEY_MIN_CONSISTENCY)
                : ((defaultValues != null) ? defaultValues.minConsistency : null));
    }

    public PerPagedFilteredLeaderboardKey(@NonNull Set<String> catValues,  @Nullable PerPagedFilteredLeaderboardKey defaultValues)
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

    @Override public boolean equalFields(@NonNull PerPagedLeaderboardKey other)
    {
        return super.equalFields(other)
                && other instanceof PerPagedFilteredLeaderboardKey
                && equalFields((PerPagedFilteredLeaderboardKey) other);
    }

    public boolean equalFields(@NonNull PerPagedFilteredLeaderboardKey other)
    {
        return super.equalFields(other) &&
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

    @NonNull @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new PerPagedFilteredLeaderboardKey(this, id, page);
    }

    @Override public void putParameters(@NonNull Bundle args)
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

    @Override public void putParameters(@NonNull Set<String> catValues)
    {
        super.putParameters(catValues);
        putWinRatio(catValues, this.winRatio);
        putAverageMonthlyTradeCount(catValues, this.averageMonthlyTradeCount);
        putAverageHoldingDays(catValues, this.averageHoldingDays);
        putMinSharpeRatio(catValues, this.minSharpeRatio);
        putMinConsistency(catValues, this.minConsistency);
    }

    public static void putWinRatio(@NonNull Set<String> catValues, @NonNull Float winRatio)
    {
        if (winRatio != null)
        {
            catValues.add(STRING_SET_LEFT_WIN_RATIO + STRING_SET_VALUE_SEPARATOR + winRatio);
        }
    }

    public static void putAverageMonthlyTradeCount(@NonNull Set<String> catValues, @Nullable Float averageMonthlyTradeCount)
    {
        if (averageMonthlyTradeCount != null)
        {
            catValues.add(STRING_SET_LEFT_AVERAGE_MONTHLY_TRADE_COUNT + STRING_SET_VALUE_SEPARATOR + averageMonthlyTradeCount);
        }
    }

    public static void putAverageHoldingDays(@NonNull Set<String> catValues, @Nullable Float averageHoldingDays)
    {
        if (averageHoldingDays != null)
        {
            catValues.add(STRING_SET_LEFT_AVERAGE_HOLDING_DAYS + STRING_SET_VALUE_SEPARATOR + averageHoldingDays);
        }
    }

    public static void putMinSharpeRatio(@NonNull Set<String> catValues, @Nullable Float minSharpeRatio)
    {
        if (minSharpeRatio != null)
        {
            catValues.add(STRING_SET_LEFT_MIN_SHARPE_RATIO + STRING_SET_VALUE_SEPARATOR + minSharpeRatio);
        }
    }

    public static void putMinConsistency(@NonNull Set<String> catValues, @Nullable Float minConsistency)
    {
        if (minConsistency != null)
        {
            catValues.add(STRING_SET_LEFT_MIN_CONSISTENCY + STRING_SET_VALUE_SEPARATOR + minConsistency);
        }
    }
}
