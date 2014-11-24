package com.tradehero.th.api.users;

import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;

public class UserSearchResultDTOList extends BaseArrayList<UserSearchResultDTO>
    implements DTO, ContainerDTO<UserSearchResultDTO, UserSearchResultDTOList>
{
    //<editor-fold desc="Constructors">
    public UserSearchResultDTOList()
    {
        super();
    }
    //</editor-fold>

    @NonNull public UserBaseKeyList createKeys()
    {
        UserBaseKeyList keyList = new UserBaseKeyList();
        for (UserSearchResultDTO userSearchResultDTO : this)
        {
            keyList.add(userSearchResultDTO.getUserBaseKey());
        }
        return keyList;
    }

    @Override public UserSearchResultDTOList getList()
    {
        return this;
    }
}
