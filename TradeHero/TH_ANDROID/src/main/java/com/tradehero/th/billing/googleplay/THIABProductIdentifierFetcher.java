package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BaseProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:58 PM To change this template use File | Settings | File Templates. */
public class THIABProductIdentifierFetcher extends BaseProductIdentifierFetcher<
        IABSKU,
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException>,
        IABException>
    implements IABProductIdentifierFetcher<
        IABSKU,
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException>,
        IABException>
{
    public static final String TAG = THIABProductIdentifierFetcher.class.getSimpleName();

    public THIABProductIdentifierFetcher()
    {
        super();
        // TODO hard-coded while there is nothing coming from the server.
        List<IABSKU> inAppIABSKUs = new ArrayList<>();
        List<IABSKU> subsIABSKUs = new ArrayList<>();
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T0_KEY));
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T1_KEY));
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T2_KEY));
        inAppIABSKUs.add(new IABSKU(THIABConstants.CREDIT_1));
        //inAppIABSKUs.add(new IABSKU(THIABConstants.CREDIT_5));
        inAppIABSKUs.add(new IABSKU(THIABConstants.CREDIT_10));
        inAppIABSKUs.add(new IABSKU(THIABConstants.CREDIT_20));

        subsIABSKUs.add(new IABSKU(THIABConstants.ALERT_1));
        subsIABSKUs.add(new IABSKU(THIABConstants.ALERT_5));
        subsIABSKUs.add(new IABSKU(THIABConstants.ALERT_UNLIMITED));

        inAppIABSKUs.add(new IABSKU(THIABConstants.RESET_PORTFOLIO_0));

        availableProductIdentifiers.put(IABConstants.ITEM_TYPE_INAPP, inAppIABSKUs);
        availableProductIdentifiers.put(IABConstants.ITEM_TYPE_SUBS, subsIABSKUs);
    }

    @Override public void fetchProductIdentifiers(int requestCode)
    {
        this.requestCode = requestCode;
        notifyListenerFetched();
    }

    @Override public Map<String, List<IABSKU>> fetchProductIdentifiersSync()
    {
        return Collections.unmodifiableMap(availableProductIdentifiers);
    }
}
