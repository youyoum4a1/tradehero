package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:03 PM To change this template use File | Settings | File Templates. */
public class SecurityIdList extends DTOKeyIdList<SecurityId>
{
    public static final String TAG = SecurityIdList.class.getSimpleName();

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
