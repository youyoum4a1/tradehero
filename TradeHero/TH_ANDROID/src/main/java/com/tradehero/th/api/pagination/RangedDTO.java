package com.tradehero.th.api.pagination;

import com.tradehero.common.persistence.DTO;
import java.util.Collections;
import java.util.List;

public class RangedDTO<ListedType, ListType extends List<ListedType>> implements DTO
{
    private ListType data;
    private RangeSequenceDTO sequenceDTO;

    public ListType getDataModifiable()
    {
        return data;
    }

    public List<ListedType> getData()
    {
        return Collections.unmodifiableList(data);
    }

    public void setData(ListType data)
    {
        this.data = data;
    }

    public RangeSequenceDTO getSequenceDTO()
    {
        return sequenceDTO;
    }

    public void setSequenceDTO(RangeSequenceDTO sequenceDTO)
    {
        this.sequenceDTO = sequenceDTO;
    }
}
