package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
@Singleton public class QuoteServiceWrapper
{
    public static final String TAG = QuoteServiceWrapper.class.getSimpleName();

    @Inject QuoteService quoteService;

    @Inject public QuoteServiceWrapper()
    {
        super();
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
            throws RetrofitError
    {
        basicCheck(securityId);
        return this.quoteService.getQuote(securityId.exchange, securityId.securitySymbol);
    }

    public void getQuote(SecurityId securityId, Callback<SignatureContainer<QuoteDTO>> callback)
            throws RetrofitError
    {
        basicCheck(securityId);
        this.quoteService.getQuote(securityId.exchange, securityId.securitySymbol, callback);
    }
    //</editor-fold>
}
