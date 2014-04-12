package com.tradehero.th.fragments.discussion;

import android.content.Context;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.news.key.NewsItemDTOKey;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/25/14 Time: 2:57 PM Copyright (c) TradeHero
 */
public class SecurityCommentListLoader extends DiscussionListLoader
{
    public SecurityCommentListLoader(Context context, NewsItemDTOKey newsItemDTOKey)
    {
        super(context, DiscussionType.NEWS, newsItemDTOKey.id);
    }
}
