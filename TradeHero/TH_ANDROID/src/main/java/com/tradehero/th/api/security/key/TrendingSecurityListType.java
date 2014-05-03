package com.tradehero.th.api.security.key;


public class TrendingSecurityListType extends SecurityListType
{
    public static final String TAG = TrendingSecurityListType.class.getSimpleName();
    public static final String ALL_EXCHANGES = "allExchanges";

    public final String exchange;

    //<editor-fold desc="Constructor">
    protected TrendingSecurityListType(TrendingSecurityListType other)
    {
        super(other);
        this.exchange = other.exchange;
        validate();
    }

    public TrendingSecurityListType(String exchange, Integer page, Integer perPage)
    {
        super(page, perPage);
        this.exchange = exchange;
        validate();
    }

    public TrendingSecurityListType(String exchange, Integer page)
    {
        super(page);
        this.exchange = exchange;
        validate();
    }

    public TrendingSecurityListType(String exchange)
    {
        super();
        this.exchange = exchange;
        validate();
    }

    public TrendingSecurityListType(Integer page, Integer perPage)
    {
        this(ALL_EXCHANGES, page, perPage);
    }

    public TrendingSecurityListType(Integer page)
    {
        this(ALL_EXCHANGES, page);
    }

    public TrendingSecurityListType()
    {
        this(ALL_EXCHANGES);
    }
    //</editor-fold>

    private void validate()
    {
        if (exchange == null)
        {
            throw new NullPointerException("Null is not a valid Exchange");
        }
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (exchange == null ? 0 : exchange.hashCode());
    }

    @Override public boolean equals(SecurityListType other)
    {
        return TrendingSecurityListType.class.isInstance(other) && equals(TrendingSecurityListType.class.cast(other));
    }

    public boolean equals(TrendingSecurityListType other)
    {
        return super.equals(other) &&
                (exchange == null ? other.exchange == null : exchange.equals(other.exchange));
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(SecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }

        if (!TrendingSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return TrendingSecurityListType.class.getName().compareTo(another.getClass().getName());
        }

        return compareTo(TrendingSecurityListType.class.cast(another));
    }

    public int compareTo(TrendingSecurityListType trendingSecurityListType)
    {
        if (trendingSecurityListType == null)
        {
            return 1;
        }
        int exchangeCompare = exchange.compareTo(trendingSecurityListType.exchange);
        if (exchangeCompare != 0)
        {
            return exchangeCompare;
        }
        return super.compareTo(trendingSecurityListType);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "TrendingSecurityListType{" +
                "exchange='" + exchange + "'" +
                ", page=" + getPage() +
                ", perPage=" + perPage +
                '}';
    }
}
