package com.ayondo.academy.api.discussion.key;

import java.io.Serializable;
import java.util.Comparator;

public class DiscussionKeyComparatorIdDesc implements Comparator<DiscussionKey>, Serializable
{
    @Override public int compare(DiscussionKey o1, DiscussionKey o2)
    {
        if (o1 == o2)
        {
            return 0;
        }
        if (o1 == null)
        {
            return -1;
        }
        return o2.id.compareTo(o1.id);
    }
}
