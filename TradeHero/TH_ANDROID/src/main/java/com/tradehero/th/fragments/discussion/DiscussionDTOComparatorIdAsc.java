package com.tradehero.th.fragments.discussion;

import java.io.Serializable;
import java.util.Comparator;

public class DiscussionDTOComparatorIdAsc implements Comparator<AbstractDiscussionCompactItemViewLinear.DTO>, Serializable
{
    @Override public int compare(AbstractDiscussionCompactItemViewLinear.DTO o1,
            AbstractDiscussionCompactItemViewLinear.DTO o2)
    {
        if (o1 == o2)
        {
            return 0;
        }
        if (o1 == null)
        {
            return 1;
        }
        return Integer.valueOf(o1.viewHolderDTO.discussionDTO.id).compareTo(o2.viewHolderDTO.discussionDTO.id);
    }
}