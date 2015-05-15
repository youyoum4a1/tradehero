package com.tradehero.th.utils.dagger;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.tradehero.th.models.graphics.TransformationModule;
import com.tradehero.th.utils.BitmapForProfileFactory;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.GraphicUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    @Provides @Singleton Picasso providePicasso(Context context, @ForPicasso LruCache lruFileCache)
    {
        Picasso mPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(new OkHttpClient()))
                .memoryCache(lruFileCache)
                .build();
		mPicasso.setIndicatorsEnabled(Constants.PICASSO_DEBUG);
		mPicasso.setLoggingEnabled(Constants.PICASSO_DEBUG);
        return mPicasso;
    }

    @Provides BitmapForProfileFactory provideBitmapForProfileFactory(GraphicUtil graphicUtil)
    {
        return graphicUtil;
    }
}
