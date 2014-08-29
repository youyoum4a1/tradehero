package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABConstants;
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
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractor;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.billing.googleplay.request.THUIIABRequest;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.THIABSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THIABStoreProductDetailView;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Singleton public class THIABBillingInteractor
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
                THIABSKUDetailAdapter,
                THIABBillingRequestFull,
                THUIIABRequest,
                IABException>
    implements THIABInteractor
{
    public static final String BUNDLE_KEY_ACTION = THIABBillingInteractor.class.getName() + ".action";

    @NotNull protected final THIABProductDetailCache thiabProductDetailCache;
    @NotNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NotNull protected final HeroListCache heroListCache;
    @NotNull protected final UserService userService;

    //<editor-fold desc="Constructors">
    @Inject public THIABBillingInteractor(
            @NotNull THIABLogicHolder billingActor,
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull PortfolioCompactListCache portfolioCompactListCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull THIABAlertDialogUtil thIABAlertDialogUtil,
            @NotNull THIABProductDetailCache thiabProductDetailCache,
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
                thIABAlertDialogUtil);
        this.thiabProductDetailCache = thiabProductDetailCache;
        this.userProfileDTOUtil = userProfileDTOUtil;
        this.heroListCache = heroListCache;
        this.userService = userService;
    }
    //</editor-fold>

    @Override public String getName()
    {
        return IABConstants.NAME;
    }

    //<editor-fold desc="Request Handling">
    @Override protected THIABBillingRequestFull createBillingRequest(
            @NotNull THUIIABRequest uiBillingRequest)
    {
        THIABBillingRequestFull.Builder<?> builder = (THIABBillingRequestFull.Builder<?>) uiBillingRequest.createEmptyBillingRequestBuilder();
        populateBillingRequestBuilder(builder, uiBillingRequest);
        return builder.build();
    }

    @Override protected void populateBillingRequestBuilder(
            @NotNull THBillingRequest.Builder<
                    IABSKUListKey,
                    IABSKU,
                    IABSKUList,
                    THIABProductDetail,
                    THIABPurchaseOrder,
                    THIABOrderId,
                    THIABPurchase,
                    IABException,
                    ?> builder,
            @NotNull THUIIABRequest uiBillingRequest)
    {
        super.populateBillingRequestBuilder(builder, uiBillingRequest);

        if (uiBillingRequest.getDomainToPresent() != null)
        {
            builder.testBillingAvailable(true)
                    .fetchProductIdentifiers(true)
                    .fetchInventory(true);
        }
        else if (uiBillingRequest.getRestorePurchase())
        {
            builder.testBillingAvailable(true)
                    .fetchProductIdentifiers(true)
                    .fetchInventory(true)
                    .fetchPurchases(true)
                    .restorePurchase(true);
        }
        else if (uiBillingRequest.getFetchInventory())
        {
            builder.testBillingAvailable(true)
                    .fetchProductIdentifiers(true)
                    .fetchInventory(true);
        }
        else if (uiBillingRequest.getFetchProductIdentifiers())
        {
            builder.testBillingAvailable(true)
                    .fetchProductIdentifiers(true);
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
                billingLogicHolder.forgetRequestCode(requestCode);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<IABSKU> productIdentifiers, IABException exception)
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

    @Override protected void runRequestCode(int requestCode)
    {
        THUIIABRequest uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            if (uiBillingRequest.getManageSubscriptions())
            {
                Activity currentActivity = currentActivityHolder.getCurrentActivity();
                if (currentActivity != null)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(IABConstants.GOOGLE_PLAY_ACCOUNT_URL));
                    currentActivity.startActivity(intent);
                }
            }
            else
            {
                super.runRequestCode(requestCode);
            }
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
                    dialog = billingAlertDialogUtil.popUserCancelled(currentContext);
                }
                else if (exception instanceof IABBadResponseException)
                {
                    dialog = ((THIABAlertDialogUtil) billingAlertDialogUtil).popBadResponse(currentContext);
                }
                else if (exception instanceof IABResultErrorException)
                {
                    dialog = ((THIABAlertDialogUtil) billingAlertDialogUtil).popResultError(currentContext);
                }
                else if (!(exception instanceof IABBillingUnavailableException)) // No need to tell again
                {
                    dialog = billingAlertDialogUtil.popUnknownError(currentContext, exception);
                }
            }
        }
        return dialog;    }
    //</editor-fold>

    //<editor-fold desc="Purchase Actions">
    @Override protected void launchPurchaseSequence(int requestCode, IABSKU productIdentifier)
    {
        Timber.e(new Exception(), "Purchase %s", productIdentifier);
        billingLogicHolder.run(requestCode, createPurchaseBillingRequest(requestCode, productIdentifier));
    }

    @Override protected THIABBillingRequestFull createEmptyBillingRequest()
    {
        return THIABBillingRequestFull.builder().build();
    }

    @Override protected void populatePurchaseBillingRequest(
            int requestCode,
            THIABBillingRequestFull request,
            @NotNull IABSKU productIdentifier)
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

    @Override @NotNull protected THIABPurchaseOrder createEmptyPurchaseOrder(
            @NotNull THUIIABRequest uiBillingRequest,
            @NotNull IABSKU productIdentifier)
    {
        return new THIABPurchaseOrder(productIdentifier, uiBillingRequest.getApplicablePortfolioId());
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
                    dialog = ((THIABAlertDialogUtil) billingAlertDialogUtil).popVerificationFailed(currentContext);
                }
                else if (exception instanceof IABUserCancelledException)
                {
                    dialog = billingAlertDialogUtil.popUserCancelled(currentContext);
                }
                else if (exception instanceof IABBadResponseException)
                {
                    dialog = ((THIABAlertDialogUtil) billingAlertDialogUtil).popBadResponse(currentContext);
                }
                else if (exception instanceof IABResultErrorException)
                {
                    dialog = ((THIABAlertDialogUtil) billingAlertDialogUtil).popResultError(currentContext);
                }
                else if (exception instanceof IABRemoteException)
                {
                    dialog = billingAlertDialogUtil.popRemoteError(currentContext);
                }
                else if (exception instanceof IABItemAlreadyOwnedException)
                {
                    dialog = billingAlertDialogUtil.popSKUAlreadyOwned(
                            currentContext,
                            thiabProductDetailCache.get(purchaseOrder.getProductIdentifier()),
                            restoreClickListener);
                }
                else if (exception instanceof IABSendIntentException)
                {
                    dialog = ((THIABAlertDialogUtil) billingAlertDialogUtil).popSendIntent(currentContext);
                }
                else
                {
                    dialog = billingAlertDialogUtil.popUnknownError(currentContext, exception);
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
        THUIIABRequest billingRequest = uiBillingRequests.get(requestCode);
        Timber.e(new Exception(), "purchase restored %s", billingRequest);
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
                    ((THIABAlertDialogUtil) billingAlertDialogUtil).handlePurchaseRestoreFinished(
                            currentContext,
                            restoredPurchases,
                            failedRestorePurchases,
                            ((THIABAlertDialogUtil) billingAlertDialogUtil).createFailedRestoreClickListener(currentContext, exception),
                            billingRequest.getPopRestorePurchaseOutcomeVerbose());
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
        @Override public void onPurchaseConsumed(int requestCode, THIABPurchase purchase)
        {
            handlePurchaseConsumed(purchase);
            notifyPurchaseConsumed(requestCode, purchase);
        }

        @Override public void onPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
        {
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
        if (thuiBillingRequest != null)
        {
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                    IABSKU,
                    THIABOrderId,
                    THIABPurchase,
                    IABException> consumptionFinishedListener = ((THUIIABRequest) thuiBillingRequest).getConsumptionFinishedListener();
            if (consumptionFinishedListener != null)
            {
                consumptionFinishedListener.onPurchaseConsumed(requestCode, purchase);
            }
        }
    }

    protected void handlePurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        THUIIABRequest billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.getStartWithProgressDialog())
        {
            dismissProgressDialog();
        }
    }

    protected void notifyPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        THUIBillingRequest billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                    IABSKU,
                    THIABOrderId,
                    THIABPurchase,
                    IABException> consumptionFinishedListener = ((THUIIABRequest) billingRequest).getConsumptionFinishedListener();
            if (consumptionFinishedListener != null)
            {
                consumptionFinishedListener.onPurchaseConsumeFailed(requestCode, purchase, exception);
            }
        }

        if (billingRequest == null ||
                        (billingRequest instanceof THUIIABRequest &&
                                ((THUIIABRequest) billingRequest).getPopIfConsumeFailed()))
        {
            popPurchaseConsumeFailed(requestCode, purchase, exception);
        }
    }

    protected AlertDialog popPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            return ((THIABAlertDialogUtil) billingAlertDialogUtil).popOfferSendEmailSupportConsumeFailed(currentContext, exception);
        }
        return null;
    }
    //</editor-fold>
}
