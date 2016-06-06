package com.androidth.general.api.pagination;

import com.androidth.general.common.api.PagedDTOKey;

public interface PaginatedKey extends PagedDTOKey
{
    static final String BUNDLE_PAGE = ".page";
    static final String BUNDLE_PERPAGE = ".perPage";

    static final String JSON_PAGE = "page";
    static final String JSON_PERPAGE = "perPage";

    PaginatedKey next();
    PaginatedKey next(int pages);
    PaginatedKey prev();
    PaginatedKey prev(int pages);
}
