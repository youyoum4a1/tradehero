package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.quote.QuoteDTO;
import com.androidth.general.api.quote.RawQuoteParser;
import com.androidth.general.api.security.SecurityId;
import javax.inject.Inject;
import javax.inject.Singleton;
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

    private void basicCheck(@Nullable SecurityId securityId)
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
    @NonNull public Observable<QuoteDTO> getQuoteRx(@NonNull SecurityId securityId)
    {
        basicCheck(securityId);
        return quoteServiceRx.getRawQuote(securityId.getExchange(), securityId.getSecuritySymbol())
                .onErrorResumeNext(
                        quoteServiceRx.getRawQuote(securityId.getExchange(), securityId.getPathSafeSymbol()))
                .flatMap(rawQuoteParser);
    }
    //</editor-fold>
}
