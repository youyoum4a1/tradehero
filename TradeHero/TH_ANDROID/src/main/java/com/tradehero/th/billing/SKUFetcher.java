package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:15 PM To change this template use File | Settings | File Templates. */
public interface SKUFetcher<
        ProductIdentifierType extends ProductIdentifier,
        OnSKUFetchedListenerType extends SKUFetcher.OnSKUFetchedListener<ProductIdentifierType>>
{
    int getRequestCode();
    OnSKUFetchedListenerType getListener();
    void setListener(OnSKUFetchedListenerType listener);
    void fetchSkus(int requestCode);
    Map<String, List<ProductIdentifierType>> fetchSkusSync();

    public static interface OnSKUFetchedListener<ProductIdentifierType extends ProductIdentifier>
    {
        void onFetchedSKUs(int requestCode, Map<String, List<ProductIdentifierType>> availableSkus);
        void onFetchSKUsFailed(int requestCode, Exception exception);
    }
}
