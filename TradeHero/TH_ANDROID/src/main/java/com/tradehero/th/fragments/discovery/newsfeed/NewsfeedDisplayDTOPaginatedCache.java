package com.tradehero.th.fragments.discovery.newsfeed;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedDTOList;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedCache;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedDTOKey;
import rx.Observable;
import rx.functions.Func1;

class NewsfeedDisplayDTOPaginatedCache
        implements DTOCacheRx<NewsfeedPagedDTOKey, NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>>
{
    private final NewsfeedPagedCache newsfeedPagedCache;
    private BaseFetchDTOCacheRx cache;

    //<editor-fold desc="Constructors">
    public NewsfeedDisplayDTOPaginatedCache(NewsfeedPagedCache newsfeedPagedCache)
    {
        this.newsfeedPagedCache = newsfeedPagedCache;
    }
    //</editor-fold>

    @NonNull @Override
    public Observable<Pair<NewsfeedPagedDTOKey, NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>>> get(@NonNull NewsfeedPagedDTOKey key)
    {
        return newsfeedPagedCache.get(key)
                .map(new Func1<Pair<NewsfeedPagedDTOKey, NewsfeedDTOList>, Pair<NewsfeedPagedDTOKey, NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>>>()
                {
                    @Override public Pair<NewsfeedPagedDTOKey, NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>> call(
                            Pair<NewsfeedPagedDTOKey, NewsfeedDTOList> newsfeedPagedDTOKeyNewsfeedDTOListPair)
                    {
                        //TODO
                        return null;
                    }
                });
    }

    @Override public void onNext(NewsfeedPagedDTOKey key, NewsfeedDisplayDTO.DTOList value)
    {
        //Stubbed
    }

    @Override public void invalidate(@NonNull NewsfeedPagedDTOKey key)
    {
        newsfeedPagedCache.invalidate(key);
    }

    @Override public void invalidateAll()
    {
        newsfeedPagedCache.invalidateAll();
    }
}
