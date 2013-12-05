package com.tradehero.th.network.service;

import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.security.TrendingAllSecurityListType;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.api.security.TrendingVolumeSecurityListType;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurpose queries
 * Created by xavier on 12/5/13.
 */
public class SecurityServiceUtil
{
    public static final String TAG = SecurityServiceUtil.class.getSimpleName();

    public static List<SecurityCompactDTO> getSecurities(SecurityService securityService, SecurityListType key)
        throws RetrofitError
    {
        if (key instanceof TrendingSecurityListType)
        {
            return getTrendingSecurities(securityService, (TrendingSecurityListType) key);
        }
        else if (key instanceof SearchSecurityListType)
        {
            return searchSecurities(securityService, (SearchSecurityListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public static void getSecurities(SecurityService securityService, SecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key instanceof TrendingSecurityListType)
        {
            getTrendingSecurities(securityService, (TrendingSecurityListType) key, callback);
        }
        else if (key instanceof SearchSecurityListType)
        {
            searchSecurities(securityService, (SearchSecurityListType) key, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public static List<SecurityCompactDTO> getTrendingSecurities(SecurityService securityService, TrendingSecurityListType key)
        throws RetrofitError
    {
        if (key instanceof TrendingBasicSecurityListType)
        {
            return getTrendingSecuritiesBasic(securityService, (TrendingBasicSecurityListType) key);
        }
        else if (key instanceof TrendingPriceSecurityListType)
        {
            return getTrendingSecuritiesByPrice(securityService, (TrendingPriceSecurityListType) key);
        }
        else if (key instanceof TrendingVolumeSecurityListType)
        {
            return getTrendingSecuritiesByVolume(securityService, (TrendingVolumeSecurityListType) key);
        }
        else if (key instanceof TrendingAllSecurityListType)
        {
            return getTrendingSecuritiesAllInExchange(securityService, (TrendingAllSecurityListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public static void getTrendingSecurities(SecurityService securityService, TrendingSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key instanceof TrendingBasicSecurityListType)
        {
            getTrendingSecuritiesBasic(securityService, (TrendingBasicSecurityListType) key, callback);
        }
        else if (key instanceof TrendingPriceSecurityListType)
        {
            getTrendingSecuritiesByPrice(securityService, (TrendingPriceSecurityListType) key, callback);
        }
        else if (key instanceof TrendingVolumeSecurityListType)
        {
            getTrendingSecuritiesByVolume(securityService, (TrendingVolumeSecurityListType) key, callback);
        }
        else if (key instanceof TrendingAllSecurityListType)
        {
            getTrendingSecuritiesAllInExchange(securityService, (TrendingAllSecurityListType) key, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public static List<SecurityCompactDTO> getTrendingSecuritiesBasic(SecurityService securityService, TrendingBasicSecurityListType key)
        throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                return securityService.getTrendingSecurities();
            }
            else if (key.perPage == null)
            {
                return securityService.getTrendingSecurities(key.page);
            }
            return securityService.getTrendingSecurities(key.page, key.perPage);
        }
        else if (key.page == null)
        {
            return securityService.getTrendingSecurities(key.exchange);
        }
        else if (key.perPage == null)
        {
            return securityService.getTrendingSecurities(key.exchange, key.page);
        }
        return securityService.getTrendingSecurities(key.exchange, key.page, key.perPage);
    }

    public static void getTrendingSecuritiesBasic(SecurityService securityService, TrendingBasicSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                securityService.getTrendingSecurities(callback);
            }
            else if (key.perPage == null)
            {
                securityService.getTrendingSecurities(key.page, callback);
            }
            else
            {
                securityService.getTrendingSecurities(key.page, key.perPage, callback);
            }
        }
        else if (key.page == null)
        {
            securityService.getTrendingSecurities(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            securityService.getTrendingSecurities(key.exchange, key.page, callback);
        }
        else
        {
            securityService.getTrendingSecurities(key.exchange, key.page, key.perPage, callback);
        }
    }

    public static List<SecurityCompactDTO> getTrendingSecuritiesByPrice(SecurityService securityService, TrendingPriceSecurityListType key)
            throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                return securityService.getTrendingSecuritiesByPrice();
            }
            else if (key.perPage == null)
            {
                return securityService.getTrendingSecuritiesByPrice(key.page);
            }
            return securityService.getTrendingSecuritiesByPrice(key.page, key.perPage);
        }
        else if (key.page == null)
        {
            return securityService.getTrendingSecuritiesByPrice(key.exchange);
        }
        else if (key.perPage == null)
        {
            return securityService.getTrendingSecuritiesByPrice(key.exchange, key.page);
        }
        return securityService.getTrendingSecuritiesByPrice(key.exchange, key.page, key.perPage);
    }

    public static void getTrendingSecuritiesByPrice(SecurityService securityService, TrendingPriceSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                securityService.getTrendingSecuritiesByPrice(callback);
            }
            else if (key.perPage == null)
            {
                securityService.getTrendingSecuritiesByPrice(key.page, callback);
            }
            else
            {
                securityService.getTrendingSecuritiesByPrice(key.page, key.perPage, callback);
            }
        }
        else if (key.page == null)
        {
            securityService.getTrendingSecuritiesByPrice(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            securityService.getTrendingSecuritiesByPrice(key.exchange, key.page, callback);
        }
        else
        {
            securityService.getTrendingSecuritiesByPrice(key.exchange, key.page, key.perPage, callback);
        }
    }

    public static List<SecurityCompactDTO> getTrendingSecuritiesByVolume(SecurityService securityService, TrendingVolumeSecurityListType key)
            throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                return securityService.getTrendingSecuritiesByVolume();
            }
            else if (key.perPage == null)
            {
                return securityService.getTrendingSecuritiesByVolume(key.page);
            }
            return securityService.getTrendingSecuritiesByVolume(key.page, key.perPage);
        }
        else if (key.page == null)
        {
            return securityService.getTrendingSecuritiesByVolume(key.exchange);
        }
        else if (key.perPage == null)
        {
            return securityService.getTrendingSecuritiesByVolume(key.exchange, key.page);
        }
        return securityService.getTrendingSecuritiesByVolume(key.exchange, key.page, key.perPage);
    }

    public static void getTrendingSecuritiesByVolume(SecurityService securityService, TrendingVolumeSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                securityService.getTrendingSecuritiesByVolume(callback);
            }
            else if (key.perPage == null)
            {
                securityService.getTrendingSecuritiesByVolume(key.page, callback);
            }
            else
            {
                securityService.getTrendingSecuritiesByVolume(key.page, key.perPage, callback);
            }
        }
        else if (key.page == null)
        {
            securityService.getTrendingSecuritiesByVolume(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            securityService.getTrendingSecuritiesByVolume(key.exchange, key.page, callback);
        }
        else
        {
            securityService.getTrendingSecuritiesByVolume(key.exchange, key.page, key.perPage, callback);
        }
    }

