package com.tradehero.common.utils;

import java.util.Collection;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.client.Header;

public class RetrofitHelper
{
    //<editor-fold desc="Constructors">
    @Inject public RetrofitHelper()
    {
        super();
    }
    //</editor-fold>

    @Nullable public Header findByName(
            @NotNull Collection<? extends Header> headers,
            @NotNull String name)
    {
        for (Header header : headers)
        {
            if (header.getName().equals(name))
            {
                return header;
            }
        }
        return null;
    }
}
