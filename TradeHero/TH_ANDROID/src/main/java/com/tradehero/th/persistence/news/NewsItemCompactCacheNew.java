package com.tradehero.th.persistence.news;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class NewsItemCompactCacheNew extends StraightDTOCacheNew<NewsItemDTOKey, NewsItemCompactDTO>
{
    @Inject public NewsItemCompactCacheNew(@SingleCacheMaxSize IntPreference maxSize)
    {
        super(maxSize.get());
    }

    @Override @NotNull public NewsItemCompactDTO fetch(@NotNull NewsItemDTOKey newsItemDTOKey) throws Throwable
    {
        throw new IllegalStateException("No fetch yet");
    }

    public void put(@Nullable List<NewsItemCompactDTO> values)
    {
        if (values != null)
        {
            for (@NotNull NewsItemCompactDTO value : values)
            {
                put(value.getDiscussionKey(), value);
            }
        }
    }
}
