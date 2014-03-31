package com.tradehero.th.fragments.news;

import android.content.Context;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.news.NewsItemDTOKey;
import com.tradehero.th.fragments.discussion.DiscussionListLoader;

/**
 * Created by tho on 3/26/2014.
 */
public class NewsDiscussionListLoader extends DiscussionListLoader
{
    public NewsDiscussionListLoader(Context context, NewsItemDTOKey newsItemDTOKey)
    {
        super(context, DiscussionType.NEWS, newsItemDTOKey.key);
    }
}