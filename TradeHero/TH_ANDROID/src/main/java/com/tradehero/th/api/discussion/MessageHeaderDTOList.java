package com.tradehero.th.api.discussion;

import java.util.ArrayList;
import java.util.Collection;

public class MessageHeaderDTOList extends ArrayList<MessageHeaderDTO>
{
    //<editor-fold desc="Constructors">
    public MessageHeaderDTOList(int i)
    {
        super(i);
    }

    public MessageHeaderDTOList()
    {
        super();
    }

    public MessageHeaderDTOList(Collection<? extends MessageHeaderDTO> messageHeaderDTOs)
    {
        super(messageHeaderDTOs);
    }
    //</editor-fold>
}
