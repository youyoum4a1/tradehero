package com.tradehero.th.billing.googleplay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABItemAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.common.billing.googleplay.exception.IABResultErrorException;
import com.tradehero.common.billing.googleplay.exception.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exception.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exception.IABVerificationFailedException;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.THBaseBillingInteractor;
import com.tradehero.th.billing.googleplay.exception.MissingApplicablePortfolioIdException;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.billing.googleplay.request.THUIIABBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.googleplay.THIABStoreProductDetailView;
import com.tradehero.th.fragments.billing.googleplay.THSKUDetailAdapter;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.social.HeroListCache;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class THIABBillingInteractor
    extends
        THBaseBillingInteractor<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABLogicHolder,
                THIABStoreProductDetailView,
                THSKUDetailAdapter,
                THIABBillingRequestFull,
                THUIIABBillingRequest,
                IABException>
    implements THIABInteractor
{
    public static final String BUNDLE_KEY_ACTION = THIABBillingInteractor.class.getName() + ".action";

    @Inject THIABProductDetailCache thiabProductDetailCache;
    @Inject THIABLogicHolder billingActor;
    @Inject THIABAlertDialogUtil THIABAlertDialogUtil;
    @Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject UserProfileDTOUtil userProfileDTOUtil;

    @Inject protected HeroListCache heroListCache;
    @Inject protected UserService userService;

    //<editor-fold desc="Constructors">
    @Inject public THIABBillingInteractor()
    {
        super();
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    public void onDestroy()
    {
        billingActor = null;
        super.onDestroy();
    }

    @Override protected void cleanRequest(THUIIABBillingRequest uiBillingRequest)
    {
        super.cleanRequest(uiBillingRequest);
        if (uiBillingRequest != null)
        {
            uiBillingRequest.consumptionFinishedListener = null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override protected THIABBillingRequestFull createEmptyBillingRequest(THUIIABBillingRequest uiBillingRequest)
    {
        return new THIABBillingRequestFull();
    }

    @Override protected void populateBillingRequest(THIABBillingRequestFull request, THUIIABBillingRequest uiBillingRequest)
    {
        super.populateBillingRequest(request, uiBillingRequest);
        // TODO add specific things for IAB
    }
    //</editor-fold>

    //<editor-fold desc="Logic Holder Handling">
    @Override public THIABLogicHolder getBillingLogicHolder()
    {
        return billingActor;
    }
    //</editor-fold>

    @Override protected BillingAlertDialogUtil<IABSKU, THIABProductDetail, THIABLogicHolder, THIABStoreProductDetailView, THSKUDetailAdapter> getBillingAlertDialogUtil()
    {
        return THIABAlertDialogUtil;
    }

    public AlertDialog popErrorWhenLoading()
    {
        AlertDialog alertDialog = null;
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentContext);
            alertDialogBuilder
                    .setTitle(R.string.store_billing_error_loading_window_title)
                    .setMessage(R.string.store_billing_error_loading_window_description)
                    .setCancelable(true)
                    //.setPositiveButton(R.string.store_billing_error_loading_act, new DialogInterface.OnClickListener()
                    //{
                    //    @Override public void onClick(DialogInterface dialogInterface, int i)
                    //    {
                    //        int requestCode = getBillingLogicHolder().getUnusedRequestCode();
                    //        getBillingLogicHolder().launchInventoryFetchSequence(requestCode, new ArrayList<IABSKU>());
                    //    }
                    //})
                    ;
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return alertDialog;
    }

    protected BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> createForgetFetchedListener()
    {
        return new BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<IABSKU> productIdentifiers, Map<IABSKU, THIABProductDetail> inventory)
            {
                getBillingLogicHolder().forgetRequestCode(requestCode);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<IABSKU> productIdentifiers, IABException exception)
            {
                getBillingLogicHolder().forgetRequestCode(requestCode);
            }
        };
    }

    protected void showProgressFollow()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = ProgressDialog.show(
                    currentContext,
                    currentContext.getString(R.string.manage_heroes_follow_progress_title),
                    currentContext.getResources().getString(R.string.manage_heroes_follow_progress_message),
                    true,
                    true
            );
            progressDialog.setCanceledOnTouchOutside(true);
        }
    }

    protected void showProgressUnfollow()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = ProgressDialog.show(
                    currentContext,
                    currentContext.getString(R.string.manage_heroes_unfollow_progress_title),
                    currentContext.getString(R.string.manage_heroes_unfollow_progress_message),
                    true,
                    true
            );
        }
    }

    //<editor-fold desc="Inventory Fetch">
    @Override protected AlertDialog popInventoryFetchFail(int requestCode,
            List<IABSKU> productIdentifiers, IABException exception)
    {
        AlertDialog dialog = super.popInventoryFetchFail(requestCode, productIdentifiers, exception);
        if (dialog == null)
        {
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                if (exception instanceof IABUserCancelledException)
                {
                    dialog = THIABAlertDialogUtil.popUserCancelled(currentContext);
                }
                else if (exception instanceof IABBadResponseException)
                {
                    dialog = THIABAlertDialogUtil.popBadResponse(currentContext);
                }
                else if (exception instanceof IABResultErrorException)
                {
                    dialog = THIABAlertDialogUtil.popResultError(currentContext);
                }
                else if (!(exception instanceof IABBillingUnavailableException)) // No need to tell again
                {
                    dialog = THIABAlertDialogUtil.popUnknownError(currentContext, exception);
                }
            }
        }
        return dialog;    }
    //</editor-fold>

    //<editor-fold desc="Purchase Actions">
    @Override protected void launchPurchaseSequence(int requestCode, IABSKU productIdentifier)
    {
        THIABLogicHolder logicHolder = getBillingLogicHolder();
        if (logicHolder != null)
        {
            logicHolder.run(requestCode, createPurchaseBillingRequest(requestCode, productIdentifier));
        }
        else
        {
            Timber.e(new NullPointerException("logicHolder just became null"), "");
        }
    }

    @Override protected THIABBillingRequestFull createEmptyBillingRequest()
    {
        return new THIABBillingRequestFull();
    }

    @Override protected void populatePurchaseBillingRequest(int requestCode, THIABBillingRequestFull request, IABSKU productIdentifier)
    {
        super.populatePurchaseBillingRequest(requestCode, request, productIdentifier);
        THUIBillingRequest uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            request.testBillingAvailable = true;
            request.consumePurchase = true;
            request.consumptionFinishedListener = createConsumptionFinishedListener();
        }
    }

    @Override protected THIABPurchaseOrder createEmptyPurchaseOrder(int requestCode, IABSKU productIdentifier) throws MissingApplicablePortfolioIdException
    {
        THUIBillingRequest uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            return new THIABPurchaseOrder(productIdentifier, uiBillingRequest.applicablePortfolioId);
        }
        return null;
    }

    @Override protected AlertDialog popPurchaseFailed(
            int requestCode,
            THIABPurchaseOrder purchaseOrder,
            IABException exception,
            AlertDialog.OnClickListener restoreClickListener)
    {
        AlertDialog dialog = super.popPurchaseFailed(requestCode, purchaseOrder, exception, restoreClickListener);
        if (dialog == null)
        {
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                if (exception instanceof IABVerificationFailedException)
                {
                    dialog = THIABAlertDialogUtil.popVerificationFailed(currentContext);
                }
                else if (exception instanceof IABUserCancelledException)
                {
                    dialog = THIABAlertDialogUtil.popUserCancelled(currentContext);
                }
                else if (exception instanceof IABBadResponseException)
                {
                    dialog = THIABAlertDialogUtil.popBadResponse(currentContext);
                }
                else if (exception instanceof IABResultErrorException)
                {
                    dialog = THIABAlertDialogUtil.popResultError(currentContext);
                }
                else if (exception instanceof IABRemoteException)
                {
                    dialog = THIABAlertDialogUtil.popRemoteError(currentContext);
                }
                else if (exception instanceof IABItemAlreadyOwnedException)
                {
                    dialog = THIABAlertDialogUtil.popSKUAlreadyOwned(currentContext,
                            thiabProductDetailCache.get(purchaseOrder.getProductIdentifier()),
                            restoreClickListener);
                }
                else if (exception instanceof IABSendIntentException)
                {
                    dialog = THIABAlertDialogUtil.popSendIntent(currentContext);
                }
                else
                {
                    dialog = THIABAlertDialogUtil.popUnknownError(currentContext, exception);
                }
            }
        }
        return dialog;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Restore">
    @Override
    protected void notifyPurchaseRestored(int requestCode, List<THIABPurchase> restoredPurchases, List<THIABPurchase> failedRestorePurchases,
            List<IABException> failExceptions)
    {
        super.notifyPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
        THUIIABBillingRequest billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            if (billingRequest.popRestorePurchaseOutcome)
            {
                Context currentContext = currentActivityHolder.getCurrentContext();
                Exception exception;
                if (failExceptions != null && failExceptions.size() > 0)
                {
                    exception = failExceptions.get(0);
                }
                else
                {
                    exception = new Exception();
                }
                if (currentContext != null)
                {
                    IABPurchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                            currentContext,
                            restoredPurchases,
                            failedRestorePurchases,
                            IABPurchaseRestorerAlertUtil.createFailedRestoreClickListener(currentContext, exception),
                            billingRequest.popRestorePurchaseOutcomeVerbose);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Consumption">
    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> createConsumptionFinishedListener()
    {
        return  new THIABUserInteractorIABConsumptionFinishedListener();
    }

    protected class THIABUserInteractorIABConsumptionFinishedListener implements IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>
    {
        private void forgetListener(int requestCode)
        {
            THIABLogicHolder logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterPurchaseConsumptionListener(requestCode);
            }
        }

        @Override public void onPurchaseConsumed(int requestCode, THIABPurchase purchase)
        {
            forgetListener(requestCode);
            handlePurchaseConsumed(purchase);
            notifyPurchaseConsumed(requestCode, purchase);
        }

        @Override public void onPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
        {
            forgetListener(requestCode);
            handlePurchaseConsumeFailed(requestCode, purchase, exception);
            notifyPurchaseConsumeFailed(requestCode, purchase, exception);
        }
    }

    protected void handlePurchaseConsumed(THIABPurchase purchase)
    {
        ProgressDialog dialog = progressDialog;
        if (dialog != null)
        {
            dialog.setTitle(R.string.store_billing_report_api_finishing_window_title);
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                dialog.setMessage(currentContext.getString(R.string.store_billing_report_api_finishing_window_title));
            }
        }

        Handler handler = currentActivityHolder.getCurrentHandler();
        if (handler != null)
        {
            handler.postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    ProgressDialog dialog = progressDialog;
                    if (dialog != null)
                    {
                        dialog.hide();
                    }
                }
            }, 1500);
        }
        else
        {
            Timber.w("Handler is null");
        }
    }

    protected void notifyPurchaseConsumed(int requestCode, THIABPurchase purchase)
    {
        THUIBillingRequest thuiBillingRequest = uiBillingRequests.get(requestCode);
        if (thuiBillingRequest != null && thuiBillingRequest instanceof THUIIABBillingRequest)
        {
            if (((THUIIABBillingRequest) thuiBillingRequest).consumptionFinishedListener != null)
            {
                ((THUIIABBillingRequest) thuiBillingRequest).consumptionFinishedListener.onPurchaseConsumed(requestCode, purchase);
            }
        }
    }

    protected void handlePurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        THUIIABBillingRequest billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.startWithProgressDialog && progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    protected void notifyPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        THUIBillingRequest billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest instanceof THUIIABBillingRequest)
        {
            if (((THUIIABBillingRequest) billingRequest).consumptionFinishedListener != null)
            {
                ((THUIIABBillingRequest) billingRequest).consumptionFinishedListener.onPurchaseConsumeFailed(requestCode, purchase, exception);
            }
            else if (billingRequest.onDefaultErrorListener != null)
            {
                ((THUIIABBillingRequest) billingRequest).onDefaultErrorListener.onError(requestCode, exception);
            }
        }

        if (billingRequest == null ||
                        (billingRequest instanceof THUIIABBillingRequest &&
                                ((THUIIABBillingRequest) billingRequest).popIfConsumeFailed))
        {
            popPurchaseConsumeFailed(requestCode, purchase, exception);
        }
    }

    protected AlertDialog popPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            return THIABAlertDialogUtil.popOfferSendEmailSupportConsumeFailed(currentContext, exception);
        }
        return null;
    }
    //</editor-fold>
}
