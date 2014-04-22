package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Repurposes requests
 */
@Singleton public class QuoteServiceWrapper
{
    private final QuoteService quoteService;

    @Inject public QuoteServiceWrapper(QuoteService quoteService)
    {
        super();
        this.quoteService = quoteService;
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

    public void getQuote(SecurityId securityId, Callback<SignatureContainer<QuoteDTO>> callback)
    {
        basicCheck(securityId);
        this.quoteService.getQuote(UrlEncoderHelper.transform(
                securityId.exchange), UrlEncoderHelper.transform(securityId.securitySymbol), callback);
    }
    //</editor-fold>

    //<editor-fold desc="Get Raw Quote">
    public Response getRawQuote(SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteService.getRawQuote(UrlEncoderHelper.transform(securityId.exchange), UrlEncoderHelper.transform(
                securityId.securitySymbol));
    }

    public MiddleCallback<Response> getRawQuote(SecurityId securityId, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new MiddleCallback<>(callback);
        basicCheck(securityId);
        this.quoteService.getRawQuote(UrlEncoderHelper.transform(securityId.exchange), UrlEncoderHelper.transform(
                securityId.securitySymbol), middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
