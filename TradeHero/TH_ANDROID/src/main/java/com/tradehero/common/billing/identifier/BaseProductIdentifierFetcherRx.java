package com.tradehero.common.billing.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BaseRequestCodeActor;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import java.util.Map;

abstract public class BaseProductIdentifierFetcherRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends BaseRequestCodeActor
        implements ProductIdentifierFetcherRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType>
{
    //<editor-fold desc="Constructors">
    public BaseProductIdentifierFetcherRx(int requestCode)
    {
        super(requestCode);
    }
    //</editor-fold>

    @NonNull protected ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>
    createResult(
            @NonNull Map<ProductIdentifierListKeyType, ProductIdentifierListType> mapped)
    {
        return new ProductIdentifierListResult<>(getRequestCode(), mapped);
    }
}
