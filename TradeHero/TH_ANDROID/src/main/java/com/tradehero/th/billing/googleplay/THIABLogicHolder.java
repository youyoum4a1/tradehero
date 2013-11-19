package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.BaseIABActor;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.BasePurchaseReporter;
import com.tradehero.th.billing.PurchaseReportedHandler;
import com.tradehero.th.billing.PurchaseReporter;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class THIABLogicHolder
    extends BaseIABActor<
        IABSKU,
        THSKUDetails,
        THIABPurchaseOrder,
        THIABOrderId,
        SKUPurchase,
        SKUDetailsPurchaser,
        THIABPurchaseHandler,
        THIABPurchaseConsumer,
        THIABPurchaseConsumeHandler>
    implements THIABActor
{
    public static final String TAG = THIABLogicHolder.class.getSimpleName();

    protected Map<Integer /*requestCode*/, PurchaseReporter> purchaseReporters;
    protected Map<Integer /*requestCode*/, BasePurchaseReporter.OnPurchaseReportedListener> purchaseReportedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<PurchaseReportedHandler>> purchaseReportedHandlers;

    public THIABLogicHolder(Activity activity)
    {
        super(activity);
        purchaseReporters = new HashMap<>();
        purchaseReportedListeners = new HashMap<>();
        purchaseReportedHandlers = new HashMap<>();
    }

    @Override public void onDestroy()
    {
        for (PurchaseReporter purchaseReporter: purchaseReporters.values())
        {
            if (purchaseReporter != null)
            {
                purchaseReporter.setListener(null);
            }
        }
        purchaseReporters.clear();
        purchaseReportedListeners.clear();
        purchaseReportedHandlers.clear();
        super.onDestroy();
    }

    @Override protected boolean isUnusedRequestCode(int randomNumber)
    {
        return super.isUnusedRequestCode(randomNumber) &&
                !purchaseReporters.containsKey(randomNumber) &&
                !purchaseReportedListeners.containsKey(randomNumber) &&
                !purchaseReportedHandlers.containsKey(randomNumber);
    }

    protected void registerPurchaseReportedHandler(int requestCode, PurchaseReportedHandler purchaseReportedHandler)
    {
        purchaseReportedHandlers.put(requestCode, new WeakReference<>(purchaseReportedHandler));
    }

    protected void createAndRegisterPurchaseReportedListener(final int requestCode)
    {
        purchaseReportedListeners.put(requestCode, createPurchaseReportedListener(requestCode));
    }

    protected BasePurchaseReporter.OnPurchaseReportedListener createPurchaseReportedListener(final int requestCode)
    {
        return new BasePurchaseReporter.OnPurchaseReportedListener<THIABOrderId, IABSKU, SKUPurchase>()
        {
            private PurchaseReportedHandler getPurchaseReportedHandler()
            {
                WeakReference<PurchaseReportedHandler> weakHandler = purchaseReportedHandlers.get(requestCode);
                if (weakHandler != null)
                {
                    return weakHandler.get();
                }
                return null;
            }

            @Override public void onPurchaseReported(SKUPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                THToast.show("OnPurchaseReportedListener.onPurchaseReported Report went through ok");
                THLog.d(TAG, "OnPurchaseReportedListener.onPurchaseReported Purchase info " + reportedPurchase);
                PurchaseReportedHandler handler = getPurchaseReportedHandler();
                if (handler != null)
                {
                    THLog.d(TAG, "OnPurchaseReportedListener.onPurchaseReported passing on the purchase for requestCode " + requestCode);
                    handler.handlePurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
                }
                else
                {
                    THLog.d(TAG, "OnPurchaseReportedListener.onPurchaseReported No PurchaseReportedHandler for requestCode " + requestCode);
                }
                finish();
            }

            @Override public void onPurchaseReportFailed(SKUPurchase reportedPurchase, Throwable error)
            {
                THLog.e(TAG, "OnPurchaseReportedListener.onPurchaseReportFailed There was an exception during the report", error);
                PurchaseReportedHandler handler = getPurchaseReportedHandler();
                if (handler != null)
                {
                    THLog.d(TAG, "OnPurchaseReportedListener.onPurchaseReportFailed passing on the exception for requestCode " + requestCode);
                    handler.handlePurchaseReportFailed(requestCode, error);
                }
                else
                {
                    THLog.d(TAG, "OnPurchaseReportedListener.onPurchaseReportFailed No THIABPurchaseHandler for requestCode " + requestCode);
                }
                finish();
            }

            private void finish()
            {
                forgetRequestCode(requestCode);
            }
        };
    }

    @Override public int launchReportSequence(PurchaseReportedHandler purchaseReportedHandler, SKUPurchase purchase)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseReportedHandler(requestCode, purchaseReportedHandler);
        createAndRegisterPurchaseReportedListener(requestCode);

        PurchaseReporter purchaseReporter = new PurchaseReporter();
        purchaseReporters.put(requestCode, purchaseReporter);
        purchaseReporter.reportPurchase(purchase);
        return requestCode;
    }

    @Override public UserProfileDTO launchReportSequenceSync(SKUPurchase purchase)
    {
        return new PurchaseReporter().reportPurchaseSync(purchase);
    }

    @Override protected SKUDetailsPurchaser createPurchaser(final int requestCode)
    {
        SKUDetailsPurchaser purchaser = new SKUDetailsPurchaser(getActivity());
        purchaser.setPurchaseFinishedListener(purchaseFinishedListeners.get(requestCode));
        return purchaser;
    }

    @Override protected THIABPurchaseConsumer createPurchaseConsumer(int requestCode)
    {
        THIABPurchaseConsumer consumer = new THIABPurchaseConsumer(getActivity());
        consumer.setConsumptionFinishedListener(purchaseConsumptionFinishedListeners.get(requestCode));
        return consumer;
    }
}
