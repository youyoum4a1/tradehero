package com.tradehero.th.api.discussion;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.PrivateMessageKey;

public class PrivateDiscussionDTO extends DiscussionDTO
{
    //<editor-fold desc="Constructors">
    public PrivateDiscussionDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> PrivateDiscussionDTO(ExtendedDTOType other, Class<? extends ExtendedDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    @Override public void putAll(ExtendedDTO other, Class<? extends ExtendedDTO> myClass)
    {
        super.putAll(other, myClass);
    }

    @Override public PrivateMessageKey getDiscussionKey()
    {
        return new PrivateMessageKey(id);
    }

    @Override public DiscussionKey getParentDiscussionKey()
    {
        if (inReplyToId > 0)
        {
            return new PrivateMessageKey(inReplyToId);
        }
        // The assumption here is that this was the first comment of the discussion
        return getDiscussionKey();
    }

    @Override public String toString()
    {
        return "PrivateDiscussionDTO{" + super.toString() + '}';
    }
}
