package com.tradehero.common.billing.googleplay.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.identifier.BaseProductIdentifierFetcherHolderRx;
import com.tradehero.common.billing.identifier.ProductIdentifierFetcherRx;

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

    @Override public void onDestroy()
    {
        for (ProductIdentifierFetcherRx<IABSKUListKeyType, IABSKUType, IABSKUListType> actor : actors.values())
        {
            ((IABProductIdentifierFetcherRx<IABSKUListKeyType, IABSKUType, IABSKUListType>) actor).onDestroy();
        }
        super.onDestroy();
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        IABProductIdentifierFetcherRx<IABSKUListKeyType, IABSKUType, IABSKUListType> actor = (IABProductIdentifierFetcherRx<IABSKUListKeyType, IABSKUType, IABSKUListType>) actors.get(requestCode);
        if (actor != null)
        {
            actor.onDestroy();
        }
        super.forgetRequestCode(requestCode);
    }
}
