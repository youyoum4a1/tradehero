package com.tradehero.common.billing.googleplay.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.identifier.BaseProductIdentifierFetcherHolderRx;

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
