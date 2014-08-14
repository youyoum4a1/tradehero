package com.tradehero.th.persistence.discussion;

import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PrivateDiscussionCutDTO extends DiscussionCutDTO
{
    PrivateDiscussionCutDTO(@NotNull PrivateDiscussionDTO privateDiscussionDTO)
    {
        super(privateDiscussionDTO);
    }

    @Nullable @Override PrivateDiscussionDTO inflate()
    {
        PrivateDiscussionDTO inflated = new PrivateDiscussionDTO();
        if (!populate(inflated))
        {
            return null;
        }
        return inflated;
    }
}
