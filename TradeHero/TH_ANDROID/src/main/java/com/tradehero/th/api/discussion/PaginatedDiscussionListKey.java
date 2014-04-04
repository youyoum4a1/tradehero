package com.tradehero.th.api.discussion;

import android.os.Bundle;
import com.tradehero.th.api.PaginatedKey;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 6:05 PM Copyright (c) TradeHero
 */
public class PaginatedDiscussionListKey extends DiscussionListKey
    implements PaginatedKey
{
    private int page;

    public PaginatedDiscussionListKey(Integer key)
    {
        super(key);
    }

    public PaginatedDiscussionListKey(Bundle args)
    {
        super(args);

        page = args != null ? args.getInt(PAGE, 0) : 0;
    }

    public PaginatedDiscussionListKey(DiscussionListKey discussionListKey, int page)
    {
        super(discussionListKey.getArgs());

        this.page = page;
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof PaginatedDiscussionListKey) && ((PaginatedDiscussionListKey) other).page == page;
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);

        args.putInt(PAGE, page);
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
}
