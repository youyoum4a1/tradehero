package com.ayondo.academy.utils.dagger;

import android.content.Context;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.ayondo.academy.models.graphics.TransformationModule;
import com.ayondo.academy.utils.Constants;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
                TransformationModule.class
        },
        injects = {
        },
        complete = false,
        library = true // TODO remove
)
public class GraphicModule
{
    @Provides @Singleton Picasso providePicasso(Context context, @ForPicasso LruCache lruFileCache, OkHttpClient okHttpClient)
    {
        Picasso mPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(okHttpClient))
                .memoryCache(lruFileCache)
                .build();
		mPicasso.setIndicatorsEnabled(Constants.PICASSO_DEBUG);
		mPicasso.setLoggingEnabled(Constants.PICASSO_DEBUG);
        return mPicasso;
    }
}
