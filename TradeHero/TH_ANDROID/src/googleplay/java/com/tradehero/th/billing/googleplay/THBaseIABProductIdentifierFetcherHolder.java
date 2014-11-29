package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABProductIdentifierFetcherHolder
    extends BaseIABProductIdentifierFetcherHolder<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductIdentifierFetcher,
        IABException>
    implements THIABProductIdentifierFetcherHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABProductIdentifierFetcherHolder()
    {
        super();
    }
    //</editor-fold>

    @Override protected THIABProductIdentifierFetcher createProductIdentifierFetcher(int requestCode)
    {
        return new THBaseIABProductIdentifierFetcher(requestCode);
    }
}
