package com.tradehero.common.billing.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BaseRequestCodeHolder;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import rx.Observable;

abstract public class BaseProductIdentifierFetcherHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
        extends BaseRequestCodeHolder<ProductIdentifierFetcherRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType>>
        implements ProductIdentifierFetcherHolderRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType>
{
    //<editor-fold desc="Constructors">
    public BaseProductIdentifierFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override
    public Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> get(int requestCode)
    {
        ProductIdentifierFetcherRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType> fetcher = actors.get(requestCode);
        if (fetcher == null)
        {
            fetcher = createFetcher(requestCode);
            actors.put(requestCode, fetcher);
        }
        return fetcher.get();
    }

    @NonNull abstract protected ProductIdentifierFetcherRx<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType> createFetcher(int requestCode);
}
