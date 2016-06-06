package com.androidth.general.api.education;

import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.api.pagination.PaginatedDTO;
import java.util.List;

public class PaginatedVideoCategoryDTO extends PaginatedDTO<VideoCategoryDTO>
    implements ContainerDTO<VideoCategoryDTO, VideoCategoryDTOList>
{

    @Override public int size()
    {
        List<VideoCategoryDTO> list = getData();
        if (list == null)
        {
            return 0;
        }
        return list.size();
    }

    @Override public VideoCategoryDTOList getList()
    {
        return new VideoCategoryDTOList(getData());
    }
}
