package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

public class SecurityIdList extends DTOKeyIdList<SecurityId>
{
    //<editor-fold desc="Constructors">
    public SecurityIdList()
    {
        super();
    }

    public SecurityIdList(int capacity)
    {
        super(capacity);
    }

    public SecurityIdList(Collection<? extends SecurityId> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
