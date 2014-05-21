package com.tradehero.common.persistence;

import java.util.ArrayList;
import java.util.Collection;

public class DTOKeyIdList<ListedDTOKeyType extends DTOKey>
        extends ArrayList<ListedDTOKeyType>
        implements DTO
{
    //<editor-fold desc="Constructors">
    public DTOKeyIdList()
    {
        super();
    }

    public DTOKeyIdList(int capacity)
    {
        super(capacity);
    }

    public DTOKeyIdList(Collection<? extends ListedDTOKeyType> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
