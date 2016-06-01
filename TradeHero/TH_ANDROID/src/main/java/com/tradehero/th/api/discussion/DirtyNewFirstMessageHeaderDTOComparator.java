package com.ayondo.academy.api.discussion;

import java.io.Serializable;
import java.util.Comparator;

public class DirtyNewFirstMessageHeaderDTOComparator implements Comparator<MessageHeaderDTO>, Serializable
{
    @Override public int compare(MessageHeaderDTO o1, MessageHeaderDTO o2)
    {
        return o2.latestMessageAtUtc.compareTo(o1.latestMessageAtUtc);
    }
}
