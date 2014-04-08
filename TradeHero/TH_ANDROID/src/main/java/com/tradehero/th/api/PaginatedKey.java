package com.tradehero.th.api;

/**
 * Created by thonguyen on 3/4/14.
 */
public interface PaginatedKey
{
    static final String PAGE = ".page";
    static final String JSON_PAGE = "page";
    static final String JSON_PERPAGE = "perPage";

    int getPage();
    PaginatedKey next();
    PaginatedKey next(int pages);
}
