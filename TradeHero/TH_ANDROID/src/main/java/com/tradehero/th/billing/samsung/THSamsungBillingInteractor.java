package com.tradehero.th.billing.samsung;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.exception.SamsungPaymentCancelledException;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.THBaseBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.billing.samsung.request.THSamsungRequestFull;
import com.tradehero.th.billing.samsung.request.THUISamsungRequest;
import com.tradehero.th.fragments.billing.samsung.THSamsungSKUDetailAdapter;
import com.tradehero.th.fragments.billing.samsung.THSamsungStoreProductDetailView;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCache;
import com.tradehero.th.persistence.social.HeroListCache;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * It expects its Activity to implement THSamsungInteractor.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
public class THSamsungBillingInteractor
    extends
        THBaseBillingInteractor<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungLogicHolder,
                THSamsungStoreProductDetailView,
                THSamsungSKUDetailAdapter,
                THSamsungRequestFull,
                THUISamsungRequest,
                SamsungException>
    implements THSamsungInteractor
{
    public static final String BUNDLE_KEY_ACTION = THSamsungBillingInteractor.class.getName() + ".action";

    THSamsungProductDetailCache thiabProductDetailCache;
    THSamsungLogicHolder billingActor;
    THSamsungAlertDialogUtil thSamsungAlertDialogUtil;
    UserProfileDTOUtil userProfileDTOUtil;
    LocalyticsSession localyticsSession;
    HeroListCache heroListCache;
    UserService userService;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungBillingInteractor(
            THSamsungProductDetailCache thiabProductDetailCache,
            THSamsungLogicHolder billingActor,
            THSamsungAlertDialogUtil thSamsungAlertDialogUtil,
            UserProfileDTOUtil userProfileDTOUtil,
            LocalyticsSession localyticsSession,
            HeroListCache heroListCache,
            UserService userService)
    {
        super();
        this.thiabProductDetailCache = thiabProductDetailCache;
        this.billingActor = billingActor;
        this.thSamsungAlertDialogUtil = thSamsungAlertDialogUtil;
        this.userProfileDTOUtil = userProfileDTOUtil;
        this.localyticsSession = localyticsSession;
        this.heroListCache = heroListCache;
        this.userService = userService;
    }
    //</editor-fold>

    @Override public String getName()
    {
        return SamsungConstants.NAME;
    }

    //<editor-fold desc="Life Cycle">
    public void onDestroy()
    {
        billingActor = null;
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override protected THSamsungRequestFull createEmptyBillingRequest(THUISamsungRequest uiBillingRequest)
    {
        return new THSamsungRequestFull();
    }

    @Override protected void populateBillingRequest(THSamsungRequestFull request, THUISamsungRequest uiBillingRequest)
    {
        super.populateBillingRequest(request, uiBillingRequest);

        if (uiBillingRequest.domainToPresent != null)
        {
            request.testBillingAvailable = true;
            request.fetchInventory = true;
        }
        else if (uiBillingRequest.restorePurchase)
        {
            request.testBillingAvailable = true;
            request.fetchInventory = true;
            request.fetchPurchase = true;
            request.restorePurchase = true;
        }
        else if (uiBillingRequest.fetchInventory)
        {
            request.testBillingAvailable = true;
            request.fetchInventory = true;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Logic Holder Handling">
    @Override public THSamsungLogicHolder getBillingLogicHolder()
    {
        return billingActor;
    }
    //</editor-fold>

    @Override protected BillingAlertDialogUtil<SamsungSKU, THSamsungProductDetail, THSamsungLogicHolder, THSamsungStoreProductDetailView, THSamsungSKUDetailAdapter> getBillingAlertDialogUtil()
    {
        return thSamsungAlertDialogUtil;
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
                    //        getBillingLogicHolder().launchInventoryFetchSequence(requestCode, new ArrayList<SamsungSKU>());
                    //    }
                    //})
                    ;
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return alertDialog;
    }

    protected BillingInventoryFetcher.OnInventoryFetchedListener<SamsungSKU, THSamsungProductDetail, SamsungException> createForgetFetchedListener()
    {
        return new BillingInventoryFetcher.OnInventoryFetchedListener<SamsungSKU, THSamsungProductDetail, SamsungException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<SamsungSKU> productIdentifiers, Map<SamsungSKU, THSamsungProductDetail> inventory)
            {
                getBillingLogicHolder().forgetRequestCode(requestCode);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<SamsungSKU> productIdentifiers, SamsungException exception)
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
            List<SamsungSKU> productIdentifiers, SamsungException exception)
    {
        AlertDialog dialog = super.popInventoryFetchFail(requestCode, productIdentifiers, exception);
        if (dialog == null)
        {
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                if (exception instanceof SamsungPaymentCancelledException)
                {
                    dialog = thSamsungAlertDialogUtil.popUserCancelled(currentContext);
                }
                else
                {
                    dialog = thSamsungAlertDialogUtil.popUnknownError(currentContext, exception);
                }
            }
        }
        return dialog;    }
    //</editor-fold>

    //<editor-fold desc="Purchase Actions">
    @Override protected void launchPurchaseSequence(int requestCode, SamsungSKU productIdentifier)
    {
        THSamsungLogicHolder logicHolder = getBillingLogicHolder();
        if (logicHolder != null)
        {
            logicHolder.run(requestCode, createPurchaseBillingRequest(requestCode, productIdentifier));
        }
        else
        {
            Timber.e(new NullPointerException("logicHolder just became null"), "");
        }
    }

    @Override protected THSamsungRequestFull createEmptyBillingRequest()
    {
        return new THSamsungRequestFull();
    }

    @Override protected void populatePurchaseBillingRequest(int requestCode, THSamsungRequestFull request, SamsungSKU productIdentifier)
    {
        super.populatePurchaseBillingRequest(requestCode, request, productIdentifier);
        THUIBillingRequest uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            // Nothing to do I suppose
        }
    }

    @Override protected THSamsungPurchaseOrder createEmptyPurchaseOrder(int requestCode, SamsungSKU productIdentifier)
    {
        THUIBillingRequest uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            return new THSamsungPurchaseOrder(productIdentifier);
        }
        return null;
    }

    @Override protected AlertDialog popPurchaseFailed(
            int requestCode,
            THSamsungPurchaseOrder purchaseOrder,
            SamsungException exception,
            AlertDialog.OnClickListener restoreClickListener)
    {
        AlertDialog dialog = super.popPurchaseFailed(requestCode, purchaseOrder, exception, restoreClickListener);
        if (dialog == null)
        {
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                // TODO finer dialog
                dialog = thSamsungAlertDialogUtil.popUnknownError(currentContext, exception);
            }
        }
        return dialog;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Restore">
    @Override
    protected void notifyPurchaseRestored(int requestCode, List<THSamsungPurchase> restoredPurchases, List<THSamsungPurchase> failedRestorePurchases,
            List<SamsungException> failExceptions)
    {
        super.notifyPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
        THUISamsungRequest billingRequest = uiBillingRequests.get(requestCode);
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
                    thSamsungAlertDialogUtil.handlePurchaseRestoreFinished(
                            currentContext,
                            restoredPurchases,
                            failedRestorePurchases,
                            thSamsungAlertDialogUtil.createFailedRestoreClickListener(
                                    currentContext, exception),
                            billingRequest.popRestorePurchaseOutcomeVerbose);
                }
            }
        }
    }
    //</editor-fold>
}
