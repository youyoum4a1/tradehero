package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.competition.key.WarrantProviderSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOFactory;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.security.DTOProcessorSecurityCompactListReceived;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

@Singleton public class ProviderServiceWrapper
{
    private final ProviderService providerService;
    private final ProviderServiceAsync providerServiceAsync;
    private final SecurityCompactDTOFactory securityCompactDTOFactory;

    @Inject public ProviderServiceWrapper(
            ProviderService providerService,
            ProviderServiceAsync providerServiceAsync,
            SecurityCompactDTOFactory securityCompactDTOFactory)
    {
        super();
        this.providerService = providerService;
        this.providerServiceAsync = providerServiceAsync;
        this.securityCompactDTOFactory = securityCompactDTOFactory;
    }

    protected DTOProcessor<List<SecurityCompactDTO>> createSecurityCompactListReceivedDTOProcessor()
    {
        return new DTOProcessorSecurityCompactListReceived(securityCompactDTOFactory);
    }

    //<editor-fold desc="Get Providers">
    public List<ProviderDTO> getProviders()
    {
        return this.providerService.getProviders();
    }

    public MiddleCallback<List<ProviderDTO>> getProviders(Callback<List<ProviderDTO>> callback)
    {
        MiddleCallback<List<ProviderDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        this.providerServiceAsync.getProviders(middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    public List<SecurityCompactDTO> getProviderSecurities(ProviderSecurityListType key)
    {
        List<SecurityCompactDTO> received;
        if (key instanceof BasicProviderSecurityListType)
        {
            received = this.providerService.getSecurities(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            received = this.providerService.getWarrantUnderlyers(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        return createSecurityCompactListReceivedDTOProcessor().process(received);
    }

    public MiddleCallback<List<SecurityCompactDTO>> getProviderSecurities(ProviderSecurityListType key, Callback<List<SecurityCompactDTO>> callback)
    {
        MiddleCallback<List<SecurityCompactDTO>> middleCallback = new BaseMiddleCallback<>(callback, createSecurityCompactListReceivedDTOProcessor());
        if (key instanceof BasicProviderSecurityListType)
        {
            this.providerServiceAsync.getSecurities(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage,
                    middleCallback);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            this.providerServiceAsync.getWarrantUnderlyers(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage,
                    middleCallback);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    public List<HelpVideoDTO> getHelpVideos(HelpVideoListKey helpVideoListKey)
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId());
    }

    public MiddleCallback<List<HelpVideoDTO>> getHelpVideos(HelpVideoListKey helpVideoListKey, Callback<List<HelpVideoDTO>> callback)
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId(), callback);
    }

    public List<HelpVideoDTO> getHelpVideos(ProviderId providerId)
    {
        return this.providerService.getHelpVideos(providerId.key);
    }

    public MiddleCallback<List<HelpVideoDTO>> getHelpVideos(ProviderId providerId, Callback<List<HelpVideoDTO>> callback)
    {
        MiddleCallback<List<HelpVideoDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        this.providerServiceAsync.getHelpVideos(providerId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
