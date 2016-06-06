package com.androidth.general.api.discussion;

import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.pagination.RangedDTO;
import java.util.List;

public class RangedDiscussionKeyList extends RangedDTO<DiscussionKey, DiscussionKeyList>
    implements DTO
{
    public RangedDiscussionKeyList(RangedDTO<AbstractDiscussionDTO, DiscussionDTOList<AbstractDiscussionDTO>> dtos)
    {
        setDataFrom(dtos.getData());
        setSequenceDTO(dtos.getSequenceDTO());
    }

    public void setDataFrom(List<AbstractDiscussionDTO> data)
    {
        setData(new DiscussionDTOList<>(data));
    }

    public void setData(DiscussionDTOList data)
    {
        setData(data.getKeys());
    }
}
