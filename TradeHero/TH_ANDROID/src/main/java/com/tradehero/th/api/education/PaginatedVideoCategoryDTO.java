package com.tradehero.th.api.education;

import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
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
