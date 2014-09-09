package com.tradehero.th.persistence.education;

import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.api.education.VideoId;
import com.tradehero.th.api.pagination.PaginatedDTO;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class PaginatedVideoId extends PaginatedDTO<VideoId>
{
    PaginatedVideoId(@NotNull VideoCache videoCache, @NotNull PaginatedVideoDTO paginatedVideoDTO)
    {
        List<VideoId> ids = null;
        List<VideoDTO> data = paginatedVideoDTO.getData();
        if (data != null)
        {
            ids = new ArrayList<>();
            for (VideoDTO video: data)
            {
                videoCache.put(video.getVideoId(), video);
                ids.add(video.getVideoId());
            }
        }
        setData(ids);
        setPagination(paginatedVideoDTO.getPagination());
    }

    @NotNull PaginatedVideoDTO create(@NotNull VideoCache videoCache)
    {
        PaginatedVideoDTO created = new PaginatedVideoDTO();
        created.setPagination(getPagination());
        List<VideoDTO> data = null;
        List<VideoId> ids = getData();
        if (ids != null)
        {
            data = new ArrayList<>();
            for (@NotNull VideoId id : ids)
            {
                data.add(videoCache.get(id));
            }

        }
        created.setData(data);
        return created;
    }
}
