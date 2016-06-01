package com.ayondo.academy.fragments.discussion;

import java.io.Serializable;
import java.util.Comparator;

public class AbstractDiscussionCompactItemViewDTODateComparator implements Comparator<AbstractDiscussionCompactItemViewLinear.DTO>, Serializable
{
    private final boolean ascending;

    public AbstractDiscussionCompactItemViewDTODateComparator()
    {
        this(true);
    }

    public AbstractDiscussionCompactItemViewDTODateComparator(boolean ascending)
    {
        super();
        this.ascending = ascending;
    }

    @Override public int compare(AbstractDiscussionCompactItemViewLinear.DTO o1,
            AbstractDiscussionCompactItemViewLinear.DTO o2)
    {
        if (o1 == o2)
        {
            return 0;
        }
        if (o1 == null)
        {
            return ascending ? 1 : -1;
        }
        int compared = o2.viewHolderDTO.discussionDTO.createdAtUtc.compareTo(o1.viewHolderDTO.discussionDTO.createdAtUtc);
        return ascending ? compared : -compared;
    }
}
