package com.androidth.general.common.billing.identifier;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseProductIdentifierList;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductIdentifierListKey;
import com.androidth.general.common.billing.RequestCodeHolder;
import rx.Observable;

public interface ProductIdentifierFetcherHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>>
    extends RequestCodeHolder
{
    @NonNull Observable<ProductIdentifierListResult<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType>> get(int requestCode);
}
