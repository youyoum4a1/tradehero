package com.androidth.general.models.discussion;

import com.androidth.general.api.news.NewsItemCompactDTO;
import com.androidth.general.persistence.discussion.DiscussionCacheRx;
import javax.inject.Inject;

public class NewsDTOProcessor extends DiscussionDTOProcessor<NewsItemCompactDTO>
{
    @Inject public NewsDTOProcessor(DiscussionCacheRx discussionCache)
    {
        super(discussionCache);
    }
}
