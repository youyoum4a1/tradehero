package com.tradehero.th.billing.googleplay.identifier;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.identifier.BaseProductIdentifierFetcherRx;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.th.billing.googleplay.THIABConstants;
import javax.inject.Inject;

public class THBaseIABProductIdentifierFetcherRx
    extends BaseProductIdentifierFetcherRx<
            IABSKUListKey,
            IABSKU,
            IABSKUList>
    implements THIABProductIdentifierFetcherRx
{
    //<editor-fold desc="Constructors">
    public THBaseIABProductIdentifierFetcherRx(int requestCode)
    {
        super(requestCode);
        fetchSkus();
    }
    //</editor-fold>

    protected void fetchSkus()
    {
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

        subject.onNext(new ProductIdentifierListResult<>(getRequestCode(), IABSKUListKey.getInApp(), inAppIABSKUs));
        subject.onNext(new ProductIdentifierListResult<>(getRequestCode(), IABSKUListKey.getSubs(), subsIABSKUs));
        subject.onCompleted();
    }
}
