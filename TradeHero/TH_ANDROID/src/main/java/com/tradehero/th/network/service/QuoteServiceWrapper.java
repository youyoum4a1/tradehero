package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.quote.RawQuoteParser;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.client.Response;
import rx.Observable;

@Singleton public class QuoteServiceWrapper
{
    @NonNull private final QuoteServiceRx quoteServiceRx;
    @NonNull private final RawQuoteParser rawQuoteParser;

    //<editor-fold desc="Constructors">
    @Inject public QuoteServiceWrapper(
            @NonNull QuoteServiceRx quoteServiceRx,
            @NonNull RawQuoteParser rawQuoteParser)
    {
        super();
        this.quoteServiceRx = quoteServiceRx;
        this.rawQuoteParser = rawQuoteParser;
    }
    //</editor-fold>

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
    public Observable<SignatureContainer<QuoteDTO>> getSignedContainerQuoteRx(SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteServiceRx.getQuote(UrlEncoderHelper.transform(securityId.getExchange()), UrlEncoderHelper.transform(
                securityId.getSecuritySymbol()));
    }
    //</editor-fold>

    //<editor-fold desc="Get Raw Quote">
    @NonNull public Observable<QuoteDTO> getQuoteRx(@NonNull SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteServiceRx.getRawQuote(
                UrlEncoderHelper.transform(securityId.getExchange()),
                UrlEncoderHelper.transform(securityId.getSecuritySymbol()))
                .map(rawQuoteParser);
    }

    @NonNull public Observable<Response> getRawQuoteRx(@NonNull SecurityId securityId)
    {
        basicCheck(securityId);
        return this.quoteServiceRx.getRawQuote(UrlEncoderHelper.transform(securityId.getExchange()), UrlEncoderHelper.transform(
                securityId.getSecuritySymbol()));
    }
    //</editor-fold>
}
