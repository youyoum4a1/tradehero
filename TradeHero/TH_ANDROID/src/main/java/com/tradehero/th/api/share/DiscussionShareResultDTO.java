package com.tradehero.th.api.share;

import com.tradehero.th.api.discussion.DiscussionDTO;

public class DiscussionShareResultDTO implements SocialShareResultDTO
{
    public DiscussionDTO sharedDiscussion;

    //<editor-fold desc="Constructors">
    public DiscussionShareResultDTO()
    {
    }

    public DiscussionShareResultDTO(DiscussionDTO sharedDiscussion)
    {
        this.sharedDiscussion = sharedDiscussion;
    }
    //</editor-fold>
}
