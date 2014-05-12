package com.tradehero.th.api.pagination;

public interface PaginatedKey
{
    static final String BUNDLE_PAGE = ".page";
    static final String BUNDLE_PERPAGE = ".perPage";

    static final String JSON_PAGE = "page";
    static final String JSON_PERPAGE = "perPage";

    Integer getPage();
    PaginatedKey next();
    PaginatedKey next(int pages);
    PaginatedKey prev();
    PaginatedKey prev(int pages);
}
