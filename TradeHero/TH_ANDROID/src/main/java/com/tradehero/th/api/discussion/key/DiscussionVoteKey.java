package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;

/**
 * Created by xavier on 3/7/14.
 */
public class DiscussionVoteKey extends DiscussionListKey
{
    public final VoteDirection voteDirection;

    public DiscussionVoteKey(DiscussionType inReplyToType, int inReplyToId,
            VoteDirection voteDirection)
    {
        super(inReplyToType, inReplyToId);
        this.voteDirection = voteDirection;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (voteDirection == null ? 0 : voteDirection.hashCode());
    }

    @Override protected boolean equalFields(DiscussionListKey other)
    {
        return super.equalFields(other) &&
                (other instanceof DiscussionVoteKey) &&
                equalFields((DiscussionVoteKey) other);
    }

    public boolean equalFields(DiscussionVoteKey other)
    {
        return super.equalFields(other) &&
                (voteDirection == null ? other.voteDirection == null : voteDirection.equals(other.voteDirection));

    }
}
