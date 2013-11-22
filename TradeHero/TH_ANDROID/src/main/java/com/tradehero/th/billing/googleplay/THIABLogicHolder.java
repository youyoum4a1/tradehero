package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.BaseIABActor;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.BasePurchaseReporter;
import com.tradehero.th.billing.PurchaseReportedHandler;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.persistence.billing.googleplay.THSKUDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import retrofit.RetrofitError;

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
    protected Map<Integer /*requestCode*/, BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase>> purchaseReportedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<PurchaseReportedHandler>> purchaseReportedHandlers;

    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<THSKUDetailCache> thskuDetailCache;

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
        return new BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase>()
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
                    handler.handlePurchaseReportFailed(requestCode, reportedPurchase, error);
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

    @Override public int registerPurchaseReportedHandler(PurchaseReportedHandler purchaseReportedHandler)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseReportedHandler(requestCode, purchaseReportedHandler);
        return requestCode;
    }

    @Override public void launchReportSequence(int requestCode, SKUPurchase purchase)
    {
        createAndRegisterPurchaseReportedListener(requestCode);
        PurchaseReporter purchaseReporter = new PurchaseReporter();
        purchaseReporter.setListener(purchaseReportedListeners.get(requestCode));
        purchaseReporters.put(requestCode, purchaseReporter);
        purchaseReporter.reportPurchase(purchase);
    }

    @Override public int launchReportSequenceAsync(SKUPurchase purchase)
    {
        // TODO review this one as it looks HACKy
        THLog.d(TAG, "launchReportSequenceAsync " + purchase);
        int requestCode = registerPurchaseReportedHandler(createReportListener());
        launchReportSequence(requestCode, purchase);
        return requestCode;
    }

    @Override public UserProfileDTO launchReportSequenceSync(SKUPurchase purchase) throws RetrofitError
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

    public void launchReportSequenceAsync(Map<IABSKU, SKUPurchase> purchases)
    {
        for (SKUPurchase purchase : purchases.values())
        {
            THLog.d(TAG, "Purchasing " + purchase);
            launchReportSequenceAsync(purchase);
        }
    }

    protected PurchaseReportedHandler createReportListener()
    {
        return new PurchaseReportedHandler()
        {
            @Override public void handlePurchaseReported(int requestCode, SKUPurchase purchase, UserProfileDTO userProfileDTO)
            {
                THIABLogicHolder.this.handlePurchaseReported(requestCode, purchase, userProfileDTO);
            }

            @Override public void handlePurchaseReportFailed(int requestCode, SKUPurchase purchase, Throwable exception)
            {
                THIABLogicHolder.this.handlePurchaseReportFailed(requestCode, purchase, exception);
            }
        };
    }

    protected void handlePurchaseReportFailed(int requestCode, final SKUPurchase purchase, Throwable exception)
    {
        THLog.e(TAG, "A purchase could not be reported", exception);

        if (exception instanceof RetrofitError)
        {
            // TODO finer identification of "already has reported purchase"
            if (((RetrofitError) exception).getResponse().getStatus() >= 400)
            {
                // Consume quietly anyway
                consumeOne(requestCode, purchase, true);

                // Offer to send an email to support
                IABAlertUtils.popSendEmailSupportReportFailed(getActivity(), new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        sendSupportEmailReportFailed(purchase);
                    }
                });
            }
        }
        else
        {
            IABAlertUtils.popUnknownError(getActivity());
        }
    }

    protected void sendSupportEmailReportFailed(SKUPurchase purchase)
    {
        getActivity().startActivity(Intent.createChooser(
                GooglePlayUtils.getSupportPurchaseReportEmailIntent(getActivity(), purchase),
                getActivity().getString(R.string.google_play_send_support_email_chooser_title)));
    }

    protected void handlePurchaseReported(int requestCode, SKUPurchase purchase, UserProfileDTO userProfileDTO)
    {
        THLog.d(TAG, "handlePurchaseReported " + purchase);
        if (userProfileDTO != null)
        {
            userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
        }
        consumeOne(requestCode, purchase);
    }

    protected void consumeOne(int requestCode, SKUPurchase purchase)
    {
        consumeOne(requestCode, purchase, false);
    }

    protected void consumeOne(int requestCode, SKUPurchase purchase, final boolean reportSuccessQuiet)
    {
        final THIABPurchaseConsumeHandler consumeListener = new THIABPurchaseConsumeHandler()
        {
            @Override public void handlePurchaseConsumeException(int requestCode, IABException exception)
            {
                IABAlertUtils.popOfferSendEmailSupportConsumeFailed(getActivity(), exception);
            }

            @Override public void handlePurchaseConsumed(int requestCode, SKUPurchase purchase)
            {
                if (purchase != null)
                {
                    OwnedPortfolioId applicablePortfolioId = purchase.getApplicableOwnedPortfolioId();
                    if (applicablePortfolioId != null)
                    {
                        portfolioCompactListCache.get().invalidate(applicablePortfolioId.getUserBaseKey());
                        portfolioCache.get().invalidate(applicablePortfolioId);
                    }
                }

                if (reportSuccessQuiet)
                {
                    // Nothing to do
                }
                else if (purchase == null || purchase.getProductIdentifier() == null || purchase.getProductIdentifier().identifier == null)
                {
                    IABAlertUtils.popConsumePurchaseSuccess(getActivity(), null);
                }
                else
                {
                    IABAlertUtils.popConsumePurchaseSuccess(getActivity(), thskuDetailCache.get().get(purchase.getProductIdentifier()).description);
                }
            }
        };
        registerPurchaseConsumeHandler(requestCode, consumeListener);
        launchConsumeSequence(requestCode, purchase);
    }
}
