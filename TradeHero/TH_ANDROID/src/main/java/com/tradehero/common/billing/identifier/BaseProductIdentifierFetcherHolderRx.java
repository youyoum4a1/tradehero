package com.tradehero.common.billing.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;

abstract public class BaseProductIdentifierFetcherHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
    implements ProductIdentifierFetcherHolderRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType>
{
    @NonNull protected final Map<Integer /*requestCode*/, ProductIdentifierFetcherRx<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> fetchers;

    //<editor-fold desc="Constructors">
    public BaseProductIdentifierFetcherHolderRx()
    {
        super();
        fetchers = new HashMap<>();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        fetchers.clear();
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return !fetchers.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        fetchers.remove(requestCode);
    }

    @NonNull @Override
    public Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> get(int requestCode)
    {
        ProductIdentifierFetcherRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType> fetcher = fetchers.get(requestCode);
        if (fetcher == null)
        {
            fetcher = createFetcher(requestCode);
            fetchers.put(requestCode, fetcher);
        }
        return fetcher.get();
    }

    abstract protected ProductIdentifierFetcherRx<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType> createFetcher(int requestCode);
}
