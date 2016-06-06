package com.androidth.general.common.billing.googleplay.identifier;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.BaseIABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.identifier.BaseProductIdentifierFetcherHolderRx;

abstract public class BaseIABProductIdentifierFetcherHolderRx<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>>
    extends BaseProductIdentifierFetcherHolderRx<
            IABSKUListKeyType,
            IABSKUType,
            IABSKUListType>
    implements IABProductIdentifierFetcherHolderRx<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType>
{
    //<editor-fold desc="Constructors">
    public BaseIABProductIdentifierFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected IABProductIdentifierFetcherRx<IABSKUListKeyType, IABSKUType, IABSKUListType> createFetcher(int requestCode);
}
