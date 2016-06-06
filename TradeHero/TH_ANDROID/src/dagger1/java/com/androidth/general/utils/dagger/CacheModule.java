package com.androidth.general.utils.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import com.squareup.picasso.LruCache;
import com.androidth.general.common.annotation.ForUser;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.common.persistence.prefs.LongPreference;
import com.androidth.general.persistence.ListCacheMaxSize;
import com.androidth.general.persistence.MessageListTimeline;
import com.androidth.general.persistence.SingleCacheMaxSize;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class CacheModule
{
    @Provides @Singleton @ForPicasso LruCache providePicassoMemCache(Context context)
    {
        return new LruCache(context);
    }

    @Provides @Singleton @SingleCacheMaxSize IntPreference provideDefaultSingleCacheMaxSize(@ForUser SharedPreferences preference)
    {
        return new IntPreference(preference, SingleCacheMaxSize.class.getName(), 1000);
    }

    @Provides @Singleton @ListCacheMaxSize IntPreference provideListSingleCacheMaxSize(@ForUser SharedPreferences preference)
    {
        return new IntPreference(preference, ListCacheMaxSize.class.getName(), 200);
    }

    @Provides @Singleton @MessageListTimeline LongPreference provideMessageListTimeline(@ForUser SharedPreferences preference)
    {
        return new LongPreference(preference, MessageListTimeline.class.getName(), -1L);
    }
}
