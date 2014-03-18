package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.PaginationDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.NewsServiceWrapper;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by tradehero on 14-3-7.
 *
 * The cache about an certain security.
 */
@Singleton
public class CertainSecurityHeadlineCache extends CommonNewsHeadlineCache {
    /**
     */
    @Override protected PaginatedDTO<NewsItemDTO> fetch(SecurityId key) throws Throwable
    {
        if(BuildConfig.DEBUG){
            Timber.d("NewsHeadlineList fetch news, key:%s", key);
        }
        return fetchSecurityNews(key.id);
    }



    private PaginatedDTO<NewsItemDTO> fetchSecurityNews(int securityId) throws Throwable
    {
        return newsServiceWrapper.getSecurityNews(securityId);
    }

}
