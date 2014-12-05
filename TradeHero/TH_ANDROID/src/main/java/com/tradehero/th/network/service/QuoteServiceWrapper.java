package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.RawResponseParser;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;

@Singleton public class QuoteServiceWrapper
{
    @NonNull private final QuoteServiceRx quoteServiceRx;
    @NonNull private final RawResponseParser rawResponseParser;

    //<editor-fold desc="Constructors">
    @Inject public QuoteServiceWrapper(
            @NonNull QuoteServiceRx quoteServiceRx,
            @NonNull RawResponseParser rawResponseParser)
    {
        super();
        this.quoteServiceRx = quoteServiceRx;
        this.rawResponseParser = rawResponseParser;
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
        return Observable.create(subscriber -> quoteServiceRx.getRawQuote(
                UrlEncoderHelper.transform(securityId.getExchange()),
                UrlEncoderHelper.transform(securityId.getSecuritySymbol()),
                new Callback<QuoteServiceRx.QuoteSignatureContainer>()
                {
                    @Override public void success(
                            QuoteServiceRx.QuoteSignatureContainer container,
                            Response response)
                    {
                        try
                        {
                            QuoteDTO quoteDTO = container.signedObject;
                            rawResponseParser.appendRawResponse(quoteDTO, response);
                            subscriber.onNext(quoteDTO);
                            subscriber.onCompleted();
                        } catch (IOException e)
                        {
                            subscriber.onError(e);
                        }
                    }

                    @Override public void failure(RetrofitError error)
                    {
                        subscriber.onError(error);
                    }
                }));
    }
    //</editor-fold>
}
