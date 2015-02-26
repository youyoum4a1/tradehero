package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum MarketRegion
{
    OTHER(0, new String[]{}),
    AUSTRALIA(1, new String[]{"ASX", "NZX"}),
    SOUTH_EAST_ASIA(2, new String[]{"SGX", "JKT", "SET", "PSE", "MYX"}),
    EAST_ASIA(3, new String[]{"HKEX", "SHA", "SHE", "KDQ", "KRX", "TPE", "TSE"}),
    EUROPE(4, new String[]{"LSE", "PAR", "MLSE"}),
    NORTH_AMERICA(5, new String[]{"NYSE", "NASDAQ", "OTCBB", "TSX", "TSXV", "AMEX"}),
    INDIA(6, new String[]{"NSE", "BSE"}),
    ;

    public final int code;
    @Deprecated // Server should return this
    @NonNull public final String[] exchanges;

    MarketRegion(int code, @NonNull String[] exchanges)
    {
        this.code = code;
        this.exchanges = exchanges;
    }

    @SuppressWarnings("UnusedDeclaration")
    @NonNull @JsonCreator static MarketRegion create(int candidateCode)
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
