package com.ayondo.academy.api.discussion;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;

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
