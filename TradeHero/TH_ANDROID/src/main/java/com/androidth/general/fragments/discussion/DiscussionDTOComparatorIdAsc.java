package com.androidth.general.fragments.discussion;

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
        return o2.viewHolderDTO.discussionDTO.createdAtUtc.compareTo(o1.viewHolderDTO.discussionDTO.createdAtUtc);
    }
}
