package com.androidth.general.common.billing.samsung.identifier;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.identifier.BaseProductIdentifierFetcherHolderRx;
import com.androidth.general.common.billing.samsung.BaseSamsungSKUList;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.SamsungSKUListKey;

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

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
