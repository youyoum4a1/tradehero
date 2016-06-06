package com.androidth.general.api.discussion.newsfeed;

import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;

public class NewsfeedDTOList extends BaseArrayList<NewsfeedDTO> implements DTO
{
    public NewsfeedDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }
}
