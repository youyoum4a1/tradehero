package com.tradehero.th.api.users;

import com.tradehero.common.api.BaseArrayList;
import org.jetbrains.annotations.NotNull;

public class UserSearchResultDTOList extends BaseArrayList<UserSearchResultDTO>
{
    //<editor-fold desc="Constructors">
    public UserSearchResultDTOList()
    {
        super();
    }
    //</editor-fold>

    @NotNull public UserBaseKeyList createKeys()
    {
        UserBaseKeyList keyList = new UserBaseKeyList();
        for (@NotNull UserSearchResultDTO userSearchResultDTO : this)
        {
            keyList.add(userSearchResultDTO.getUserBaseKey());
        }
        return keyList;
    }
}
