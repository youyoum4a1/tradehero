package com.tradehero.th.persistence.discussion;

import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractDiscussionCutDTO extends AbstractDiscussionCompactCutDTO
{
    public int userId;
    @Nullable public String text;

    protected AbstractDiscussionCutDTO(@NotNull AbstractDiscussionDTO abstractDiscussionDTO)
    {
        super(abstractDiscussionDTO);
        this.userId = abstractDiscussionDTO.userId;
        this.text = abstractDiscussionDTO.text;
    }

    final boolean populate(@NotNull AbstractDiscussionDTO inflated)
    {
        if (!super.populate(inflated))
        {
            return false;
        }
        inflated.userId = userId;
        inflated.text = text;
        return true;
    }
}
