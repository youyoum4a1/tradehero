package com.tradehero.th.api.pagination;

/**
 * Created by thonguyen on 3/4/14.
 */
public interface PaginatedKey
{
    static final String BUNDLE_PAGE = ".page";
    static final String JSON_PAGE = "page";
    static final String JSON_PERPAGE = "perPage";

    Integer getPage();
    PaginatedKey next();
    PaginatedKey next(int pages);
}
