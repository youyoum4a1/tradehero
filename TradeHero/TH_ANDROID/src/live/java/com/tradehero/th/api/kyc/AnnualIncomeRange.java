package com.tradehero.th.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AnnualIncomeRange
{
    EMPTY(R.string.annual_income_empty, -1),
    LESS15KUSD(R.string.annual_income_less_than_15_k_usd, 0),
    FROM15KUSDTO40KUSD(R.string.annual_income_from_15_k_to_40_k_usd, 15),
    FROM40KUSDTO70KUSD(R.string.annual_income_from_40_k_to_70_k_usd, 40),
    FROM70KUSDTO100KUSD(R.string.annual_income_from_70_k_to_100_k_usd, 70),
    MORETHAN100KUSD(R.string.annual_income_more_than_100_k_usd, 100),;

    private static final Map<Integer, AnnualIncomeRange> filedAnnualIncomeRanges;

    @StringRes public final int dropDownText;
    private final int fromServer;

    AnnualIncomeRange(@StringRes int dropDownText, int fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static AnnualIncomeRange getAnnualIncomeRange(int fromServer)
    {
        AnnualIncomeRange candidate = filedAnnualIncomeRanges.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any AnnualIncomeRange");
        }
        return candidate;
    }

    static
    {
        Map<Integer, AnnualIncomeRange> map = new HashMap<>();
        for (AnnualIncomeRange candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedAnnualIncomeRanges = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue int getFromServerCode()
    {
        return fromServer;
    }
}
