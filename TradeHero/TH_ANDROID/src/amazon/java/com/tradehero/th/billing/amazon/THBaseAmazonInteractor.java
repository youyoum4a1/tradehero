package com.tradehero.th.billing.amazon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.amazon.AmazonConstants;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.exception.AmazonFetchInventoryFailedException;
import com.tradehero.common.billing.amazon.exception.AmazonFetchInventoryUnsupportedException;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractor;
import com.tradehero.th.billing.amazon.request.THAmazonRequestFull;
import com.tradehero.th.billing.amazon.request.THUIAmazonRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.THAmazonSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THAmazonStoreProductDetailView;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class THBaseAmazonInteractor
    extends
        THBaseBillingInteractor<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonLogicHolder,
                THAmazonStoreProductDetailView,
                THAmazonSKUDetailAdapter,
                THAmazonRequestFull,
                THUIAmazonRequest,
                AmazonException>
    implements THAmazonInteractor
{
    public static final String BUNDLE_KEY_ACTION = THBaseAmazonInteractor.class.getName() + ".action";

    @NotNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NotNull protected final HeroListCache heroListCache;
    @NotNull protected final UserService userService;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonInteractor(
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull PortfolioCompactListCache portfolioCompactListCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull THAmazonAlertDialogUtil thAmazonAlertDialogUtil,
            @NotNull THAmazonLogicHolder billingActor,
            @NotNull UserProfileDTOUtil userProfileDTOUtil,
            @NotNull HeroListCache heroListCache,
            @NotNull UserService userService)
    {
        super(
                billingActor,
                currentActivityHolder,
                currentUserId,
                userProfileCache,
                portfolioCompactListCache,
                progressDialogUtil,
                thAmazonAlertDialogUtil);
        this.userProfileDTOUtil = userProfileDTOUtil;
        this.heroListCache = heroListCache;
        this.userService = userService;
    }
    //</editor-fold>

    @Override public String getName()
    {
        return AmazonConstants.NAME;
    }

    //<editor-fold desc="Life Cycle">
    public void onDestroy()
    {
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override public int run(@NotNull THUIAmazonRequest uiBillingRequest)
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

    @Override protected THAmazonRequestFull createBillingRequest(
            @NotNull THUIAmazonRequest uiBillingRequest)
    {
        THAmazonRequestFull.Builder<?> builder = THAmazonRequestFull.builder();
        populateBillingRequestBuilder(builder, uiBillingRequest);
        return builder.build();
    }

    protected void populateBillingRequestBuilder(
            @NotNull THAmazonRequestFull.Builder<?> builder,
            @NotNull THUIAmazonRequest uiBillingRequest)
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
                    //        getBillingLogicHolder().launchInventoryFetchSequence(requestCode, new ArrayList<AmazonSKU>());
                    //    }
                    //})
                    ;
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return alertDialog;
    }

    protected BillingInventoryFetcher.OnInventoryFetchedListener<AmazonSKU, THAmazonProductDetail, AmazonException> createForgetFetchedListener()
    {
        return new BillingInventoryFetcher.OnInventoryFetchedListener<AmazonSKU, THAmazonProductDetail, AmazonException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<AmazonSKU> productIdentifiers, Map<AmazonSKU, THAmazonProductDetail> inventory)
            {
                billingLogicHolder.forgetRequestCode(requestCode);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<AmazonSKU> productIdentifiers, AmazonException exception)
            {
                billingLogicHolder.forgetRequestCode(requestCode);
            }
        };
    }

    protected void showProgressFollow()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
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
            List<AmazonSKU> productIdentifiers, AmazonException exception)
    {
        AlertDialog dialog = super.popInventoryFetchFail(requestCode, productIdentifiers, exception);
        if (dialog == null)
        {
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                if (exception instanceof AmazonFetchInventoryFailedException
                        || exception instanceof AmazonFetchInventoryUnsupportedException)
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
    @Override protected void launchPurchaseSequence(int requestCode, AmazonSKU productIdentifier)
    {
        billingLogicHolder.run(requestCode, createPurchaseBillingRequest(requestCode, productIdentifier));
    }

    @Override protected THAmazonRequestFull createEmptyBillingRequest()
    {
        return THAmazonRequestFull.builder().build();
    }

    @Override protected void populatePurchaseBillingRequest(
            int requestCode,
            THAmazonRequestFull request,
            @NotNull AmazonSKU productIdentifier)
    {
        super.populatePurchaseBillingRequest(requestCode, request, productIdentifier);
        THUIBillingRequest uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            // Nothing to do I suppose
        }
    }

    @Override @NotNull protected THAmazonPurchaseOrder createEmptyPurchaseOrder(
            @NotNull THUIAmazonRequest uiBillingRequest,
            @NotNull AmazonSKU productIdentifier)
    {
        return new THAmazonPurchaseOrder(productIdentifier, 1, uiBillingRequest.getApplicablePortfolioId());
    }

    @Override protected AlertDialog popPurchaseFailed(
            int requestCode,
            THAmazonPurchaseOrder purchaseOrder,
            AmazonException exception,
            AlertDialog.OnClickListener restoreClickListener)
    {
        Timber.d("popPurchaseFailed");
        AlertDialog dialog = super.popPurchaseFailed(requestCode, purchaseOrder, exception, restoreClickListener);
        if (dialog == null)
        {
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                // TODO finer dialog
                dialog = billingAlertDialogUtil.popUnknownError(currentContext, exception);
            }
        }
        return dialog;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Restore">
    @Override
    protected void notifyPurchaseRestored(int requestCode, List<THAmazonPurchase> restoredPurchases, List<THAmazonPurchase> failedRestorePurchases,
            List<AmazonException> failExceptions)
    {
        super.notifyPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
        THUIAmazonRequest billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            dismissProgressDialog();
            if (billingRequest.getPopRestorePurchaseOutcome())
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
                    ((THAmazonAlertDialogUtil) billingAlertDialogUtil).handlePurchaseRestoreFinished(
                            currentContext,
                            restoredPurchases,
                            failedRestorePurchases,
                            ((THAmazonAlertDialogUtil) billingAlertDialogUtil).createFailedRestoreClickListener(
                                    currentContext, exception),
                            billingRequest.getPopRestorePurchaseOutcomeVerbose());
                }
            }
        }
    }
    //</editor-fold>
}
