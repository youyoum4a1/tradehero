package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.ProviderSecurityListType;
import com.tradehero.th.api.competition.WarrantProviderSecurityListType;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.TrendingSecurityListType;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by xavier on 1/16/14.
 */
@Singleton public class ProviderServiceWrapper
{
    public static final String TAG = ProviderServiceWrapper.class.getSimpleName();

    @Inject ProviderService providerService;

    @Inject public ProviderServiceWrapper()
    {
        super();
    }

    //<editor-fold desc="Get Provider Securities">
    public List<SecurityCompactDTO> getProviderSecurities(ProviderSecurityListType key)
            throws RetrofitError
    {
        if (key instanceof BasicProviderSecurityListType)
        {
            return getProviderBasicSecurities((BasicProviderSecurityListType) key);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            return getWarrantUnderlyers((WarrantProviderSecurityListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public void getProviderSecurities(ProviderSecurityListType key, Callback<List<SecurityCompactDTO>> callback)
    {
        if (key instanceof BasicProviderSecurityListType)
        {
            getProviderBasicSecurities((BasicProviderSecurityListType) key, callback);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            getWarrantUnderlyers((WarrantProviderSecurityListType) key, callback);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get Provider Basic Securities">
    public List<SecurityCompactDTO> getProviderBasicSecurities(BasicProviderSecurityListType key)
            throws RetrofitError
    {
        if (key.getPage() == null)
        {
            return this.providerService.getSecurities(key.getProviderId().key);
        }
        else if (key.perPage == null)
        {
            return this.providerService.getSecurities(key.getProviderId().key, key.getPage());
        }
        return this.providerService.getSecurities(key.getProviderId().key, key.getPage(), key.perPage);
    }

    public void getProviderBasicSecurities(BasicProviderSecurityListType key, Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.getPage() == null)
        {
            this.providerService.getSecurities(key.getProviderId().key, callback);
        }
        else if (key.perPage == null)
        {
            this.providerService.getSecurities(key.getProviderId().key, key.getPage(), callback);
        }
        else
        {
            this.providerService.getSecurities(key.getProviderId().key, key.getPage(), key.perPage, callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get Provider Warrants Securities">
    public List<SecurityCompactDTO> getWarrantUnderlyers(WarrantProviderSecurityListType key)
            throws RetrofitError
    {
        if (key.getPage() == null)
        {
            return this.providerService.getWarrantUnderlyers(key.getProviderId().key);
        }
        else if (key.perPage == null)
        {
            return this.providerService.getWarrantUnderlyers(key.getProviderId().key, key.getPage());
        }
        return this.providerService.getWarrantUnderlyers(key.getProviderId().key, key.getPage(), key.perPage);
    }

    public void getWarrantUnderlyers(WarrantProviderSecurityListType key, Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.getPage() == null)
        {
            this.providerService.getWarrantUnderlyers(key.getProviderId().key, callback);
        }
        else if (key.perPage == null)
        {
            this.providerService.getWarrantUnderlyers(key.getProviderId().key, key.getPage(), callback);
        }
        else
        {
            this.providerService.getWarrantUnderlyers(key.getProviderId().key, key.getPage(), key.perPage, callback);
        }
    }
    //</editor-fold>
}
