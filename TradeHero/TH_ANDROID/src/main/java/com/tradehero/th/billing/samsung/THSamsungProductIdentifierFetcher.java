package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.BaseProductIdentifierFetcher;
import com.tradehero.common.billing.samsung.SamsungProductIdentifierFetcher;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.Collections;
import java.util.HashMap;

public class THSamsungProductIdentifierFetcher
    extends BaseProductIdentifierFetcher<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        SamsungException>
    implements SamsungProductIdentifierFetcher<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        SamsungException>
{
    protected HashMap<SamsungSKUListKey, SamsungSKUList> availableProductIdentifiers;

    public THSamsungProductIdentifierFetcher()
    {
        super();
        availableProductIdentifiers = new HashMap<>();

        SamsungSKUList consumables = new SamsungSKUList();
        consumables.add(new SamsungSKU(THSamsungConstants.IAP_ITEM_GROUP_ID, THSamsungConstants.EXTRA_CASH_T0_ITEM_ID));
        consumables.add(new SamsungSKU(THSamsungConstants.IAP_ITEM_GROUP_ID, THSamsungConstants.EXTRA_CASH_T1_ITEM_ID));
        consumables.add(new SamsungSKU(THSamsungConstants.IAP_ITEM_GROUP_ID, THSamsungConstants.EXTRA_CASH_T2_ITEM_ID));

        SamsungSKUList nonConsumables = new SamsungSKUList();

        SamsungSKUList subscriptions = new SamsungSKUList();

        SamsungSKUList all = new SamsungSKUList();
        all.addAll(consumables);
        all.addAll(nonConsumables);
        all.addAll(subscriptions);

        availableProductIdentifiers.put(SamsungSKUListKey.getConsumableKey(), consumables);
        availableProductIdentifiers.put(SamsungSKUListKey.getNonConsumableKey(), nonConsumables);
        availableProductIdentifiers.put(SamsungSKUListKey.getSubscriptionKey(), subscriptions);
        availableProductIdentifiers.put(SamsungSKUListKey.getAllKey(), all);
    }

    @Override public void fetchProductIdentifiers(int requestCode)
    {
        super.fetchProductIdentifiers(requestCode);
        notifyListenerFetched(Collections.unmodifiableMap(availableProductIdentifiers));
    }
}
