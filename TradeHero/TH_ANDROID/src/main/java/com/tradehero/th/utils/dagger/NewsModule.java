package com.tradehero.th.utils.dagger;

import com.tradehero.th.models.graphics.ForSecurityItemBackground2;
import com.tradehero.th.persistence.news.CertainSecurityHeadlineCache;
import com.tradehero.th.persistence.news.CommonNewsHeadlineCache;
import com.tradehero.th.persistence.news.NewsHeadlineCache;
import com.tradehero.th.persistence.news.yahoo.YahooNewsHeadlineCache;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/21/14.
 */
@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class NewsModule
{
    @Provides @Singleton NewsHeadlineCache provideNewsHeadlineCache(YahooNewsHeadlineCache newsHeadlineCache)
    {
        return newsHeadlineCache;
    }

    @Provides @ForCertainSecurityNews @Singleton
    CommonNewsHeadlineCache provideNewsHeadlineCache(CertainSecurityHeadlineCache newsHeadlineCache)
    {
        return newsHeadlineCache;
    }
}
