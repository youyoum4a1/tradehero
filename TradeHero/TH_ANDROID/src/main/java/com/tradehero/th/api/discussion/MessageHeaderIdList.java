package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MessageHeaderIdList extends DTOKeyIdList<MessageHeaderId>
{
    //<editor-fold desc="Constructors">
    public MessageHeaderIdList()
    {
        super();
    }

    public MessageHeaderIdList(List<MessageHeaderDTO> messageHeaderDTOList)
    {
        super();
        for (@NotNull MessageHeaderDTO messageHeaderDTO : messageHeaderDTOList)
        {
            add(messageHeaderDTO.getDTOKey());
        }
    }
    //</editor-fold>
}
