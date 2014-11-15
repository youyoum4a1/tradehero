package com.tradehero.common.billing.identifier;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;

public class ProductIdentifierListResult<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
    extends BaseResult
{
    public final ProductIdentifierListKeyType type;
    public final ProductIdentifierListType productIdentifiers;

    //<editor-fold desc="Constructors">
    public ProductIdentifierListResult(int requestCode, ProductIdentifierListKeyType type, ProductIdentifierListType productIdentifiers)
    {
        super(requestCode);
        this.type = type;
        this.productIdentifiers = productIdentifiers;
    }
    //</editor-fold>
}
