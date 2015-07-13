package com.tradehero.th.api.kyc;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PercentNetWorthForInvestmentRange
{
    LESSTHAN25P(R.string.percent_net_worth_invest_less_than_25_p, "a"),
    FROM25PTO50P(R.string.percent_net_worth_invest_from_25_p_to_50_p, "b"),
    FROM51PTO75P(R.string.percent_net_worth_invest_from_51_p_to_75_p, "c"),
    MORETHAN75P(R.string.percent_net_worth_invest_from_more_than_75p, "d"),;

    public static final Map<String, PercentNetWorthForInvestmentRange> filedPercentNetWorthRanges;

    @StringRes public final int dropDownText;
    @NonNull private final String fromServer;

    PercentNetWorthForInvestmentRange(@StringRes int dropDownText, @NonNull String fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static PercentNetWorthForInvestmentRange getPercentNetWorthRange(@NonNull String fromServer)
    {
        PercentNetWorthForInvestmentRange candidate = filedPercentNetWorthRanges.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any PercentNetWorthForInvestmentRange");
        }
        return candidate;
    }

    static
    {
        Map<String, PercentNetWorthForInvestmentRange> map = new HashMap<>();
        for (PercentNetWorthForInvestmentRange candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedPercentNetWorthRanges = Collections.unmodifiableMap(map);
    }

    @NonNull public static List<String> createTexts(@NonNull Resources resources, @NonNull Collection<PercentNetWorthForInvestmentRange> percentWorthRanges)
    {
        List<String> created = new ArrayList<>();
        for (PercentNetWorthForInvestmentRange percentWorthRange : percentWorthRanges)
        {
            created.add(resources.getString(percentWorthRange.dropDownText));
        }
        return created;
    }
}
