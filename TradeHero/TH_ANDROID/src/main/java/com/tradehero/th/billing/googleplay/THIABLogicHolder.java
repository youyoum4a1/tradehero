package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.BaseIABActor;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.BasePurchaseReporter;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.THSKUDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class THIABLogicHolder
    extends BaseIABActor<
        IABSKU,
        THSKUDetails,
        THIABInventoryFetcher,
        InventoryFetcher.OnInventoryFetchedListener<IABSKU, THSKUDetails, IABException>,
        THIABPurchaseOrder,
        THIABOrderId,
        SKUPurchase,
        PurchaseFetcher,
        IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, SKUPurchase>,
        SKUDetailsPurchaser,
        BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, SKUPurchase, IABException>,
        THIABPurchaseConsumer,
        THIABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, SKUPurchase, IABException>>
    implements THIABActor
{
    public static final String TAG = THIABLogicHolder.class.getSimpleName();

    protected Map<Integer /*requestCode*/, SKUFetcher> skuFetchers;
    protected Map<Integer /*requestCode*/, IABSKUFetcher.OnSKUFetchedListener<IABSKU>> skuFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<IABSKUFetcher.OnSKUFetchedListener<IABSKU>>> parentSkuFetchedListeners;

    protected Map<Integer /*requestCode*/, PurchaseReporter> purchaseReporters;
    protected Map<Integer /*requestCode*/, BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase>> purchaseReportedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase>>> parentPurchaseReportedHandlers;

    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<IABSKUListCache> iabskuListCache;
    @Inject protected Lazy<THSKUDetailCache> thskuDetailCache;

    public THIABLogicHolder(Activity activity)
    {
        super(activity);

        skuFetchers = new HashMap<>();
        skuFetchedListeners = new HashMap<>();
        parentSkuFetchedListeners = new HashMap<>();

        purchaseReporters = new HashMap<>();
        purchaseReportedListeners = new HashMap<>();
        parentPurchaseReportedHandlers = new HashMap<>();
    }

    @Override public void onDestroy()
    {
        for (SKUFetcher skuFetcher : skuFetchers.values())
        {
            if (skuFetcher != null)
            {
                skuFetcher.setListener(null);
            }
        }
        skuFetchers.clear();
        skuFetchedListeners.clear();
        parentSkuFetchedListeners.clear();

        for (PurchaseReporter purchaseReporter: purchaseReporters.values())
        {
            if (purchaseReporter != null)
            {
                purchaseReporter.setListener(null);
            }
        }
        purchaseReporters.clear();
        purchaseReportedListeners.clear();
        parentPurchaseReportedHandlers.clear();
        super.onDestroy();
    }

    @Override protected boolean isUnusedRequestCode(int randomNumber)
    {
        return super.isUnusedRequestCode(randomNumber) &&
                !skuFetchers.containsKey(randomNumber) &&
                !skuFetchedListeners.containsKey(randomNumber) &&
                !parentSkuFetchedListeners.containsKey(randomNumber) &&
                !purchaseReporters.containsKey(randomNumber) &&
                !purchaseReportedListeners.containsKey(randomNumber) &&
                !parentPurchaseReportedHandlers.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);

        skuFetchers.remove(requestCode);
        skuFetchedListeners.remove(requestCode);
        parentSkuFetchedListeners.remove(requestCode);

        purchaseReporters.remove(requestCode);
        purchaseReportedListeners.remove(requestCode);
        parentPurchaseReportedHandlers.remove(requestCode);
    }

    protected void registerSkuFetchedListener(int requestCode, IABSKUFetcher.OnSKUFetchedListener<IABSKU> skuFetchedListener)
    {
        parentSkuFetchedListeners.put(requestCode, new WeakReference<>(skuFetchedListener));
    }

    @Override public int registerSkuFetchedListener(IABSKUFetcher.OnSKUFetchedListener<IABSKU> skuFetchedListener)
    {
        int requestCode = getUnusedRequestCode();
        registerSkuFetchedListener(requestCode, skuFetchedListener);
        return requestCode;
    }

    @Override public void launchSkuFetchSequence(int requestCode)
    {
        IABSKUFetcher.OnSKUFetchedListener<IABSKU> skuFetchedListener = new IABSKUFetcher.OnSKUFetchedListener<IABSKU>()
        {
            @Override public void onFetchedSKUs(int requestCode, Map<String, List<IABSKU>> availableSkus)
            {
                notifySkuFetchedSuccess(requestCode, availableSkus);
            }

            @Override public void onFetchSKUsFailed(int requestCode, Exception exception)
            {
                notifySkuFetchedFailed(requestCode, exception);
            }
        };
        skuFetchedListeners.put(requestCode, skuFetchedListener);
        SKUFetcher skuFetcher = new SKUFetcher();
        skuFetcher.setListener(skuFetchedListener);
        skuFetchers.put(requestCode, skuFetcher);
        skuFetcher.fetchSkus(requestCode);
    }

    @Override public IABSKUFetcher.OnSKUFetchedListener<IABSKU> getSkuFetchedListener(int requestCode)
    {
        WeakReference<IABSKUFetcher.OnSKUFetchedListener<IABSKU>> weakListener = parentSkuFetchedListeners.get(requestCode);
        if (weakListener == null)
        {
            return null;
        }
        return weakListener.get();
    }

    protected void notifySkuFetchedSuccess(int requestCode, Map<String, List<IABSKU>> availableSkus)
    {
        IABSKUFetcher.OnSKUFetchedListener<IABSKU> fetchedListener = getSkuFetchedListener(requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchedSKUs(requestCode, availableSkus);
        }
    }

    protected void notifySkuFetchedFailed(int requestCode, Exception exception)
    {
        IABSKUFetcher.OnSKUFetchedListener<IABSKU> fetchedListener = getSkuFetchedListener(requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchSKUsFailed(requestCode, exception);
        }
    }

    public BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase> getPurchaseReportHandler(int requestCode)
    {
        WeakReference<BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase>> weakHandler = parentPurchaseReportedHandlers.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
    }

    protected void registerPurchaseReportedHandler(int requestCode, BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase> purchaseReportedHandler)
    {
        parentPurchaseReportedHandlers.put(requestCode, new WeakReference<>(purchaseReportedHandler));
    }

    @Override public int registerPurchaseReportedHandler(BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase> purchaseReportedHandler)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseReportedHandler(requestCode, purchaseReportedHandler);
        return requestCode;
    }

    protected void handlePurchaseReported(int requestCode, SKUPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        THLog.d(TAG, "handlePurchaseReported Purchase info " + reportedPurchase);
        BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase> handler = getPurchaseReportHandler(requestCode);
        if (handler != null)
        {
            THLog.d(TAG, "handlePurchaseReported passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReported No PurchaseReportedHandler for requestCode " + requestCode);
        }
    }

    protected void handlePurchaseReportFailed(int requestCode, SKUPurchase reportedPurchase, Throwable error)
    {
        THLog.e(TAG, "handlePurchaseReportFailed There was an exception during the report", error);
        BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase> handler = getPurchaseReportHandler(requestCode);
        if (handler != null)
        {
            THLog.d(TAG, "handlePurchaseReportFailed passing on the exception for requestCode " + requestCode);
            handler.onPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReportFailed No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

    @Override public void launchReportSequence(int requestCode, SKUPurchase purchase)
    {
        BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase> reportedListener = new BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, SKUPurchase>()
        {
            @Override public void onPurchaseReported(int requestCode, SKUPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                handlePurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }

            @Override public void onPurchaseReportFailed(int requestCode, SKUPurchase reportedPurchase, Throwable error)
            {
                handlePurchaseReportFailed(requestCode, reportedPurchase, error);
            }
        };
        purchaseReportedListeners.put(requestCode, reportedListener);
        PurchaseReporter purchaseReporter = new PurchaseReporter();
        purchaseReporter.setListener(reportedListener);
        purchaseReporters.put(requestCode, purchaseReporter);
        purchaseReporter.reportPurchase(requestCode, purchase);
    }

    @Override public UserProfileDTO launchReportSequenceSync(SKUPurchase purchase) throws RetrofitError
    {
        return new PurchaseReporter().reportPurchaseSync(purchase);
    }

    @Override protected BaseIABSKUList<IABSKU> getAllSkus()
    {
        BaseIABSKUList<IABSKU> mixed = iabskuListCache.get().get(IABSKUListType.getInApp());
        BaseIABSKUList<IABSKU> subs = iabskuListCache.get().get(IABSKUListType.getSubs());
        if (subs != null)
        {
            mixed.addAll(subs);
        }
        return mixed;
    }

    @Override protected THIABInventoryFetcher createInventoryFetcher()
    {
        return new THIABInventoryFetcher(getActivity());
    }

    @Override protected PurchaseFetcher createPurchaseFetcher()
    {
        return new PurchaseFetcher(getActivity());
    }

    @Override protected SKUDetailsPurchaser createPurchaser()
    {
        return new SKUDetailsPurchaser(getActivity());
    }

    @Override protected THIABPurchaseConsumer createPurchaseConsumer()
    {
        return new THIABPurchaseConsumer(getActivity());
    }

    public void launchReportSequenceAsync(Map<IABSKU, SKUPurchase> purchases)
    {
        for (SKUPurchase purchase : purchases.values())
        {
            THLog.d(TAG, "Purchasing " + purchase);
            // TODO
        }
    }

    protected void handlePurchaseReportFailed_Old(int requestCode, final SKUPurchase purchase, Throwable exception) // TODO place somewhere else
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

    protected void handlePurchaseReported_Old(int requestCode, SKUPurchase purchase, UserProfileDTO userProfileDTO) // TODO place somewhere else
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
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, SKUPurchase, IABException> consumeListener =  new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, SKUPurchase, IABException>()
        {
            @Override public void onPurchaseConsumed(int requestCode, SKUPurchase purchase)
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

            @Override public void onPurchaseConsumeFailed(int requestCode, SKUPurchase purchase, IABException exception)
            {
                IABAlertUtils.popOfferSendEmailSupportConsumeFailed(getActivity(), exception);
                //notifyPurchaseConsumeFail(requestCode, purchase, exception);
            }
        };
        registerConsumeFinishedListener(requestCode, consumeListener);
        launchConsumeSequence(requestCode, purchase);
    }
}
