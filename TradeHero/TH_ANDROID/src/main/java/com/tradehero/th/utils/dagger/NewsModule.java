package com.tradehero.th.utils.dagger;

import com.tradehero.th.fragments.news.NewsDetailFullView;
import com.tradehero.th.fragments.news.NewsDetailSummaryView;
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
                NewsDetailSummaryView.class,
                NewsDetailFullView.class
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
}
