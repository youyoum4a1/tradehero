package com.tradehero.common.billing.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BaseRequestCodeReplayActor;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import rx.Observable;

abstract public class BaseProductIdentifierFetcherRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends BaseRequestCodeReplayActor<ProductIdentifierListResult<ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType>>
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

    @Override @NonNull
    public Observable<ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>> get()
    {
        return replayObservable;
    }

    @NonNull protected ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType> createResult(ProductIdentifierListKeyType type, ProductIdentifierListType productIdentifiers)
    {
        return new ProductIdentifierListResult<>(getRequestCode(), type, productIdentifiers);
    }
}
