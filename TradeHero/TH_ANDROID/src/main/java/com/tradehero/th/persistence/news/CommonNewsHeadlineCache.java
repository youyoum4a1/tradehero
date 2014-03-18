package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.PaginationDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.NewsServiceWrapper;

import javax.inject.Inject;

/**
 * Created by tradehero on 14-3-7.
 */
public abstract class CommonNewsHeadlineCache extends StraightDTOCache<SecurityId,PaginatedDTO<NewsItemDTO>> {
    public static final int DEFAULT_MAX_SIZE = 15;

    @Inject
    protected NewsServiceWrapper newsServiceWrapper;

    public CommonNewsHeadlineCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
}
