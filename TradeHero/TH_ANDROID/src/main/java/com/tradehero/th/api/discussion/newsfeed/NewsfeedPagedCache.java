package com.ayondo.academy.api.discussion.newsfeed;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.api.pagination.PaginatedDTO;
import com.ayondo.academy.network.service.NewsServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton @UserCache
public class NewsfeedPagedCache extends BaseFetchDTOCacheRx<NewsfeedPagedDTOKey, NewsfeedDTOList>
{
    public static final int DEFAULT_SIZE = 10;
    @NonNull private final NewsServiceWrapper newsServiceWrapper;
    @NonNull private final NewsfeedCache newsfeedCache;

    @Inject protected NewsfeedPagedCache(@NonNull NewsServiceWrapper newsServiceWrapper, @NonNull NewsfeedCache newsfeedCache,
            @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_SIZE, dtoCacheUtilRx);
        this.newsServiceWrapper = newsServiceWrapper;
        this.newsfeedCache = newsfeedCache;
    }

    @NonNull @Override protected Observable<NewsfeedDTOList> fetch(@NonNull NewsfeedPagedDTOKey key)
    {
        return newsServiceWrapper.getIntegratedNews(key)
                .map(new Func1<PaginatedDTO<NewsItemCompactDTO>, NewsfeedDTOList>()
                {
                    @Override public NewsfeedDTOList call(PaginatedDTO<NewsItemCompactDTO> newsItemCompactDTOPaginatedDTO)
                    {
                        NewsfeedDTOList list = new NewsfeedDTOList(newsItemCompactDTOPaginatedDTO.getData().size());
                        for (NewsItemCompactDTO compactDTO : newsItemCompactDTOPaginatedDTO.getData())
                        {
                            list.add(NewsfeedNewsDTO.from(compactDTO));
                        }
                        return list;
                    }
                });
    }

    @Override public void onNext(@NonNull NewsfeedPagedDTOKey key, @NonNull NewsfeedDTOList value)
    {
        super.onNext(key, value);
        for (NewsfeedDTO dto : value)
        {
            newsfeedCache.onNext(dto.getKey(), dto);
        }
    }
}