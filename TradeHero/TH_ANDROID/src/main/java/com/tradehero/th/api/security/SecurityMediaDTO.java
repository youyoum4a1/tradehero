package com.tradehero.th.api.security;

public class SecurityMediaDTO
{
    public int securityId;
    public String exchange;
    public String symbol;
    public String yahooSymbol;
    public String url;

    public Double lastPrice;
    public Double risePercent;
    public String name;

    public boolean hasValidSecurityId()
    {
        return exchange != null && symbol != null;
    }

    public SecurityId createSecurityId()
    {
        return new SecurityId(exchange, symbol);
    }

    public String displaySecurityName()
    {
        return exchange + ":" + symbol;
    }

}
