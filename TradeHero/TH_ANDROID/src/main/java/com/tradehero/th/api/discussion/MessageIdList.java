package com.tradehero.th.api.discussion;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.discussion.key.MessageId;
import java.util.Collection;

/**
 * Created by wangliang on 14-4-4.
 *
 * list of message id
 */
public class MessageIdList extends DTOKeyIdList<MessageId>
{
    public MessageIdList()
    {
        super();
    }

    public MessageIdList(int capacity)
    {
        super(capacity);
    }

    public MessageIdList(Collection<? extends MessageId> collection)
    {
        super(collection);
    }
}
