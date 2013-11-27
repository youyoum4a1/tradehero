package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.SKUFetcher;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public interface IABSKUFetcher<
        IABSKUType extends IABSKU,
        OnSKUFetchedListenerType extends SKUFetcher.OnSKUFetchedListener<IABSKUType>>
    extends SKUFetcher<IABSKUType, OnSKUFetchedListenerType>
{
}
