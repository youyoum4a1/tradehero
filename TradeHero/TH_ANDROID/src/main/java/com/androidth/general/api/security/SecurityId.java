package com.androidth.general.api.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;
import com.tradehero.route.RouteProperty;
import com.androidth.general.api.portfolio.key.PortfolioCompactListKey;

@RouteProperty
public class SecurityId implements Comparable, PortfolioCompactListKey, DTO
{
    private final static String BUNDLE_KEY_EXCHANGE = SecurityId.class.getName() + ".exchange";
    private final static String BUNDLE_KEY_SYMBOL = SecurityId.class.getName() + ".symbol";
    private final static String BUNDLE_KEY_SECURITY_ID = SecurityId.class.getName() + ".securityId";
    private final static String BUNDLE_KEY_AYONDO_ID = SecurityId.class.getName() + ".id_ay";


    String exchange;
    String securitySymbol;
    int securityIdNumber;
    String ayondoId;

    //<editor-fold desc="Constructors">
    public SecurityId() {}

    public SecurityId(final String exchange, final String securitySymbol, final int securityIdNumber)
    {
        this.exchange = exchange;
        this.securitySymbol = securitySymbol;
        this.securityIdNumber = securityIdNumber;
    }
    public SecurityId(final String exchange, final String securitySymbol, final int securityIdNumber, final String ayondoId)
    {
        this.exchange = exchange;
        this.securitySymbol = securitySymbol;
        this.securityIdNumber = securityIdNumber;
        this.ayondoId = ayondoId;
    }


    public SecurityId(@NonNull Bundle args)
    {
        this.exchange = args.getString(BUNDLE_KEY_EXCHANGE);
        this.securitySymbol = args.getString(BUNDLE_KEY_SYMBOL);
        this.securityIdNumber = args.getInt(BUNDLE_KEY_SECURITY_ID);
        this.ayondoId = args.getString(BUNDLE_KEY_AYONDO_ID);
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

    public int getSecurityIdNumber() { return securityIdNumber; }

    public String getAyondoId() { return ayondoId;}

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

    @Override public boolean equals(@Nullable Object other)
    {
        if (other == null)
        {
            return false;
        }
        if (other == this)
        {
            return true;
        }
        return other.getClass().equals(SecurityId.class)
                && equalFields((SecurityId) other);
    }

    protected boolean equalFields(@NonNull SecurityId other)
    {
        return (exchange == null ? other.exchange == null : exchange.equals(other.exchange)) &&
                (securitySymbol == null ? other.securitySymbol == null : securitySymbol.equals(other.securitySymbol)) &&
                (securityIdNumber == 0 ? other.securityIdNumber == 0 : securityIdNumber==other.securityIdNumber);
    }

    @Override public int compareTo(@NonNull Object other)
    {
        if (other.getClass() == getClass())
        {
            return compareTo(getClass().cast(other));
        }
        return other.getClass().getName().compareTo(getClass().getName());
    }

    public int compareTo(@NonNull SecurityId other)
    {
        if (this == other)
        {
            return 0;
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
        return exchange != null && !exchange.isEmpty() && securitySymbol != null && !securitySymbol.isEmpty() && securityIdNumber != 0;
    }

    public static boolean isValid(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY_EXCHANGE) &&
                args.getString(BUNDLE_KEY_EXCHANGE) != null &&
                !args.getString(BUNDLE_KEY_EXCHANGE).isEmpty() &&
                args.getString(BUNDLE_KEY_SYMBOL) != null &&
                !args.getString(BUNDLE_KEY_SYMBOL).isEmpty() &&
                args.getInt(BUNDLE_KEY_SECURITY_ID, 0) !=0;
    }

    protected void putParameters(Bundle args)
    {
        args.putString(BUNDLE_KEY_EXCHANGE, exchange);
        args.putString(BUNDLE_KEY_SYMBOL, securitySymbol);
        args.putInt(BUNDLE_KEY_SECURITY_ID, securityIdNumber);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[SecurityId exchange=%s; securitySymbol=%s; securityIdNumber=%d]", exchange, securitySymbol, securityIdNumber);
    }
}
