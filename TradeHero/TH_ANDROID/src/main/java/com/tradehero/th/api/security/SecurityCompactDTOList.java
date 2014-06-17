package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

public class SecurityCompactDTOList extends ArrayList<SecurityCompactDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public SecurityCompactDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public SecurityCompactDTOList()
    {
        super();
    }

    public SecurityCompactDTOList(Collection<? extends SecurityCompactDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
