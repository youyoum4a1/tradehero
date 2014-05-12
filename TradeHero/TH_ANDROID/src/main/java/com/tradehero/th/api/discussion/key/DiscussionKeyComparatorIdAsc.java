package com.tradehero.th.api.discussion.key;

import java.util.Comparator;

public class DiscussionKeyComparatorIdAsc implements Comparator<DiscussionKey>
{
    public DiscussionKeyComparatorIdAsc()
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
            return 1;
        }
        return o1.id.compareTo(o2.id);
    }
}
