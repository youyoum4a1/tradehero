package com.tradehero.th.network.service;

import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
public class QuoteServiceUtil
{
    public static final String TAG = QuoteServiceUtil.class.getSimpleName();

    private static void basicCheck(SecurityId securityId)
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
    public static SignatureContainer<QuoteDTO> getQuote(QuoteService quoteService, SecurityId securityId)
            throws RetrofitError
    {
        basicCheck(securityId);
        return quoteService.getQuote(securityId.exchange, securityId.securitySymbol);
    }

    public static void getQuote(QuoteService quoteService, SecurityId securityId, Callback<SignatureContainer<QuoteDTO>> callback)
            throws RetrofitError
    {
        basicCheck(securityId);
        quoteService.getQuote(securityId.exchange, securityId.securitySymbol, callback);
    }
    //</editor-fold>
}
