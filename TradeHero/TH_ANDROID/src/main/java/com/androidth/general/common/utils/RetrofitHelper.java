package com.androidth.general.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collection;
import javax.inject.Inject;
import retrofit.client.Header;

public class RetrofitHelper
{
    //<editor-fold desc="Constructors">
    @Inject public RetrofitHelper()
    {
        super();
    }
    //</editor-fold>

    @Nullable public Header findHeaderByName(
            @NonNull Collection<? extends Header> headers,
            @NonNull String name)
    {
        for (Header header : headers)
        {
            if (header.getName() != null && header.getName().equals(name))
            {
                return header;
            }
        }
        return null;
    }
}
