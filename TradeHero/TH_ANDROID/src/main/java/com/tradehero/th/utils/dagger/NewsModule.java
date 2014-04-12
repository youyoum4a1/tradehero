package com.tradehero.th.utils.dagger;

import com.tradehero.th.fragments.news.NewsDetailFullView;
import com.tradehero.th.fragments.news.NewsDetailSummaryView;
import dagger.Module;

/**
 * Created by xavier on 2/21/14.
 */
@Module(
        injects = {
                NewsDetailSummaryView.class,
                NewsDetailFullView.class,
        },
        complete = false,
        library = true
)
public class NewsModule
{
}
