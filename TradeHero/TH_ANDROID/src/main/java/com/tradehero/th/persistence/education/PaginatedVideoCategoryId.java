package com.tradehero.th.persistence.education;

import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.api.pagination.PaginatedDTO;
import java.util.ArrayList;
import java.util.List;
import android.support.annotation.NonNull;

class PaginatedVideoCategoryId extends PaginatedDTO<VideoCategoryId>
{
    PaginatedVideoCategoryId(
            @NonNull VideoCategoryCacheRx videoCategoryCache,
            @NonNull PaginatedVideoCategoryDTO paginatedVideoCategoryDTO)
    {
        List<VideoCategoryId> ids = null;
        List<VideoCategoryDTO> data = paginatedVideoCategoryDTO.getData();
        if (data != null)
        {
            ids = new ArrayList<>();
            for (VideoCategoryDTO videoCategory: data)
            {
                videoCategoryCache.onNext(videoCategory.getVideoCategoryId(), videoCategory);
                ids.add(videoCategory.getVideoCategoryId());
            }
        }
        setData(ids);
        setPagination(paginatedVideoCategoryDTO.getPagination());
    }

    @NonNull PaginatedVideoCategoryDTO create(@NonNull VideoCategoryCacheRx videoCategoryCache)
    {
        PaginatedVideoCategoryDTO created = new PaginatedVideoCategoryDTO();
        created.setPagination(getPagination());
        List<VideoCategoryDTO> data = null;
        List<VideoCategoryId> ids = getData();
        if (ids != null)
        {
            data = new ArrayList<>();
            for (VideoCategoryId id : ids)
            {
                data.add(videoCategoryCache.getValue(id));
            }

        }
        created.setData(data);
        return created;
    }
}
