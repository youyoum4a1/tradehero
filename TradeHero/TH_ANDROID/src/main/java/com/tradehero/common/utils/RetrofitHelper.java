package com.tradehero.common.utils;

import com.tradehero.th.utils.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RestAdapter;
import retrofit.client.Header;

import javax.inject.Inject;
import java.util.Collection;

public class RetrofitHelper
{
    //<editor-fold desc="Constructors">
    @Inject public RetrofitHelper()
    {
        super();
    }
    //</editor-fold>

    public static final RestAdapter.LogLevel DEFAULT_SERVICE_LOG_LEVEL =
            Constants.RELEASE ? RestAdapter.LogLevel.NONE : RestAdapter.LogLevel.FULL;

    @Nullable public Header findHeaderByName(
            @NotNull Collection<? extends Header> headers,
            @NotNull String name)
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
