package com.androidth.general.api.users;

import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;

public class UserSearchResultDTOList extends BaseArrayList<UserSearchResultDTO>
    implements DTO, ContainerDTO<UserSearchResultDTO, UserSearchResultDTOList>
{
    @Override public UserSearchResultDTOList getList()
    {
        return this;
    }
}
