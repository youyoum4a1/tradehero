package com.tradehero.th.api;

/**
 * Created by thonguyen on 3/4/14.
 */
public interface PaginatedKey
{
    static final String PAGE = ".page";

    int getPage();
    PaginatedKey next();
    PaginatedKey next(int pages);
}
