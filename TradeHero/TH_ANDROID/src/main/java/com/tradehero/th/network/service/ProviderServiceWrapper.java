package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoListKey;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderSecurityListType;
import com.tradehero.th.api.competition.WarrantProviderSecurityListType;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOFactory;
import com.tradehero.th.api.security.TrendingSecurityListType;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by xavier on 1/16/14.
 */
@Singleton public class ProviderServiceWrapper
{
    public static final String TAG = ProviderServiceWrapper.class.getSimpleName();

    @Inject ProviderService providerService;
    @Inject SecurityCompactDTOFactory securityCompactDTOFactory;

    @Inject public ProviderServiceWrapper()
    {
        super();
    }

    //<editor-fold desc="Get Providers">
    public List<ProviderDTO> getProviders()
            throws RetrofitError
    {
        return this.providerService.getProviders();
    }

    public void getProviders(Callback<List<ProviderDTO>> callback)
    {
        this.providerService.getProviders(callback);
    }
    //</editor-fold>

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
        List<SecurityCompactDTO> returned;

        if (key.getPage() == null)
        {
            returned = this.providerService.getSecurities(key.getProviderId().key);
        }
        else if (key.perPage == null)
        {
            returned = this.providerService.getSecurities(key.getProviderId().key, key.getPage());
        }
        else
        {
            returned = this.providerService.getSecurities(key.getProviderId().key, key.getPage(), key.perPage);
        }

        return securityCompactDTOFactory.clonePerType(returned);
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
        List<SecurityCompactDTO> returned;

        if (key.getPage() == null)
        {
            returned = this.providerService.getWarrantUnderlyers(key.getProviderId().key);
        }
        else if (key.perPage == null)
        {
            returned = this.providerService.getWarrantUnderlyers(key.getProviderId().key, key.getPage());
        }
        else
        {
            returned = this.providerService.getWarrantUnderlyers(key.getProviderId().key, key.getPage(), key.perPage);
        }

        return securityCompactDTOFactory.clonePerType(returned);
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

    //<editor-fold desc="Get Help Videos">
    public List<HelpVideoDTO> getHelpVideos(HelpVideoListKey helpVideoListKey)
            throws RetrofitError
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId());
    }

    void getHelpVideos(HelpVideoListKey helpVideoListKey, Callback<List<HelpVideoDTO>> callback)
    {
        this.getHelpVideos(helpVideoListKey.getProviderId(), callback);
    }

    public List<HelpVideoDTO> getHelpVideos(ProviderId providerId)
            throws RetrofitError
    {
        return this.providerService.getHelpVideos(providerId.key);
    }

    void getHelpVideos(ProviderId providerId, Callback<List<HelpVideoDTO>> callback)
    {
        this.providerService.getHelpVideos(providerId.key, callback);
    }
    //</editor-fold>

    abstract public class CallbackCloneListSecurityCompactDTO implements Callback<List<SecurityCompactDTO>>
    {
        @Override public final void success(List<SecurityCompactDTO> securityCompactDTOs, Response response)
        {
            successCloned(securityCompactDTOFactory.clonePerType(securityCompactDTOs), response);
        }

        abstract public void successCloned(List<SecurityCompactDTO> securityCompactDTOs, Response response);
    }
}
