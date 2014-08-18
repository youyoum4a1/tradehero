package com.tradehero.common.billing;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;

public class BaseProductIdentifierList<ProductIdentifierType extends ProductIdentifier>
        extends BaseArrayList<ProductIdentifierType> implements DTO
{
    //<editor-fold desc="Constructors">
    public BaseProductIdentifierList()
    {
        super();
    }
    //</editor-fold>
}
