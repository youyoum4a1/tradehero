package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import java.util.Collection;

public class MessageHeaderIdList extends DTOKeyIdList<MessageHeaderId>
{
    //<editor-fold desc="Constructors">
    public MessageHeaderIdList()
    {
        super();
    }

    public MessageHeaderIdList(int capacity)
    {
        super(capacity);
    }

    public MessageHeaderIdList(Collection<? extends MessageHeaderId> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
