package com.tradehero.common.billing.amazon.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.identifier.BaseProductIdentifierFetcherHolderRx;

abstract public class BaseAmazonProductIdentifierFetcherHolderRx<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>>
    extends BaseProductIdentifierFetcherHolderRx<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType>
    implements AmazonProductIdentifierFetcherHolderRx<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType>
{
    //<editor-fold desc="Constructors">
    public BaseAmazonProductIdentifierFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected AmazonProductIdentifierFetcherRx<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType> createFetcher(int requestCode);
}
