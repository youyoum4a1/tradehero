package com.tradehero.th.api.discussion.key;

import com.tradehero.th.api.PaginatedKey;
import com.tradehero.th.api.discussion.DiscussionType;
import java.util.Map;

/**
 * Created by xavier on 3/7/14.
 */
public class PaginatedDiscussionListKey extends DiscussionListKey
    implements PaginatedKey
{
    public final Integer page;
    public final Integer perPage;

    public PaginatedDiscussionListKey(DiscussionType inReplyToType, int inReplyToId)
    {
        this(inReplyToType, inReplyToId, null);
    }

    public PaginatedDiscussionListKey(DiscussionType inReplyToType, int inReplyToId, Integer page)
    {
        this(inReplyToType, inReplyToId, page, null);
    }

    public PaginatedDiscussionListKey(DiscussionType inReplyToType, int inReplyToId, Integer page, Integer perPage)
    {
        super(inReplyToType, inReplyToId);
        this.page = page;
        this.perPage = perPage;
    }

    public PaginatedDiscussionListKey(DiscussionListKey discussionListKey, Integer page, Integer perPage)
    {
        this(discussionListKey.inReplyToType, discussionListKey.inReplyToId, page, perPage);
    }

    public PaginatedDiscussionListKey(DiscussionListKey discussionListKey, Integer page)
    {
        this(discussionListKey, page, null);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (page == null ? 0 : page.hashCode()) ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    @Override protected boolean equalFields(DiscussionListKey other)
    {
        return super.equalFields(other) &&
                (other instanceof PaginatedDiscussionListKey) &&
                equalFields((PaginatedDiscussionListKey) other);
    }

    public boolean equalFields(PaginatedDiscussionListKey other)
    {
        return super.equalFields(other) &&
                (page == null ? other.page == null : page.equals(other.page)) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));

    }

    //<editor-fold desc="PaginatedKey">
    @Override public int getPage()
    {
        return page;
    }

    @Override public PaginatedDiscussionListKey next()
    {
        return next(1);
    }

    @Override public PaginatedDiscussionListKey next(int pages)
    {
        return new PaginatedDiscussionListKey(this, page + pages);
    }
    //</editor-fold>

    @Override public Map<String, Object> toMap()
    {
        Map<String, Object> generatedMap = super.toMap();

        generatedMap.put(PaginatedKey.JSON_PAGE, page);
        generatedMap.put(PaginatedKey.JSON_PERPAGE, perPage);
        return generatedMap;
    }
}
