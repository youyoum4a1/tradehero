package com.tradehero.th.api.quote;

import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.SignatureContainer;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import rx.functions.Func1;

public class RawQuoteParser implements Func1<Response, QuoteDTO>
{
    @NonNull private Converter converter;

    //<editor-fold desc="Constructors">
    @Inject public RawQuoteParser(@NonNull Converter converter)
    {
        this.converter = converter;
    }
    //</editor-fold>

    @Override @NonNull public QuoteDTO call(Response response)
    {
        try
        {
            QuoteDTO quoteDTO = parse(response);
            if (quoteDTO == null)
            {
                throw new NullPointerException("QuoteDTO was parsed as null");
            }
            return quoteDTO;
        }
        catch (IOException | ConversionException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Nullable public QuoteDTO parse(@NonNull Response response) throws IOException, ConversionException
    {
        QuoteDTO quoteDTO = null;
        TypedInput body = response.getBody();
        InputStream is = null;
        try
        {
            if (body != null && body.mimeType() != null)
            {
                if (!(body instanceof TypedByteArray))
                {
                    is = body.in();
                    byte[] bodyBytes = IOUtils.streamToBytes(is);

                    body = new TypedByteArray(body.mimeType(), bodyBytes);
                }

                QuoteSignatureContainer signatureContainer = (QuoteSignatureContainer) converter.fromBody(body, QuoteSignatureContainer.class);
                if (signatureContainer != null)
                {
                    quoteDTO = signatureContainer.signedObject;
                    if (quoteDTO != null)
                    {
                        quoteDTO.rawResponse = new String(((TypedByteArray) body).getBytes());
                    }
                }
            }
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException ignored)
                {
                }
            }
        }
        return quoteDTO;
    }

    private static class QuoteSignatureContainer extends SignatureContainer<QuoteDTO>{}
}
