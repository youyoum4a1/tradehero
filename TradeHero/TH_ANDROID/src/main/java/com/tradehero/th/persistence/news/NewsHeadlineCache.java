package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.news.NewsHeadlineList;
import com.tradehero.th.api.security.SecurityId;

/**
 * Cache for Yahoo News - uses SecurityId as a key and store List<News> as values.
 * This class uses internally the SecurityCompactCache (see the fetch method implementation)
 */
abstract public class NewsHeadlineCache extends StraightDTOCache<SecurityId, NewsHeadlineList>
{
    public NewsHeadlineCache(int maxSize)
    {
        super(maxSize);
    }
}

