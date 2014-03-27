package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.listener.OnGetItemListener;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public interface SamsungProductIdentifierFetcher<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungExceptionType extends SamsungException>
    extends ProductIdentifierFetcher<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungExceptionType>,
        OnGetItemListener
{
}
