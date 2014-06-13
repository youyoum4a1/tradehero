package com.tradehero.th.utils;

import retrofit.RestAdapter;

public class RetrofitConstants
{
    public static final RestAdapter.LogLevel DEFAULT_SERVICE_LOG_LEVEL =
            Constants.RELEASE ? RestAdapter.LogLevel.NONE : RestAdapter.LogLevel.FULL;
}
