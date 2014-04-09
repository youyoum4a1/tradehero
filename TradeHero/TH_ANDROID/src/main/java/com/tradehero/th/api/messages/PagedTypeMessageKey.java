package com.tradehero.th.api.messages;

import android.os.Bundle;
import com.tradehero.th.api.PaginatedKey;

/**
 * Created by wangliang on 14-4-4.
 *
 *
 */
public class PagedTypeMessageKey extends TypedMessageKey implements PaginatedKey
{

    private int page;

    public PagedTypeMessageKey(int page)
    {
        this(MESSAGE_TYPE_ALL, page);
    }

    public PagedTypeMessageKey(Integer key,int page)
    {
        super(key);

        this.page = page;
    }

    public PagedTypeMessageKey(Bundle args)
    {
        super(args);
    }

    @Override public int getPage()
    {
        return page;
    }

    @Override public PagedTypeMessageKey next()
    {
        return next(1);
    }

    @Override public PagedTypeMessageKey next(int pages)
    {
        return new PagedTypeMessageKey(page + pages);
    }
}
