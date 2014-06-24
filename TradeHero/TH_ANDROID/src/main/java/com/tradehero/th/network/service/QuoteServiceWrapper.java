package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.tradehero.th.network.retrofit.MiddleCallback;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class QuoteServiceWrapper
{
    @NotNull private final QuoteService quoteService;
    @NotNull private final QuoteServiceAsync quoteServiceAsync;

    @Inject public QuoteServiceWrapper(
            @NotNull QuoteService quoteService,
            @NotNull QuoteServiceAsync quoteServiceAsync)
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
        if (securityId.getExchange() == null)
        {
            throw new NullPointerException("securityId.getExchange() cannot be null");
        }
        if (securityId.getSecuritySymbol() == null)
        {
            throw new NullPointerException("securityId.getSecuritySymbol() cannot be null");
        }
    }

    //<editor-fold desc="Get Quote">
    public SignatureContainer<QuoteDTO> getQuote(SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteService.getQuote(UrlEncoderHelper.transform(securityId.getExchange()), UrlEncoderHelper.transform(
                securityId.getSecuritySymbol()));
    }

    public MiddleCallback<SignatureContainer<QuoteDTO>> getQuote(SecurityId securityId, Callback<SignatureContainer<QuoteDTO>> callback)
    {
        MiddleCallback<SignatureContainer<QuoteDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        basicCheck(securityId);
        this.quoteServiceAsync.getQuote(UrlEncoderHelper.transform(
                securityId.getExchange()), UrlEncoderHelper.transform(securityId.getSecuritySymbol()), middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Raw Quote">
    public Response getRawQuote(SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteService.getRawQuote(UrlEncoderHelper.transform(securityId.getExchange()), UrlEncoderHelper.transform(
                securityId.getSecuritySymbol()));
    }

    public BaseMiddleCallback<Response> getRawQuote(SecurityId securityId, Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        basicCheck(securityId);
        this.quoteServiceAsync.getRawQuote(UrlEncoderHelper.transform(securityId.getExchange()), UrlEncoderHelper.transform(
                securityId.getSecuritySymbol()), middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
