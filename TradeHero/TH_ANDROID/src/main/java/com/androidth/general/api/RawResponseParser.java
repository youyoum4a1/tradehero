package com.androidth.general.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;

import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit2.Response;

public class RawResponseParser
{
    @Nullable protected TypedByteArray getBodyAsTypedArray(@NonNull Response response) throws IOException
    {
//        TypedInput body = response.body();
//        if (body != null && body.mimeType() != null)
//        {
//            if (!(body instanceof TypedByteArray))
//            {
//                InputStream is = null;
//                try
//                {
//                    is = body.in();
//                } finally
//                {
//                    if (is != null)
//                    {
//                        try
//                        {
//                            is.close();
//                        } catch (IOException ignored)
//                        {
//                        }
//                    }
//                }
//                byte[] bodyBytes = IOUtils.streamToBytes(is);
//
//                return new TypedByteArray(body.mimeType(), bodyBytes);
//            }
//            return (TypedByteArray) body;
//        }

        //not used coz of Retrofit 2
        return null;
    }
}
