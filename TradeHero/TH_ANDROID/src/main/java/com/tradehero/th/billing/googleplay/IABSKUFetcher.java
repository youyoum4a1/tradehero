package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public interface IABSKUFetcher<IABSKUType extends IABSKU>
{
    int getRequestCode();
    OnSKUFetchedListener<IABSKUType> getListener();
    void setListener(OnSKUFetchedListener<IABSKUType> listener);
    void fetchSkus(int requestCode);
    Map<String, List<IABSKUType>> fetchSkusSync();

    public static interface OnSKUFetchedListener<IABSKUType extends IABSKU>
    {
        void onFetchedSKUs(int requestCode, Map<String, List<IABSKUType>> availableSkus);
        void onFetchSKUsFailed(int requestCode, Exception exception);
    }
}
