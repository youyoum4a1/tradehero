package com.androidth.general.models.fastfill.jumio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.androidth.general.models.fastfill.DocumentCheckService;
import com.androidth.general.models.fastfill.ForDocumentChecker;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.dagger.ForPicasso;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit2.Retrofit;

@Module(
        complete = false,
        library = true
)
public class NetverifyModule
{
    private Retrofit createNetverifyRestAdapter(
            Retrofit.Builder builder)
//            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return builder
//                .setEndpoint(Endpoints.newFixedEndpoint(NetverifyConstants.NETVERIFY_END_POINT))
//                .setRequestInterceptor(new NetverifyRetrofitRequestInterceptor())
//                .setLogLevel(RestAdapter.LogLevel.FULL) //to activate logging
//                .setErrorHandler(errorHandlerLogger)
                .baseUrl(NetverifyConstants.NETVERIFY_END_POINT)
                .build();
    }

    @Provides NetverifyServiceRx provideNetverifyServiceRx(Retrofit.Builder builder)
//            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return createNetverifyRestAdapter(builder).create(NetverifyServiceRx.class);
    }

    @Provides @ForDocumentChecker Picasso provideNetverifyPicasso(Context context, @ForPicasso LruCache lruFileCache, OkHttpClient okHttpClient)
    {
        OkHttpClient netverifyClient = okHttpClient;
        netverifyClient.interceptors().add(new NetverifyPicassoRequestInterceptor());

//        Log.v("", "!!!Network setting interceptor");
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//        netverifyClient.networkInterceptors().add(loggingInterceptor);

        Picasso mPicasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(netverifyClient))
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
