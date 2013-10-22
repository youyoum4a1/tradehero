package com.tradehero.th.api.security;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 12:29 PM To change this template use File | Settings | File Templates. */
public class SecurityId implements Comparable, DTOKey
{
    public final static String BUNDLE_KEY_EXCHANGE = SecurityId.class.getName() + ".exchange";
    public final static String BUNDLE_KEY_SYMBOL = SecurityId.class.getName() + ".symbol";

    public final String exchange;
    public final String securitySymbol;

    //<editor-fold desc="Constructors">
    public SecurityId(final String exchange, final String securitySymbol)
    {
        this.exchange = exchange;
        this.securitySymbol = securitySymbol;
    }

    public SecurityId(Bundle args)
    {
        this.exchange = args.getString(BUNDLE_KEY_EXCHANGE);
        this.securitySymbol = args.getString(BUNDLE_KEY_SYMBOL);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (exchange == null ? 0 : exchange.hashCode()) ^
                (securitySymbol == null ? 0 : securitySymbol.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof SecurityId) && equals((SecurityId) other);
    }

    public boolean equals(SecurityId other)
    {
        return (other != null) &&
                (exchange == null ? other.exchange == null : exchange.equals(other.exchange)) &&
                (securitySymbol == null ? other.securitySymbol == null : securitySymbol.equals(other.securitySymbol));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == SecurityId.class)
        {
            return compareTo((SecurityId) o);
        }
        return o.getClass().getName().compareTo(SecurityId.class.getName());
    }

    public int compareTo(SecurityId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int exchangeComp = exchange.compareTo(other.exchange);
        if (exchangeComp != 0)
        {
            return exchangeComp;
        }

        return securitySymbol.compareTo(other.securitySymbol);
    }

    public boolean isValid()
    {
        return exchange != null && !exchange.isEmpty() && securitySymbol != null && !securitySymbol.isEmpty();
    }

    public static boolean isValid(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY_EXCHANGE) &&
                args.getString(BUNDLE_KEY_EXCHANGE) != null &&
                !args.getString(BUNDLE_KEY_EXCHANGE).isEmpty() &&
                args.getString(BUNDLE_KEY_SYMBOL) != null &&
                !args.getString(BUNDLE_KEY_SYMBOL).isEmpty();
    }

    public void putParameters(Bundle args)
    {
        args.putString(BUNDLE_KEY_EXCHANGE, exchange);
        args.putString(BUNDLE_KEY_SYMBOL, securitySymbol);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[exchange=%s; securitySymbol=%s]", exchange, securitySymbol);
    }
}
