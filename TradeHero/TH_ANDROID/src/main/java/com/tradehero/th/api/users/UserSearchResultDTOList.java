package com.ayondo.academy.api.users;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;

public class UserSearchResultDTOList extends BaseArrayList<UserSearchResultDTO>
    implements DTO, ContainerDTO<UserSearchResultDTO, UserSearchResultDTOList>
{
    @Override public UserSearchResultDTOList getList()
    {
        return this;
    }
}
