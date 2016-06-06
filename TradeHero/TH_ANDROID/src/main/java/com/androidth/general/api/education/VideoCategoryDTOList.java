package com.androidth.general.api.education;

import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;
import java.util.Collection;

public class VideoCategoryDTOList extends BaseArrayList<VideoCategoryDTO> implements DTO
{
    public VideoCategoryDTOList()
    {
        super();
    }

    public VideoCategoryDTOList(Collection<? extends VideoCategoryDTO> c)
    {
        super(c);
    }
}
