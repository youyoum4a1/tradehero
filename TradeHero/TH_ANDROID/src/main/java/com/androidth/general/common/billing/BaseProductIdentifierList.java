package com.androidth.general.common.billing;

import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.DTO;

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
