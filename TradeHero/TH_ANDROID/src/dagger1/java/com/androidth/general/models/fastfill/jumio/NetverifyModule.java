package com.androidth.general.models.fastfill.jumio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.androidth.general.common.log.RetrofitErrorHandlerLogger;
import com.androidth.general.models.fastfill.DocumentCheckService;
import com.androidth.general.models.fastfill.ForDocumentChecker;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.dagger.ForPicasso;
import dagger.Module;
import dagger.Provides;
import retrofit.Endpoints;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true
)
public class NetverifyModule
{
    private RestAdapter createNetverifyRestAdapter(
            RestAdapter.Builder builder,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return builder
                .setEndpoint(Endpoints.newFixedEndpoint(NetverifyConstants.NETVERIFY_END_POINT))
                .setRequestInterceptor(new NetverifyRetrofitRequestInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL) //to activate logging
                .setErrorHandler(errorHandlerLogger)
                .build();
    }

    @Provides NetverifyServiceRx provideNetverifyServiceRx(RestAdapter.Builder builder,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return createNetverifyRestAdapter(builder, errorHandlerLogger).create(NetverifyServiceRx.class);
    }

    @Provides @ForDocumentChecker Picasso provideNetverifyPicasso(Context context, @ForPicasso LruCache lruFileCache, OkHttpClient okHttpClient)
    {
        OkHttpClient netverifyClient = okHttpClient.clone();
        netverifyClient.interceptors().add(new NetverifyPicassoRequestInterceptor());

        Log.v("", "!!!Network setting interceptor");
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        netverifyClient.networkInterceptors().add(loggingInterceptor);

        Picasso mPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(netverifyClient))
                .memoryCache(lruFileCache)
                .build();
        mPicasso.setIndicatorsEnabled(Constants.PICASSO_DEBUG);
        mPicasso.setLoggingEnabled(Constants.PICASSO_DEBUG);
        return mPicasso;
    }

    @Provides DocumentCheckService provideNetverifyDocumentCheckService(@NonNull NetverifyServiceWrapper netverifyServiceWrapper)
    {
        return netverifyServiceWrapper;
    }
}
