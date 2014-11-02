package com.tradehero.th.models.discussion;

import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import javax.inject.Inject;

public class NewsDTOProcessor extends DiscussionDTOProcessor<NewsItemCompactDTO>
{
    @Inject public NewsDTOProcessor(DiscussionCacheRx discussionCache)
    {
        super(discussionCache);
    }
}
