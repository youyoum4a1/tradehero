package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.discussion.DiscussionType;

/**
 * Created by xavier on 3/7/14.
 */
public class GetDiscussionsKey extends DiscussionKey
{
    public final Integer page;
    public final Integer perPage;

    public GetDiscussionsKey(DiscussionType inReplyToType, int inReplyToId)
    {
        this(inReplyToType, inReplyToId, null);
    }

    public GetDiscussionsKey(DiscussionType inReplyToType, int inReplyToId, Integer page)
    {
        this(inReplyToType, inReplyToId, page, null);
    }

    public GetDiscussionsKey(DiscussionType inReplyToType, int inReplyToId, Integer page, Integer perPage)
    {
        super(inReplyToType, inReplyToId);
        this.page = page;
        this.perPage = perPage;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (page == null ? 0 : page.hashCode()) ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    @Override protected boolean equalFields(DiscussionKey other)
    {
        return super.equalFields(other) &&
                (other instanceof GetDiscussionsKey) &&
                equalFields((GetDiscussionsKey) other);
    }

    public boolean equalFields(GetDiscussionsKey other)
    {
        return super.equalFields(other) &&
                (page == null ? other.page == null : page.equals(other.page)) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));

    }
}
