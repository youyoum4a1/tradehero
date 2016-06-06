package com.ayondo.academyapp.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum NetWorthRange
{
    EMPTY(R.string.net_worth_empty, -1),
    LESS15KUSD(R.string.net_worth_less_than_15_k_usd, 0),
    FROM15KUSDTO40KUSD(R.string.net_worth_from_15_k_to_40_k_usd, 15),
    FROM40KUSDTO70KUSD(R.string.net_worth_from_40_k_to_70_k_usd, 40),
    FROM70KUSDTO100KUSD(R.string.net_worth_from_70_k_to_100_k_usd, 70),
    FROM100KUSDTO500KUSD(R.string.net_worth_from_100_k_to_500_k_usd, 100),
    MORETHAN500KUSD(R.string.net_worth_more_than_500_k_usd, 101);

    private static final Map<Integer, NetWorthRange> filedNetWorthRanges;

    @StringRes public final int dropDownText;
    private final int fromServer;

    NetWorthRange(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static NetWorthRange getNetWorthRange(int fromServer)
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
        Map<Integer, NetWorthRange> map = new HashMap<>();
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

    @SuppressWarnings("unused")
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
