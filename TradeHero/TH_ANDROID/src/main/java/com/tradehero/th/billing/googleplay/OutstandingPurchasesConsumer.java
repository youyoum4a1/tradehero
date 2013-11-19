package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 5:27 PM To change this template use File | Settings | File Templates. */
public class OutstandingPurchasesConsumer
{
    public static final String TAG = OutstandingPurchasesConsumer.class.getSimpleName();

    private Map<IABSKU, THSKUDetails> inventory;
    private List<SKUPurchase> outstandingPurchases;

    private SKUPurchase processingPurchase;
    private List<SKUPurchase> reportedPurchases;
    private List<SKUPurchase> consumedPurchases;
    protected WeakReference<THIABActor> billingActor = new WeakReference<>(null);
    private WeakReference<OnOutstandingPurchasesConsumedListener> consumedListener = new WeakReference<>(null);

    public OutstandingPurchasesConsumer(THIABActor billingActor)
    {
        setBillingActor(billingActor);
        reportedPurchases = new ArrayList<>();
        consumedPurchases = new ArrayList<>();
    }

    protected THIABActor getBillingActor()
    {
        return billingActor.get();
    }

    /**
     * The billingActor should be referenced elsewhere
     * @param billingActor
     */
    protected void setBillingActor(THIABActor billingActor)
    {
        this.billingActor = new WeakReference<>(billingActor);
    }

    public void setInventory(Map<IABSKU, THSKUDetails> inventory)
    {
        if (inventory == null)
        {
            this.inventory = new HashMap<>();
        }
        else
        {
            this.inventory = inventory;
        }
        consumeOutstanding();
    }

    public void setOutstandingPurchases(List<SKUPurchase> outstandingPurchases)
    {
        if (outstandingPurchases == null)
        {
            this.outstandingPurchases = new ArrayList<>();
        }
        else
        {
            this.outstandingPurchases = outstandingPurchases;
        }
        consumeOutstanding();
    }

    public void consumeOutstanding()
    {
        if (inventory == null || outstandingPurchases == null || outstandingPurchases.size() == 0)
        {
            return;
        }

        // Do we have all info necessary?
        for (SKUPurchase purchase: new ArrayList<>(outstandingPurchases))
        {
            if (!inventory.containsKey(purchase.getProductIdentifier()))
            {
                return;
            }
        }

        consumeSync();  // TODO asynctask
    }

    private void consumeSync()
    {
        for (SKUPurchase purchase: new ArrayList<>(outstandingPurchases))
        {
            //getBillingActor().launchReportSequenceSync(purchase, inventory.get(purchase.getProductIdentifier()))
        }
    }

    public OnOutstandingPurchasesConsumedListener getConsumedListener()
    {
        return consumedListener.get();
    }

    public void setConsumedListener(OnOutstandingPurchasesConsumedListener consumedListener)
    {
        this.consumedListener = new WeakReference<>(consumedListener);
    }

    public static interface OnOutstandingPurchasesConsumedListener
    {
        void onOutstandingPurchasesConsumed(List<SKUPurchase> purchasesConsumed, List<SKUPurchase> purchasesNotConsumed);
        void onOutstandingPurchasesConsumeFailed(Exception error);
    }
}
