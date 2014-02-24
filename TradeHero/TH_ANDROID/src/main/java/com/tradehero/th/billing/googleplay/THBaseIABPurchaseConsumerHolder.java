package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABPurchaseConsumerHolder
    extends BaseIABPurchaseConsumerHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaseConsumer,
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            IABException>>
    implements THIABPurchaseConsumerHolder
{
    protected Activity activity;

    public THBaseIABPurchaseConsumerHolder(Activity activity)
    {
        super();
        this.activity = activity;
    }

    @Override protected THIABPurchaseConsumer createPurchaseConsumer()
    {
        return new THIABPurchaseConsumer(activity);
    }
}
