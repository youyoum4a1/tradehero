package com.tradehero.th.api.discussion;

import android.os.Bundle;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 6:05 PM Copyright (c) TradeHero
 */
public class PagedDiscussionKey extends DiscussionKey
{
    public final static String BUNDLE_KEY_PAGE = PagedDiscussionKey.class.getName() + ".page";

    private final Integer page;

    public PagedDiscussionKey(Integer key, Integer page)
    {
        super(key);
        this.page = page;
    }

    public PagedDiscussionKey(DiscussionKey discussionKey, Integer page)
    {
        this(discussionKey.key, page);
    }

    public PagedDiscussionKey(Bundle args)
    {
        super(args);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }

    public Integer getPage()
    {
        return page;
    }
}
