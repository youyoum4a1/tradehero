package com.ayondo.academy.api.discussion.newsfeed;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;

public class NewsfeedDTOList extends BaseArrayList<NewsfeedDTO> implements DTO
{
    public NewsfeedDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }
}
