package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

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
