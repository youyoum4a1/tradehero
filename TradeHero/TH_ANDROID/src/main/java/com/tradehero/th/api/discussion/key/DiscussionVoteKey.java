package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;

public class DiscussionVoteKey extends DiscussionListKey
{
    public static final String VOTE_DIRECTION_NAME_BUNDLE_KEY = DiscussionVoteKey.class.getName() + ".voteDirection";
    public final VoteDirection voteDirection;

    //<editor-fold desc="Constructors">
    public DiscussionVoteKey(DiscussionType inReplyToType, int inReplyToId,
            VoteDirection voteDirection)
    {
        super(inReplyToType, inReplyToId);
        this.voteDirection = voteDirection;
    }

    public DiscussionVoteKey(Bundle args)
    {
        super(args);
        if (!args.containsKey(VOTE_DIRECTION_NAME_BUNDLE_KEY))
        {
            throw new IllegalArgumentException("Missing VOTE_DIRECTION_NAME_BUNDLE_KEY");
        }
        this.voteDirection = VoteDirection.valueOf(args.getString(VOTE_DIRECTION_NAME_BUNDLE_KEY));
    }
    //</editor-fold>

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

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putString(VOTE_DIRECTION_NAME_BUNDLE_KEY, voteDirection.name());
    }
}
