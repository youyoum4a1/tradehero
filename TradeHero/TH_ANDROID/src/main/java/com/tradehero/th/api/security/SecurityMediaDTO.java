package com.tradehero.th.api.security;

import com.tradehero.th.api.misc.MediaDTO;

public class SecurityMediaDTO extends MediaDTO
{
    public int securityId;
    public String exchange;
    public String symbol;
    public String yahooSymbol;
    public String url;

    public boolean hasValidSecurityId()
    {
        return exchange != null && symbol != null;
    }

    public SecurityId createSecurityId()
    {
        return new SecurityId(exchange, symbol);
    }
}
