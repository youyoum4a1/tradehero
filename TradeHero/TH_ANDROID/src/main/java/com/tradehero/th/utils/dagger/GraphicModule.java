package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.squareup.picasso.Picasso;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.th.fragments.alert.AlertItemView;
import com.tradehero.th.utils.Constants;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:47 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                AlertItemView.class
        },
        complete = false,
        library = true // TODO remove
)
public class GraphicModule
{
    @Provides @Singleton Picasso providePicasso(Context context, LruMemFileCache lruFileCache)
    {
        Picasso mPicasso = new Picasso.Builder(context)
                //.downloader(new UrlConnectionDownloader(getContext()))
                .memoryCache(lruFileCache)
                .build();
        mPicasso.setDebugging(Constants.PICASSO_DEBUG);
        return mPicasso;
    }
}
