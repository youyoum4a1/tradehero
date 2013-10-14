package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:07 PM To change this template use File | Settings | File Templates. */
public class TrendingSecurityListType extends SecurityListType
{
    public static final String TAG = TrendingSecurityListType.class.getSimpleName();

    public static final String ALL_EXCHANGES = "allExchanges";

    private final String exchange;

    //<editor-fold desc="Constructor">
    public TrendingSecurityListType()
    {
        this.exchange = ALL_EXCHANGES;
    }

    public TrendingSecurityListType(String exchange)
    {
        if (exchange == null)
        {
            throw new NullPointerException("Null is not a valid Exchange");
        }
        this.exchange = exchange;
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
    public String getExchange()
    {
        return exchange;
    }
    //</editor-fold>

    @Override public int compareTo(SecurityListType securityListType)
    {
        if (securityListType == null)
        {
            return 1;
        }

        if (securityListType instanceof TrendingSecurityListType)
        {
            return compareTo((TrendingSecurityListType) securityListType);
        }

        // TODO is it very expensive?
        return TrendingSecurityListType.class.getName().compareTo(securityListType.getClass().getName());
    }

    public int compareTo(TrendingSecurityListType trendingSecurityListType)
    {
        return exchange.compareTo(trendingSecurityListType.exchange);
    }

    @Override public String makeKey()
    {
        return String.format("%s:%s", TrendingSecurityListType.class.getName(), getExchange());
    }

    @Override public String toString()
    {
        return String.format("[%s]", TAG);
    }
}
