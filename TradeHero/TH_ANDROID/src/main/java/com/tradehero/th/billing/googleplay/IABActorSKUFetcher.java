package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.ActorSKUFetcher;
import com.tradehero.th.billing.SKUFetcher;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface IABActorSKUFetcher<
        IABSKUType extends IABSKU,
        OnSKUFetchedListenerType extends SKUFetcher.OnSKUFetchedListener<IABSKUType>>
    extends ActorSKUFetcher<IABSKUType, OnSKUFetchedListenerType>
{
}
