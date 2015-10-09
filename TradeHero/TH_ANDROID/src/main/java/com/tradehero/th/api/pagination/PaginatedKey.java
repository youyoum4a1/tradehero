package com.tradehero.th.api.pagination;

import com.tradehero.common.api.PagedDTOKey;

public interface PaginatedKey extends PagedDTOKey
{
    String BUNDLE_PAGE = ".page";
    String BUNDLE_PERPAGE = ".perPage";

    String JSON_PAGE = "page";
    String JSON_PERPAGE = "perPage";

    PaginatedKey next();
    PaginatedKey next(int pages);
    PaginatedKey prev();
    PaginatedKey prev(int pages);
}
