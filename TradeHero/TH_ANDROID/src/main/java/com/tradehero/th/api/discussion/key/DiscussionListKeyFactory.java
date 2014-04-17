package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import javax.inject.Inject;

public class DiscussionListKeyFactory
{
    @Inject public DiscussionListKeyFactory()
    {
        super();
    }

    public DiscussionListKey create(Bundle args)
    {
        DiscussionListKey discussionListKey = null;
        if (args != null)
        {
            // Perhaps a bit too haphazard?
            if (args.containsKey(DiscussionVoteKey.VOTE_DIRECTION_NAME_BUNDLE_KEY))
            {
                discussionListKey = new DiscussionVoteKey(args);
            }
            else if (args.containsKey(RangedDiscussionListKey.MAX_COUNT_BUNDLE_KEY))
            {
                discussionListKey = new RangedDiscussionListKey(args);
            }
            else if (args.containsKey(PaginatedDiscussionListKey.PAGE_BUNDLE_KEY))
            {
                discussionListKey = new PaginatedDiscussionListKey(args);
            }
            else if (args.containsKey(DiscussionListKey.IN_REPLY_TO_TYPE_NAME_BUNDLE_KEY))
            {
                discussionListKey = new DiscussionListKey(args);
            }
        }
        return discussionListKey;
    }
}
