package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.quote.RawQuoteParser;
import com.tradehero.th.api.security.SecurityId;
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
    @NonNull public Observable<QuoteDTO> getQuoteRx(@NonNull SecurityId securityId)
    {
        basicCheck(securityId);
        return quoteServiceRx.getRawQuote(securityId.getExchange(), securityId.getPathSafeSymbol())
                .flatMap(response -> {
                    try
                    {
                        QuoteDTO parsed = rawQuoteParser.parse(response);
                        return Observable.just(parsed);
                    } catch (Throwable e)
                    {
                        return Observable.error(e);
                    }
                });
    }
    //</editor-fold>
}
