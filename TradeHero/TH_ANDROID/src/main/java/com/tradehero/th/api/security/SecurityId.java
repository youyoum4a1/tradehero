package com.tradehero.th.api.security;

import android.os.Bundle;
import com.thoj.route.RouteProperty;
import com.tradehero.common.billing.googleplay.Security;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOKey;

@RouteProperty
public class SecurityId implements Comparable, DTOKey, DTO
{
    private final static String BUNDLE_KEY_EXCHANGE = SecurityId.class.getName() + ".exchange";
    private final static String BUNDLE_KEY_SYMBOL = SecurityId.class.getName() + ".symbol";

    String exchange;

    String securitySymbol;

    //<editor-fold desc="Constructors">
    public SecurityId() {}

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

    public String getSecuritySymbol()
    {
        return securitySymbol;
    }

    public String getExchange()
    {
        return exchange;
    }

    /**
     * Parse security raw info, follow this form: id_exchangeName_securitySymbol into securityId
     * @param securityRawInfo
     */
    @RouteProperty("securityRawInfo")
    public void setSecurityRawInfo(String securityRawInfo)
    {
        if (securityRawInfo != null && securityRawInfo.length() > 0)
        {
            String[] parts = securityRawInfo.split("_");
            if (parts.length >= 2)
            {
                securitySymbol = parts[parts.length-1].trim();
                exchange = parts[parts.length-2].trim();
            }
        }
    }

    // When passing the symbol in an API path as the last element of the path,
    // you have to use this instead of the symbol.
    public String getPathSafeSymbol()
    {
        if (securitySymbol == null)
        {
            return null;
        }
        return securitySymbol.replace('.', '_');
    }

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

    @Override public int compareTo(Object other)
    {
        if (other == null)
        {
            return 1;
        }

        if (other.getClass() == getClass())
        {
            return compareTo(getClass().cast(other));
        }
        return other.getClass().getName().compareTo(getClass().getName());
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

    protected void putParameters(Bundle args)
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
