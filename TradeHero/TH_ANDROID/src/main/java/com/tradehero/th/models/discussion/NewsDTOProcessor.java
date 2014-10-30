package com.tradehero.th.models.discussion;

import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import javax.inject.Inject;

/**
 * Created by thonguyen on 28/10/14.
 */
public class NewsDTOProcessor extends DiscussionDTOProcessor<NewsItemCompactDTO>
{
    @Inject public NewsDTOProcessor(DiscussionCache discussionCache)
    {
        super(discussionCache);
    }
}
