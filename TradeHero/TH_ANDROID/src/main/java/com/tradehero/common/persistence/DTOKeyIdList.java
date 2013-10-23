package com.tradehero.common.persistence;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 6:55 PM To change this template use File | Settings | File Templates.
 *
 * The purpose of this class is to avoid duplicating DTO information in different parts of the same cache.
 * Instead of storing a list of DTOs, whose individual elements may be found in other lists, we store a list of ids, which in turn
 * point to DTOs found in another cache.
 */
public class DTOKeyIdList<CacheDTOKeyType extends DTOKey, ListedDTOKeyType extends DTOKey>
        extends ArrayList<ListedDTOKeyType>
        implements DTO
{
    public static final String TAG = DTOKeyIdList.class.getSimpleName();

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
