package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.quote.RawQuoteParser;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

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
    //TODO this can not be repeat, please refer getQuoteFXRx
    @NonNull public Observable<QuoteDTO> getQuoteRx(@NonNull SecurityId securityId)
    {
        basicCheck(securityId);
        //noinspection Convert2Lambda
        return Observable.create(new Observable.OnSubscribe<QuoteDTO>()
        {
            @Override public void call(Subscriber<? super QuoteDTO> subscriber)
            {
                try
                {
                    Response response = quoteServiceRx.getRawQuote(
                            UrlEncoderHelper.transform(securityId.getExchange()),
                            UrlEncoderHelper.transform(securityId.getSecuritySymbol()));
                    subscriber.onNext(rawQuoteParser.parse(response));
                } catch (Exception e)
                {
                    subscriber.onError(e);
                }
            }
        })
//                ;
                .subscribeOn(Schedulers.io());
    }
    //</editor-fold>

    //<editor-fold desc="Get Quote fx">
    @NonNull public Observable<Response> getQuoteFXRx(@NonNull SecurityId securityId)
    {
        basicCheck(securityId);
        Observable<Response> received;
        received = quoteServiceRx.getRawQuoteFX(securityId.getExchange(), securityId.getSecuritySymbol());
        return received;
    }
    //</editor-fold>
}
