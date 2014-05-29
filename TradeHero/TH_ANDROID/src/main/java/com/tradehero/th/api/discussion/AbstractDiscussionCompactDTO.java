package com.tradehero.th.api.discussion;

import com.tradehero.th.api.ExtendedDTO;
import java.util.Date;

public class AbstractDiscussionCompactDTO extends ExtendedDTO
{
    public int id;
    public Date createdAtUtc;
    public int upvoteCount;
    public int downvoteCount;
    public int voteDirection; //-1: down, 0: cancel, 1: up
    public int commentCount;
    public String langCode;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO>
    AbstractDiscussionCompactDTO(ExtendedDTOType other,
            Class<? extends AbstractDiscussionCompactDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>
}
