package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.pagination.RangedDTO;
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
