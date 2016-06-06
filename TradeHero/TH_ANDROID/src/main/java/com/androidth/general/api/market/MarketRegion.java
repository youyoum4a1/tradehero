package com.androidth.general.api.market;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum MarketRegion
{
    OTHER(0),
    AUSTRALIA(1),
    SOUTH_EAST_ASIA(2),
    EAST_ASIA(3),
    EUROPE(4),
    NORTH_AMERICA(5),
    INDIA(6),
    ;

    public final int code;

    MarketRegion(int code)
    {
        this.code = code;
    }

    @SuppressWarnings("UnusedDeclaration")
    @NonNull @JsonCreator public static MarketRegion create(int candidateCode)
    {
        for (MarketRegion candidate : values())
        {
            if (candidate.code == candidateCode)
            {
                return candidate;
            }
        }
        return OTHER;
    }
}
