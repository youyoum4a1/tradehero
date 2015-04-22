package com.tradehero.common.billing.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import java.util.Collections;
import java.util.Map;

public class ProductIdentifierListResult<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
    extends BaseResult
{
    @NonNull public final Map<ProductIdentifierListKeyType, ProductIdentifierListType> mappedIds;

    //<editor-fold desc="Constructors">
    public ProductIdentifierListResult(int requestCode,
            @NonNull Map<ProductIdentifierListKeyType, ProductIdentifierListType> mappedIds)
    {
        super(requestCode);
        this.mappedIds = Collections.unmodifiableMap(mappedIds);
    }
    //</editor-fold>
}
