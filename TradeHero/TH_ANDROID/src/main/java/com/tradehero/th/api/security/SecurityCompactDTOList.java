package com.tradehero.th.api.security;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;

public class SecurityCompactDTOList extends BaseArrayList<SecurityCompactDTO>
    implements DTO, ContainerDTO<SecurityCompactDTO, SecurityCompactDTOList>
{
    //<editor-fold desc="Constructors">
    public SecurityCompactDTOList()
    {
        super();
    }

    @Override public SecurityCompactDTOList getList()
    {
        return this;
    }
    //</editor-fold>
}
