package com.tradehero.th.api.discussion;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.users.UserBaseKey;

public abstract class AbstractDiscussionDTO extends AbstractDiscussionCompactDTO
{
    public int userId;
    public String text;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> AbstractDiscussionDTO(ExtendedDTOType other, Class<? extends AbstractDiscussionDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    public UserBaseKey getSenderKey()
    {
        return new UserBaseKey(userId);
    }

    @Override public String toString()
    {
        return "AbstractDiscussionDTO{" +
                super.toString() +
                ", userId=" + userId +
                ", text='" + text + '\'' +
                '}';
    }
}
