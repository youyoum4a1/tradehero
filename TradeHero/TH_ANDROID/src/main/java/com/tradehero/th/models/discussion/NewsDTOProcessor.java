package com.ayondo.academy.models.discussion;

import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.persistence.discussion.DiscussionCacheRx;
import javax.inject.Inject;

public class NewsDTOProcessor extends DiscussionDTOProcessor<NewsItemCompactDTO>
{
    @Inject public NewsDTOProcessor(DiscussionCacheRx discussionCache)
    {
        super(discussionCache);
    }
}
