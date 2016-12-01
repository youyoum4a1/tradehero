package com.androidth.general.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import javax.inject.Inject;

import okhttp3.Headers;
import retrofit2.http.Header;

public class RetrofitHelper
{
    //<editor-fold desc="Constructors">
    @Inject public RetrofitHelper()
    {
        super();
    }
    //</editor-fold>

//    @Nullable public Header findHeaderByName(
//            @NonNull Collection<? extends Header> headers,
//            @NonNull String name)
//    {
//        for (Header header : headers)
//        {
//            if (header.getName() != null && header.getName().equals(name))
//            {
//                return header;
//            }
//            //Retrofit 2 way
//            if (header.value() != null && header.value().equals(name))
//            {
//                return header;
//            }
//        }
//        return null;
//    }

    //Retrofit 2 way
    @Nullable public static String findHeaderCodeByName(
            @NonNull Headers headers,
            @NonNull String name)
    {
        for (int i=0; i<headers.size(); i++)
        {
            String headerName = headers.name(i);
            if (headerName != null && headerName.equals(name))
            {
                return headers.value(i);
            }
        }
        return null;
    }
}
