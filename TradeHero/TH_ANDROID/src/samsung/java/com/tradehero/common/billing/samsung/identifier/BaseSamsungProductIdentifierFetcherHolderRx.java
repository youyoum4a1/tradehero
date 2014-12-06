package com.tradehero.common.billing.samsung.identifier;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.identifier.BaseProductIdentifierFetcherHolderRx;
import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;

abstract public class BaseSamsungProductIdentifierFetcherHolderRx<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>>
        extends BaseProductIdentifierFetcherHolderRx<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType>
        implements SamsungProductIdentifierFetcherHolderRx<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType>
{
    //<editor-fold desc="Constructors">
    public BaseSamsungProductIdentifierFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected SamsungProductIdentifierFetcherRx<SamsungSKUListKeyType, SamsungSKUType, SamsungSKUListType> createFetcher(
            int requestCode);

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
