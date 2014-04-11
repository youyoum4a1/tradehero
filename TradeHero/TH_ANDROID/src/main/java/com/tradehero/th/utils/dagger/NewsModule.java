package com.tradehero.th.utils.dagger;

import com.tradehero.th.fragments.news.NewsDetailFullView;
import com.tradehero.th.fragments.news.NewsDetailSummaryView;
import com.tradehero.th.fragments.news.NewsDiscussionListLoader;
import dagger.Module;

/**
 * Created by xavier on 2/21/14.
 */
@Module(
        injects = {
                NewsDetailSummaryView.class,
                NewsDetailFullView.class,
                NewsDiscussionListLoader.class
        },
        complete = false,
        library = true
)
public class NewsModule
{
}
