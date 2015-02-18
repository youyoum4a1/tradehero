package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum MarketRegion
{
    OTHER("unknown", new String[]{}),
    NORTH_AMERICA("northAm", new String[]{"NYSE", "NASDAQ", "OTCBB", "TSX", "TSXV", "AMEX"}),
    EUROPE("eur", new String[]{"LSE", "PAR", "MLSE"}),
    INDIA("ind", new String[]{"NSE", "BSE"}),
    SOUTH_EAST_ASIA("sea", new String[]{"SGX", "JKT", "SET", "PSE", "MYX"}),
    EAST_ASIA("eastA", new String[]{"HKEX", "SHA", "SHE", "KDQ", "KRX", "TPE", "TSE"}),
    AUSTRALIA("austr", new String[]{"ASX", "NZX"}),
    ;

    @NonNull public final String code;
    @Deprecated // Server should return this
    @NonNull public final String[] exchanges;

    MarketRegion(@NonNull String code, @NonNull String[] exchanges)
    {
        this.code = code;
        this.exchanges = exchanges;
    }

    @SuppressWarnings("UnusedDeclaration")
    @NonNull @JsonCreator static MarketRegion create(@NonNull String candidateCode)
    {
        for (MarketRegion candidate : values())
        {
            if (candidate.code.equals(candidateCode))
            {
                return candidate;
            }
        }
        return OTHER;
    }
}
