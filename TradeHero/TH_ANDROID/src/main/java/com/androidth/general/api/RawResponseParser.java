package com.androidth.general.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

public class RawResponseParser
{
//    @Nullable protected TypedByteArray getBodyAsTypedArray(@NonNull Response response) throws IOException
//    {
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
//
//        //not used coz of Retrofit 2
//        return null;
//    }


    @Nullable protected String getResponseBodyToString(@NonNull ResponseBody responseBody) throws IOException
    {
        InputStream is = null;
        try
        {
            is = responseBody.byteStream();
        } finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return IOUtils.streamToString(is);
    }
}
