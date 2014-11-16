package com.tradehero.th.billing.googleplay.identifier;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.identifier.BaseIABProductIdentifierFetcherHolderRx;
import com.tradehero.common.billing.googleplay.identifier.IABProductIdentifierFetcherRx;
import javax.inject.Inject;

public class THBaseIABProductIdentifierFetcherHolderRx
    extends BaseIABProductIdentifierFetcherHolderRx<
            IABSKUListKey,
            IABSKU,
            IABSKUList>
    implements THIABProductIdentifierFetcherHolderRx
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABProductIdentifierFetcherHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override protected IABProductIdentifierFetcherRx<IABSKUListKey, IABSKU, IABSKUList> createFetcher(int requestCode)
    {
        return new THBaseIABProductIdentifierFetcherRx(requestCode);
    }
}
