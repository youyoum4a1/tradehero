package com.tradehero.th.fragments.social;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import rx.Observable;
import rx.functions.Func1;

class RelationItemDisplayPaginatedCache
        implements DTOCacheRx<SearchAllowableRecipientListType, RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO>>
{
    @NonNull private final AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCacheRx;

    //<editor-fold desc="Constructors">
    public RelationItemDisplayPaginatedCache(
            @NonNull AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCacheRx)
    {
        this.allowableRecipientPaginatedCacheRx = allowableRecipientPaginatedCacheRx;
    }
    //</editor-fold>

    @NonNull @Override public Observable<Pair<SearchAllowableRecipientListType, RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO>>> get(
            @NonNull final SearchAllowableRecipientListType key)
    {
        return allowableRecipientPaginatedCacheRx.get(key).map(
                new Func1<Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO>, Pair<SearchAllowableRecipientListType, RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO>>>()
                {
                    @Override public Pair<SearchAllowableRecipientListType, RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO>> call(
                            Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO> pair)
                    {
                        RelationItemDisplayDTO.DTOList<RelationItemDisplayDTO> relationItemDisplayDTOs = new RelationItemDisplayDTO.DTOList<>();

                        int currentPage = pair.first.page != null? pair.first.page : 0;
                        int currentOrder = currentPage * (pair.first.perPage != null? pair.first.perPage : 0);
                        for (AllowableRecipientDTO allowableRecipientDTO: pair.second.getList())
                        {
                            relationItemDisplayDTOs.add(new RelationItemDisplayDTO(currentOrder, allowableRecipientDTO, allowableRecipientDTO.user.displayName, allowableRecipientDTO.user.picture));
                            currentOrder++;
                        }
                        return Pair.create(pair.first
                                , relationItemDisplayDTOs);
                    }
                });
    }

    @Override public void onNext(SearchAllowableRecipientListType key, RelationItemDisplayDTO.DTOList value)
    {
    }

    @Override public void invalidate(@NonNull SearchAllowableRecipientListType key)
    {
        allowableRecipientPaginatedCacheRx.invalidate(key);
    }

    @Override public void invalidateAll()
    {
        allowableRecipientPaginatedCacheRx.invalidateAll();
    }
}
