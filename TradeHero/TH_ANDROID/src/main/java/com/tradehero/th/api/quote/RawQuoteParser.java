package com.tradehero.th.api.quote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.RawResponseParser;
import com.tradehero.th.api.SignatureContainer;
import java.io.IOException;
import javax.inject.Inject;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;

public class RawQuoteParser extends RawResponseParser
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

    private static class QuoteSignatureContainer extends SignatureContainer<QuoteDTO>
    {
    }
}