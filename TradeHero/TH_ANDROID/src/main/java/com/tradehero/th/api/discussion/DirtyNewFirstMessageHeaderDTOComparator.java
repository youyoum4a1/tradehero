package com.tradehero.th.api.discussion;

import java.util.Comparator;

public class DirtyNewFirstMessageHeaderDTOComparator implements Comparator<MessageHeaderDTO>
{
    @Override public int compare(MessageHeaderDTO o1, MessageHeaderDTO o2)
    {
        return o2.id.compareTo(o1.id);
    }
}
