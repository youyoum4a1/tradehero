package com.tradehero.th.billing.samsung;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.exception.SamsungPaymentCancelledException;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.billing.samsung.request.THSamsungRequestFull;
import com.tradehero.th.billing.samsung.request.THUISamsungRequest;
import com.tradehero.th.fragments.billing.THSamsungSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THSamsungStoreProductDetailView;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;
import timber.log.Timber;

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

    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NonNull protected final HeroListCacheRx heroListCache;
    @NonNull protected final UserService userService;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungBillingInteractor(
            @NonNull Provider<Activity> activityProvider,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCache userProfileCache,
            @NonNull UserProfileCacheRx userProfileCacheRx,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THSamsungAlertDialogUtil thSamsungAlertDialogUtil,
            @NonNull THSamsungLogicHolder billingActor,
            @NonNull UserProfileDTOUtil userProfileDTOUtil,
            @NonNull HeroListCacheRx heroListCache,
            @NonNull UserService userService)
    {
        super(
                billingActor,
                activityProvider,
                currentUserId,
                userProfileCache,
                userProfileCacheRx,
                portfolioCompactListCache,
                progressDialogUtil,
                thSamsungAlertDialogUtil);
        this.userProfileDTOUtil = userProfileDTOUtil;
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
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override public int run(@NonNull THUISamsungRequest uiBillingRequest)
    {
        // Here we disable the initial restore
        if (uiBillingRequest.getRestorePurchase() && !uiBillingRequest.getStartWithProgressDialog())
        {
            // In effect skip it
            return getUnusedRequestCode();
        }
        else
        {
            return super.run(uiBillingRequest);
        }
    }

    @Override protected THSamsungRequestFull createBillingRequest(
            @NonNull THUISamsungRequest uiBillingRequest)
    {
        THSamsungRequestFull.Builder<?> builder = THSamsungRequestFull.builder();
        populateBillingRequestBuilder(builder, uiBillingRequest);
        return builder.build();
    }

    protected void populateBillingRequestBuilder(
            @NonNull THSamsungRequestFull.Builder<?> builder,
            @NonNull THUISamsungRequest uiBillingRequest)
    {
        super.populateBillingRequestBuilder(builder, uiBillingRequest);
        if (uiBillingRequest.getDomainToPresent() != null)
        {
            builder.testBillingAvailable(true)
                    .fetchInventory(true);
        }
        else if (uiBillingRequest.getRestorePurchase())
        {
            builder.testBillingAvailable(true)
                    .fetchInventory(true)
                    .fetchPurchases(true)
                    .restorePurchase(true);
        }
        else if (uiBillingRequest.getFetchInventory())
        {
            builder.testBillingAvailable(true)
                    .fetchInventory(true);
        }
    }
    //</editor-fold>

    public AlertDialog popErrorWhenLoading()
    {
        AlertDialog alertDialog = null;
        Context currentContext = activityProvider.get();
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
                billingLogicHolder.forgetRequestCode(requestCode);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<SamsungSKU> productIdentifiers, SamsungException exception)
            {
                billingLogicHolder.forgetRequestCode(requestCode);
            }
        };
    }

    protected void showProgressFollow()
    {
        Context currentContext = activityProvider.get();
        if (currentContext != null)
        {
            dismissProgressDialog();
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
        Context currentContext = activityProvider.get();
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
            Context currentContext = activityProvider.get();
            if (currentContext != null)
            {
                if (exception instanceof SamsungPaymentCancelledException)
                {
                    dialog = billingAlertDialogUtil.popUserCancelled(currentContext);
                }
                else
                {
                    dialog = billingAlertDialogUtil.popUnknownError(currentContext, exception);
                }
            }
        }
        return dialog;    }
    //</editor-fold>

    //<editor-fold desc="Purchase Actions">
    @Override protected void launchPurchaseSequence(int requestCode, SamsungSKU productIdentifier)
    {
        billingLogicHolder.run(requestCode, createPurchaseBillingRequest(requestCode, productIdentifier));
    }

    @Override protected THSamsungRequestFull createEmptyBillingRequest()
    {
        return THSamsungRequestFull.builder().build();
    }

    @Override protected void populatePurchaseBillingRequest(
            int requestCode,
            THSamsungRequestFull request,
            @NonNull SamsungSKU productIdentifier)
    {
        super.populatePurchaseBillingRequest(requestCode, request, productIdentifier);
        THUIBillingRequest uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            // Nothing to do I suppose
        }
    }

    @Override @NonNull protected THSamsungPurchaseOrder createEmptyPurchaseOrder(
            @NonNull THUISamsungRequest uiBillingRequest,
            @NonNull SamsungSKU productIdentifier)
    {
        return new THSamsungPurchaseOrder(productIdentifier, uiBillingRequest.getApplicablePortfolioId());
    }

    @Override protected AlertDialog popPurchaseFailed(
            int requestCode,
            THSamsungPurchaseOrder purchaseOrder,
            SamsungException exception,
            AlertDialog.OnClickListener restoreClickListener)
    {
        Timber.d("popPurchaseFailed");
        AlertDialog dialog = super.popPurchaseFailed(requestCode, purchaseOrder, exception, restoreClickListener);
        if (dialog == null)
        {
            Context currentContext = activityProvider.get();
            if (currentContext != null)
            {
                if (exception instanceof SamsungPaymentCancelledException)
                {
                    dialog = billingAlertDialogUtil.popUserCancelled(currentContext);
                }
                else
                {
                    dialog = billingAlertDialogUtil.popUnknownError(currentContext, exception);
                }
                // TODO finer dialog
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
            dismissProgressDialog();
            if (billingRequest.getPopRestorePurchaseOutcome())
            {
                Context currentContext = activityProvider.get();
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
                    ((THSamsungAlertDialogUtil) billingAlertDialogUtil).handlePurchaseRestoreFinished(
                            currentContext,
                            restoredPurchases,
                            failedRestorePurchases,
                            ((THSamsungAlertDialogUtil) billingAlertDialogUtil).createFailedRestoreClickListener(
                                    currentContext, exception),
                            billingRequest.getPopRestorePurchaseOutcomeVerbose());
                }
            }
        }
    }
    //</editor-fold>
}
