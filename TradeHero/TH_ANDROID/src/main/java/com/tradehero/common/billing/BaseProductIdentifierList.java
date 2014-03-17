package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:32 PM To change this template use File | Settings | File Templates. */
public class BaseProductIdentifierList<ProductIdentifierType extends ProductIdentifier>
        extends ArrayList<ProductIdentifierType> implements DTO
{
    //<editor-fold desc="Constructors">
    public BaseProductIdentifierList()
    {
        super();
    }

    public BaseProductIdentifierList(int capacity)
    {
        super(capacity);
    }

    public BaseProductIdentifierList(Collection<? extends ProductIdentifierType> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
