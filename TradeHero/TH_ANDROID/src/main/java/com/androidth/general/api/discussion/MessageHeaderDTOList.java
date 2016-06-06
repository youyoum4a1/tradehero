package com.androidth.general.api.discussion;

import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;

public class MessageHeaderDTOList extends BaseArrayList<MessageHeaderDTO>
    implements DTO
{
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 300;

    //<editor-fold desc="Constructors">
    public MessageHeaderDTOList()
    {
        super(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }
    //</editor-fold>
}
