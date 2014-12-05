package com.tradehero.th.api;

import android.support.annotation.NonNull;
import com.tradehero.common.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public class RawResponseParser
{
    //<editor-fold desc="Constructors">
    @Inject public RawResponseParser()
    {
    }
    //</editor-fold>

    public void appendRawResponse(
            @NonNull RawResponseKeeper rawResponseKeeper,
            @NonNull Response response) throws IOException
    {
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

                rawResponseKeeper.setRawResponse(new String(((TypedByteArray) body).getBytes()));
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
    }
}