    public static List<SecurityCompactDTO> getTrendingSecuritiesAllInExchange(SecurityService securityService, TrendingAllSecurityListType key)
            throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                return securityService.getTrendingSecuritiesAllInExchange();
            }
            else if (key.perPage == null)
            {
                return securityService.getTrendingSecuritiesAllInExchange(key.page);
            }
            return securityService.getTrendingSecuritiesAllInExchange(key.page, key.perPage);
        }
        else if (key.page == null)
        {
            return securityService.getTrendingSecuritiesAllInExchange(key.exchange);
        }
        else if (key.perPage == null)
        {
            return securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.page);
        }
        return securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.page, key.perPage);
    }

    public static void getTrendingSecuritiesAllInExchange(SecurityService securityService, TrendingAllSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.page == null)
            {
                securityService.getTrendingSecuritiesAllInExchange(callback);
            }
            else if (key.perPage == null)
            {
                securityService.getTrendingSecuritiesAllInExchange(key.page, callback);
            }
            else
            {
                securityService.getTrendingSecuritiesAllInExchange(key.page, key.perPage, callback);
            }
        }
        else if (key.page == null)
        {
            securityService.getTrendingSecuritiesAllInExchange(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.page, callback);
        }
        else
        {
            securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.page, key.perPage, callback);
        }
    }

    public static List<SecurityCompactDTO> searchSecurities(SecurityService securityService, SearchSecurityListType key)
            throws RetrofitError
    {
        if (key.page == null)
        {
            return securityService.searchSecurities(key.searchString);
        }
        else if (key.perPage == null)
        {
            return securityService.searchSecurities(key.searchString, key.page);
        }
        return securityService.searchSecurities(key.searchString, key.page, key.perPage);
    }

    public static void searchSecurities(SecurityService securityService, SearchSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.page == null)
        {
            securityService.searchSecurities(key.searchString, callback);
        }
        else if (key.perPage == null)
        {
            securityService.searchSecurities(key.searchString, key.page, callback);
        }
        else
        {
            securityService.searchSecurities(key.searchString, key.page, key.perPage, callback);
        }
    }
}
