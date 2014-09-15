package com.tradehero.th.persistence.education;

import com.tradehero.th.api.education.PaginatedVideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.api.pagination.PaginatedDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class PaginatedVideoCategoryId extends PaginatedDTO<VideoCategoryId>
{
    PaginatedVideoCategoryId(
            @NotNull VideoCategoryCache videoCategoryCache,
            @NotNull PaginatedVideoCategoryDTO paginatedVideoCategoryDTO)
    {
        List<VideoCategoryId> ids = null;
        List<VideoCategoryDTO> data = paginatedVideoCategoryDTO.getData();
        if (data != null)
        {
            ids = new ArrayList<>();
            for (VideoCategoryDTO videoCategory: data)
            {
                videoCategoryCache.put(videoCategory.getVideoCategoryId(), videoCategory);
                ids.add(videoCategory.getVideoCategoryId());
            }
        }
        setData(ids);
        setPagination(paginatedVideoCategoryDTO.getPagination());
    }

    @NotNull PaginatedVideoCategoryDTO create(@NotNull VideoCategoryCache videoCategoryCache)
    {
        PaginatedVideoCategoryDTO created = new PaginatedVideoCategoryDTO();
        created.setPagination(getPagination());
        List<VideoCategoryDTO> data = null;
        List<VideoCategoryId> ids = getData();
        if (ids != null)
        {
            data = new ArrayList<>();
            for (@NotNull VideoCategoryId id : ids)
            {
                data.add(videoCategoryCache.get(id));
            }

        }
        created.setData(data);
        return created;
    }
}
