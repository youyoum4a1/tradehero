package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.pagination.PaginatedKey;
import com.tradehero.th.api.discussion.DiscussionType;
import java.util.Map;

public class PaginatedDiscussionListKey extends DiscussionListKey
    implements PaginatedKey
{
    public static final String PAGE_BUNDLE_KEY = PaginatedDiscussionListKey.class.getName() + ".page";
    public static final String PER_PAGE_BUNDLE_KEY = PaginatedDiscussionListKey.class.getName() + ".perPage";

    public final Integer page;
    public final Integer perPage;

    //<editor-fold desc="Constructors">
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

    public PaginatedDiscussionListKey(Bundle args)
    {
        super(args);
        this.page = args.containsKey(PAGE_BUNDLE_KEY) ? args.getInt(PAGE_BUNDLE_KEY) : null;
        this.perPage = args.containsKey(PER_PAGE_BUNDLE_KEY) ? args.getInt(PER_PAGE_BUNDLE_KEY) : null;
    }
    //</editor-fold>

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
    @Override public Integer getPage()
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

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (page != null)
        {
            args.putInt(PAGE_BUNDLE_KEY, page);
        }
        if (perPage != null)
        {
            args.putInt(PER_PAGE_BUNDLE_KEY, perPage);
        }
    }

    @Override public Map<String, Object> toMap()
    {
        Map<String, Object> generatedMap = super.toMap();

        generatedMap.put(PaginatedKey.JSON_PAGE, page);
        generatedMap.put(PaginatedKey.JSON_PERPAGE, perPage);
        return generatedMap;
    }
}
