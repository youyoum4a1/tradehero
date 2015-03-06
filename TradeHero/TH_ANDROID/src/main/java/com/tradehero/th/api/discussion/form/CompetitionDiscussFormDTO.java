package com.tradehero.th.api.discussion.form;

import com.tradehero.th.api.discussion.DiscussionType;

/**
 * Created by palmer on 15/3/6.
 */
public class CompetitionDiscussFormDTO extends DiscussionFormDTO
{
    public static final DiscussionType TYPE = DiscussionType.COMPETITION;

    public CompetitionDiscussFormDTO()
    {
        super();
    }

    @Override public DiscussionType getInReplyToType()
    {
        return TYPE;
    }
}