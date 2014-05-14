package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class QuoteServiceWrapper
{
    private final QuoteService quoteService;
    private final QuoteServiceAsync quoteServiceAsync;

    @Inject public QuoteServiceWrapper(
            QuoteService quoteService,
            QuoteServiceAsync quoteServiceAsync)
    {
        super();
        this.quoteService = quoteService;
        this.quoteServiceAsync = quoteServiceAsync;
    }

    private void basicCheck(SecurityId securityId)
    {
        if (securityId == null)
        {
            throw new NullPointerException("securityId cannot be null");
        }
        if (securityId.exchange == null)
        {
            throw new NullPointerException("securityId.exchange cannot be null");
        }
        if (securityId.securitySymbol == null)
        {
            throw new NullPointerException("securityId.securitySymbol cannot be null");
        }
    }

    //<editor-fold desc="Get Quote">
    public SignatureContainer<QuoteDTO> getQuote(SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteService.getQuote(UrlEncoderHelper.transform(securityId.exchange), UrlEncoderHelper.transform(
                securityId.securitySymbol));
    }

    public MiddleCallback<SignatureContainer<QuoteDTO>> getQuote(SecurityId securityId, Callback<SignatureContainer<QuoteDTO>> callback)
    {
        MiddleCallback<SignatureContainer<QuoteDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        basicCheck(securityId);
        this.quoteServiceAsync.getQuote(UrlEncoderHelper.transform(
                securityId.exchange), UrlEncoderHelper.transform(securityId.securitySymbol), middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Raw Quote">
    public Response getRawQuote(SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteService.getRawQuote(UrlEncoderHelper.transform(securityId.exchange), UrlEncoderHelper.transform(
                securityId.securitySymbol));
    }

    public BaseMiddleCallback<Response> getRawQuote(SecurityId securityId, Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        basicCheck(securityId);
        this.quoteServiceAsync.getRawQuote(UrlEncoderHelper.transform(securityId.exchange), UrlEncoderHelper.transform(
                securityId.securitySymbol), middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
