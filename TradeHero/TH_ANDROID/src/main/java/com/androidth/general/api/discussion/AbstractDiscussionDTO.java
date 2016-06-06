package com.androidth.general.api.discussion;

import android.support.annotation.Nullable;
import com.androidth.general.api.ExtendedDTO;
import com.androidth.general.api.users.UserBaseKey;

public abstract class AbstractDiscussionDTO<T extends AbstractDiscussionDTO> extends AbstractDiscussionCompactDTO<T>
{
    public int userId;
    @Nullable public String text;

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
