package com.androidth.general.api.kyc;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.androidth.general.R;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TradingPerQuarter
{
    EMPTY(R.string.trading_per_quarter_empty, -1),
    NONE(R.string.trading_per_quarter_none, 1),
    ONE_TO_FIVE(R.string.trading_per_quarter_1_to_5, 2),
    SIX_TO_TEN(R.string.trading_per_quarter_6_to_10, 3),
    OVER_TEN(R.string.trading_per_quarter_over_10, 4);

    private static final Map<Integer, TradingPerQuarter> filedTradingPerQuarters;

    @StringRes public final int dropDownText;
    private final int fromServer;

    TradingPerQuarter(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static TradingPerQuarter getTradingPerQuarter(int fromServer)
    {
        TradingPerQuarter candidate = filedTradingPerQuarters.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any TradingPerQuarter");
        }
        return candidate;
    }

    static
    {
        Map<Integer, TradingPerQuarter> map = new HashMap<>();
        for (TradingPerQuarter candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedTradingPerQuarters = Collections.unmodifiableMap(map);
    }

    @NonNull public static List<String> createTexts(@NonNull Resources resources, @NonNull Collection<TradingPerQuarter> tradingPerQuarters)
    {
        List<String> created = new ArrayList<>();
        for (TradingPerQuarter tradingPerQuarter : tradingPerQuarters)
        {
            created.add(resources.getString(tradingPerQuarter.dropDownText));
        }
        return created;
    }

    @SuppressWarnings("unused")
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
