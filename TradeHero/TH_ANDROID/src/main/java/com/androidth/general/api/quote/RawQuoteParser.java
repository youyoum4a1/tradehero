package com.androidth.general.api.quote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.RawResponseParser;
import com.androidth.general.api.SignatureContainer;
import java.io.IOException;
import javax.inject.Inject;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import rx.Observable;
import rx.functions.Func1;

public class RawQuoteParser extends RawResponseParser
        implements Func1<Response, Observable<? extends QuoteDTO>>
{
    @NonNull private Converter converter;

    //<editor-fold desc="Constructors">
    @Inject public RawQuoteParser(@NonNull Converter converter)
    {
        this.converter = converter;
    }
    //</editor-fold>

    @Nullable public QuoteDTO parse(@NonNull Response response) throws IOException, ConversionException
    {
        QuoteDTO quoteDTO = null;
        TypedByteArray body = getBodyAsTypedArray(response);
        if (body != null)
        {
            QuoteSignatureContainer signatureContainer = (QuoteSignatureContainer) converter.fromBody(body, QuoteSignatureContainer.class);
            if (signatureContainer != null)
            {
                quoteDTO = signatureContainer.signedObject;
                if (quoteDTO != null)
                {
                    quoteDTO.setRawResponse(new String(body.getBytes()));
                }
            }
        }
        return quoteDTO;
    }

    @Override public Observable<? extends QuoteDTO> call(@NonNull Response response)
    {
        try
        {
            QuoteDTO parsed = parse(response);
            return Observable.just(parsed);
        } catch (Throwable e)
        {
            return Observable.error(e);
        }
    }

    private static class QuoteSignatureContainer extends SignatureContainer<QuoteDTO>
    {
    }
}