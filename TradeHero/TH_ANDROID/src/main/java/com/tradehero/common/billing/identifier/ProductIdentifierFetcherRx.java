package com.tradehero.common.billing.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.RequestCodeActor;
import rx.Observable;

public interface ProductIdentifierFetcherRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends RequestCodeActor
{
    @NonNull Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> get();
}
