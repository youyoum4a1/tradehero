package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BaseProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class THIABProductIdentifierFetcher
    extends BaseProductIdentifierFetcher<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        IABException>
    implements IABProductIdentifierFetcher<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        IABException>
{
    protected Map<IABSKUListKey, IABSKUList> availableProductIdentifiers;

    public THIABProductIdentifierFetcher()
    {
        super();
        availableProductIdentifiers = new HashMap<>();

        // TODO hard-coded while there is nothing coming from the server.
        IABSKUList inAppIABSKUs = new IABSKUList();
        IABSKUList subsIABSKUs = new IABSKUList();
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

        availableProductIdentifiers.put(IABSKUListKey.getInApp(), inAppIABSKUs);
        availableProductIdentifiers.put(IABSKUListKey.getSubs(), subsIABSKUs);
    }

    @Override public void fetchProductIdentifiers(int requestCode)
    {
        super.fetchProductIdentifiers(requestCode);
        notifyListenerFetched(Collections.unmodifiableMap(availableProductIdentifiers));
    }
}
