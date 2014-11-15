package com.tradehero.common.billing.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import rx.Observable;
import rx.subjects.BehaviorSubject;

abstract public class BaseProductIdentifierFetcherRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        implements ProductIdentifierFetcherRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType>
{
    protected int requestCode;
    protected BehaviorSubject<ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>> subject;
    protected Observable<ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>>
            replayObservable;

    //<editor-fold desc="Constructors">
    public BaseProductIdentifierFetcherRx(int requestCode)
    {
        super();
        this.requestCode = requestCode;
        this.subject = BehaviorSubject.create();
        this.replayObservable = subject.replay().publish();
    }
    //</editor-fold>

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override @NonNull public Observable<ProductIdentifierListResult<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType>> get()
    {
        return replayObservable;
    }

    @NonNull protected ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType> createResult(ProductIdentifierListKeyType type, ProductIdentifierListType productIdentifiers)
    {
        return new ProductIdentifierListResult<>(requestCode, type, productIdentifiers);
    }
}
