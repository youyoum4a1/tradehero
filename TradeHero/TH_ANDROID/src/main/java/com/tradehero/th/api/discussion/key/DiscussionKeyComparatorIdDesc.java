package com.tradehero.th.api.discussion.key;

import java.util.Comparator;

public class DiscussionKeyComparatorIdDesc implements Comparator<DiscussionKey>
{
    public DiscussionKeyComparatorIdDesc()
    {
        super();
    }

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
