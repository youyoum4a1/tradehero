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

public enum NetWorthRange
{
    LESS15KUSD(R.string.net_worth_less_than_15_k_usd, "a"),
    FROM15KUSDTO40KUSD(R.string.net_worth_from_15_k_to_40_k_usd, "b"),
    FROM40KUSDTO70KUSD(R.string.net_worth_from_40_k_to_70_k_usd, "c"),
    FROM70KUSDTO100KUSD(R.string.net_worth_from_70_k_to_100_k_usd, "d"),
    FROM100KUSDTO500KUSD(R.string.net_worth_from_100_k_to_500_k_usd, "e"),
    MORETHAN500KUSD(R.string.net_worth_more_than_500_k_usd, "f"),;

    public static final Map<String, NetWorthRange> filedNetWorthRanges;

    @StringRes public final int dropDownText;
    @NonNull private final String fromServer;

    NetWorthRange(@StringRes int dropDownText, @NonNull String fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static NetWorthRange getNetWorthRange(@NonNull String fromServer)
    {
        NetWorthRange candidate = filedNetWorthRanges.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any NetWorthRange");
        }
        return candidate;
    }

    static
    {
        Map<String, NetWorthRange> map = new HashMap<>();
        for (NetWorthRange candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedNetWorthRanges = Collections.unmodifiableMap(map);
    }

    @NonNull public static List<String> createTexts(@NonNull Resources resources, @NonNull Collection<NetWorthRange> worthRanges)
    {
        List<String> created = new ArrayList<>();
        for (NetWorthRange worthRange : worthRanges)
        {
            created.add(resources.getString(worthRange.dropDownText));
        }
        return created;
    }
}
