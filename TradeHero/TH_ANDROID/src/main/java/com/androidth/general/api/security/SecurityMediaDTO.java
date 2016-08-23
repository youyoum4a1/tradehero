package com.androidth.general.api.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.misc.MediaDTO;

public class SecurityMediaDTO extends MediaDTO
{
    public int securityId;
    @Nullable public String exchange;
    @Nullable public String symbol;
    public String yahooSymbol;
    public String url;
    public Double risePercent;
    public Double lastPrice;
    public String name;

    public boolean hasValidSecurityId()
    {
        return exchange != null && symbol != null;
    }

    @NonNull public SecurityId createSecurityId()
    {
        return new SecurityId(exchange, symbol, securityId);
    }
}
