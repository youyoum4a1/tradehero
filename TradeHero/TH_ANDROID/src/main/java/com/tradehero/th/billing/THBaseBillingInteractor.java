package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.tradehero.common.billing.BaseBillingInteractor;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

abstract public class THBaseBillingInteractor<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<
                ProductIdentifierType,
                THOrderIdType>,
        THBillingLogicHolderType extends THBillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                THBillingRequestType,
                BillingExceptionType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                THProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                THProductDetailType,
                ProductDetailViewType>,
        THBillingRequestType extends THBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        THUIBillingRequestType extends THUIBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
        extends BaseBillingInteractor<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        THProductDetailType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType,
        THBillingLogicHolderType,
        THBillingRequestType,
        THUIBillingRequestType,
        BillingExceptionType>
        implements THBillingInteractor<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        THProductDetailType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType,
        THBillingLogicHolderType,
        THBillingRequestType,
        THUIBillingRequestType,
        BillingExceptionType>
{
    public static final int ACTION_RESET_PORTFOLIO = 1;

    @NotNull protected final CurrentActivityHolder currentActivityHolder;
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final UserProfileCache userProfileCache;
    @NotNull protected final PortfolioCompactListCache portfolioCompactListCache;
    @NotNull protected final ProgressDialogUtil progressDialogUtil;
    @NotNull protected final BillingAlertDialogUtil billingAlertDialogUtil;

    protected THBillingInitialMilestone THBillingInitialMilestone;
    protected Milestone.OnCompleteListener billingInitialMilestoneListener;
    protected LinkedList<Integer> requestsToLaunchOnBillingInitialMilestoneComplete;

    //<editor-fold desc="Constructors">
    protected THBaseBillingInteractor(
            @NotNull THBillingLogicHolderType billingLogicHolder,
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull PortfolioCompactListCache portfolioCompactListCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull BillingAlertDialogUtil<
                    ProductIdentifierType,
                    THProductDetailType,
                    THBillingLogicHolderType,
                    ProductDetailViewType,
                    ProductDetailAdapterType> billingAlertDialogUtil)
    {
        super(billingLogicHolder);
        this.currentActivityHolder = currentActivityHolder;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.progressDialogUtil = progressDialogUtil;
        this.billingAlertDialogUtil = billingAlertDialogUtil;

        requestsToLaunchOnBillingInitialMilestoneComplete = new LinkedList<>();

        billingInitialMilestoneListener = new THBaseBillingInteractorShowProductDetailCompleteListener();
        THBillingInitialMilestone = new THBillingInitialMilestone(currentUserId.toUserBaseKey());
        THBillingInitialMilestone.setOnCompleteListener(billingInitialMilestoneListener);
        THBillingInitialMilestone.launch();
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    @Override public void onDestroy()
    {
        billingInitialMilestoneListener = null;
        if (THBillingInitialMilestone != null)
        {
            THBillingInitialMilestone.setOnCompleteListener(null);
        }
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override public int run(@NotNull THUIBillingRequestType uiBillingRequest)
    {
        int requestCode = super.run(uiBillingRequest);
        if (uiBillingRequest.getStartWithProgressDialog())
        {
            popInitialProgressDialog(uiBillingRequest);
        }
        requestsToLaunchOnBillingInitialMilestoneComplete.addLast(requestCode);
        THBillingInitialMilestone.launch();
        Timber.e(new Exception(), "run %d", requestCode);
        return requestCode;
    }

    protected void runWaitingRequests()
    {
        Timber.e(new Exception(), "runWaitingRequests %d", requestsToLaunchOnBillingInitialMilestoneComplete.size());
        while (requestsToLaunchOnBillingInitialMilestoneComplete.size() > 0)
        {
            runRequestCode(requestsToLaunchOnBillingInitialMilestoneComplete.removeFirst());
        }
    }

    protected void populateBillingRequestBuilder(
            @NotNull THBillingRequestType.Builder<ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    THProductDetailType,
                    THPurchaseOrderType,
                    THOrderIdType,
                    THProductPurchaseType,
                    BillingExceptionType,
                    ?> builder,
            @NotNull THUIBillingRequestType uiBillingRequest)
    {
        builder
                .testBillingAvailable(uiBillingRequest.getTestBillingAvailable())
                .billingAvailableListener(createBillingAvailableListener())
                .fetchProductIdentifiers(uiBillingRequest.getFetchProductIdentifiers())
                .productIdentifierFetchedListener(createProductIdentifierFetchedListener())
                .fetchInventory(uiBillingRequest.getFetchInventory())
                .inventoryFetchedListener(createInventoryFetchedListener())
                .fetchPurchases(uiBillingRequest.getFetchPurchases())
                .purchaseFetchedListener(createPurchaseFetchedListener())
                .restorePurchase(uiBillingRequest.getRestorePurchase())
                .purchaseRestorerListener(createPurchaseRestorerFinishedListener())
                .doPurchase(uiBillingRequest.getDoPurchase())
                .purchaseFinishedListener(createPurchaseFinishedListener())
                .reportPurchase(uiBillingRequest.getReportPurchase())
                .purchaseReportedListener(createPurchaseReportedListener());
    }
    //</editor-fold>

    //<editor-fold desc="Inventory Preparation">
    protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
    {
        dismissProgressDialog();
        // TODO add a wait to inform the user
    }
    //</editor-fold>

    //<editor-fold desc="Product Detail Presentation">
    protected AlertDialog popBuyDialog(int requestCode, ProductIdentifierDomain productIdentifierDomain)
    {
        return popBuyDialog(requestCode, productIdentifierDomain, productIdentifierDomain.storeTitleResId);
    }

    protected AlertDialog popBuyDialog(int requestCode, ProductIdentifierDomain productIdentifierDomain, int titleResId)
    {
        Activity currentActivity = currentActivityHolder.getCurrentActivity();
        if (currentActivity != null)
        {
            dismissProgressDialog();
            return billingAlertDialogUtil.popBuyDialog(
                    requestCode,
                    currentActivity,
                    billingLogicHolder,
                    this,
                    productIdentifierDomain,
                    titleResId);
        }
        return null;
    }

    @Override public void onDialogProductDetailClicked(int requestCode, DialogInterface dialogInterface,
            int position, THProductDetailType productDetail)
    {
        launchPurchaseSequence(requestCode, productDetail.getProductIdentifier());
    }
    //</editor-fold>

    //<editor-fold desc="Purchasing Sequence">
    abstract protected void launchPurchaseSequence(int requestCode, ProductIdentifierType productIdentifier);

    protected THBillingRequestType createPurchaseBillingRequest(int requestCode, ProductIdentifierType productIdentifier)
    {
        THBillingRequestType request = createEmptyBillingRequest();
        populatePurchaseBillingRequest(requestCode, request, productIdentifier);
        return request;
    }

    abstract protected THBillingRequestType createEmptyBillingRequest();

    protected void populatePurchaseBillingRequest(
            int requestCode,
            THBillingRequestType billingRequest,
            @NotNull ProductIdentifierType productIdentifier)
    {
        THUIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            billingRequest.doPurchase = true;
            billingRequest.purchaseOrder = createPurchaseOrder(uiBillingRequest, productIdentifier);
            billingRequest.purchaseFinishedListener = createPurchaseFinishedListener();
            billingRequest.reportPurchase = true;
            billingRequest.purchaseReportedListener = createPurchaseReportedListener();
        }
        Timber.e(new Exception(), "now purchase %s", billingRequest);
    }

    protected THPurchaseOrderType createPurchaseOrder(
            @NotNull THUIBillingRequestType uiBillingRequest,
            @NotNull ProductIdentifierType productIdentifier)
    {
        THPurchaseOrderType purchaseOrder = createEmptyPurchaseOrder(uiBillingRequest, productIdentifier);
        populatePurchaseOrder(uiBillingRequest, purchaseOrder);
        return purchaseOrder;
    }

    @NotNull abstract protected THPurchaseOrderType createEmptyPurchaseOrder(
            @NotNull THUIBillingRequestType uiBillingRequest,
            @NotNull ProductIdentifierType productIdentifier);

    protected void populatePurchaseOrder(
            @NotNull THUIBillingRequestType uiBillingRequest,
            @NotNull THPurchaseOrderType purchaseOrder)
    {
        purchaseOrder.setUserToFollow(uiBillingRequest.getUserToPremiumFollow());
    }
    //</editor-fold>

    //<editor-fold desc="Billing Available">
    protected BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> createBillingAvailableListener()
    {
        return new THBaseBillingInteractorBillingAvailableListener();
    }

    protected class THBaseBillingInteractorBillingAvailableListener implements BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType>
    {
        @Override public void onBillingAvailable(int requestCode)
        {
            handleBillingAvailable(requestCode);
            notifyBillingAvailable(requestCode);
        }

        @Override public void onBillingNotAvailable(int requestCode, BillingExceptionType billingException)
        {
            handleBillingNotAvailable(requestCode, billingException);
            notifyBillingNotAvailable(requestCode, billingException);
        }
    }

    protected void handleBillingAvailable(int requestCode)
    {
    }

    protected void handleBillingNotAvailable(int requestCode, BillingExceptionType billingException)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.getStartWithProgressDialog())
        {
            dismissProgressDialog();
        }
    }

    @Override public AlertDialog popBillingUnavailable(BillingExceptionType billingException)
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            return billingAlertDialogUtil.popBillingUnavailable(
                    currentActivityHolder.getCurrentContext(),
                    billingLogicHolder.getBillingHolderName(
                            currentContext.getResources()));
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Product Identifier Fetch">
    @NotNull protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> createProductIdentifierFetchedListener()
    {
        return new THBaseBillingInteractorOnProductIdentifierFetchedListener();
    }

    protected class THBaseBillingInteractorOnProductIdentifierFetchedListener implements ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType>
    {
        @Override public void onFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
        {
            handleFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
            notifyFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
        }

        @Override public void onFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
        {
            handleFetchProductIdentifiersFailed(requestCode, exception);
            notifyFetchProductIdentifiersFailed(requestCode, exception);
        }
    }

    protected void handleFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
    {
    }

    protected void handleFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.getStartWithProgressDialog())
        {
            dismissProgressDialog();
        }
    }

    protected AlertDialog popFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Inventory Fetch">
    @Override protected BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            THProductDetailType,
            BillingExceptionType> createInventoryFetchedListener()
    {
        return new THBaseBillingInteractorOnInventoryFetchedListener();
    }

    protected class THBaseBillingInteractorOnInventoryFetchedListener extends BaseBillingInteractorOnInventoryFetchListenerWrapper
    {
        @Override public void onInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, THProductDetailType> inventory)
        {
            Timber.d("Inventory fetched count %d", productIdentifiers.size());
            super.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
            handleInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }

        @Override public void onInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
        {
            Timber.e(exception, "inventory failed");
            super.onInventoryFetchFail(requestCode, productIdentifiers, exception);
            handleInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }

    protected void handleInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, THProductDetailType> inventory)
    {
        THUIBillingRequestType thuiBillingRequest = uiBillingRequests.get(requestCode);
        if (thuiBillingRequest != null)
        {
            if (thuiBillingRequest.getDomainToPresent() != null)
            {
                billingLogicHolder.forgetRequestCode(requestCode);
                popBuyDialog(requestCode, thuiBillingRequest.getDomainToPresent());
            }
        }
    }

    protected void handleInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.getStartWithProgressDialog())
        {
            dismissProgressDialog();
        }
    }

    protected AlertDialog popInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        Timber.e(exception, "");
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Purchases">
    protected  BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType> createPurchaseFetchedListener()
    {
        return new THBaseBillingInteractorOnPurchaseFetchedListener();
    }

    protected class THBaseBillingInteractorOnPurchaseFetchedListener implements BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType>
    {
        @Override public void onFetchedPurchases(int requestCode, List<THProductPurchaseType> purchases)
        {
            handleFetchedPurchases(requestCode, purchases);
            notifyFetchedPurchases(requestCode, purchases);
        }

        @Override public void onFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
        {
            handleFetchPurchasesFailed(requestCode, exception);
            notifyFetchPurchasesFailed(requestCode, exception);
        }
    }

    protected void handleFetchedPurchases(int requestCode, List<THProductPurchaseType> purchases)
    {
    }

    protected void handleFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.getStartWithProgressDialog())
        {
            dismissProgressDialog();
        }
    }

    protected AlertDialog popFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Restore">
    protected BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> createPurchaseRestorerFinishedListener()
    {
        return new BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseRestored(int requestCode, List<THProductPurchaseType> restoredPurchases, List<THProductPurchaseType> failedRestorePurchases,
                    List<BillingExceptionType> failExceptions)
            {
                Timber.d("onPurchaseRestored %d, %d, %d", restoredPurchases.size(), failedRestorePurchases.size(), failExceptions.size());
                billingLogicHolder.unregisterPurchaseRestorerListener(requestCode);
                handlePurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
            }
        };
    }

    protected void handlePurchaseRestored(int requestCode, List<THProductPurchaseType> restoredPurchases, List<THProductPurchaseType> failedRestorePurchases, List<BillingExceptionType> failExceptions)
    {
        notifyPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
    }
    //</editor-fold>

    //<editor-fold desc="Purchase">
    protected BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType> createPurchaseFinishedListener()
    {
        return new THBaseBillingInteractorOnPurchaseFinishedListener();
    }

    protected class THBaseBillingInteractorOnPurchaseFinishedListener implements BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            THPurchaseOrderType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType>
    {
        @Override public void onPurchaseFinished(int requestCode, THPurchaseOrderType purchaseOrder, THProductPurchaseType purchase)
        {
            handlePurchaseFinished(requestCode, purchaseOrder, purchase);
            notifyPurchaseFinished(requestCode, purchaseOrder, purchase);
        }

        @Override public void onPurchaseFailed(int requestCode, THPurchaseOrderType purchaseOrder, BillingExceptionType billingException)
        {
            handlePurchaseFailed(requestCode, purchaseOrder, billingException);
            notifyPurchaseFailed(requestCode, purchaseOrder, billingException);
        }
    }

    protected void handlePurchaseFinished(int requestCode, THPurchaseOrderType purchaseOrder, THProductPurchaseType purchase)
    {
    }

    protected void handlePurchaseFailed(int requestCode, THPurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.getStartWithProgressDialog())
        {
            dismissProgressDialog();
        }
    }

    protected AlertDialog popPurchaseFailed(
            int requestCode,
            THPurchaseOrderType purchaseOrder,
            BillingExceptionType billingException,
            AlertDialog.OnClickListener restoreClickListener)
    {
        Timber.e(billingException, "");
        return null;
    }

    protected void flipRequestFromPurchaseToRestore(int requestCode)
    {
        super.flipRequestFromPurchaseToRestore(requestCode);
        THUIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            uiBillingRequest.setDomainToPresent(null);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Reporting Sequence">
    protected THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType> createPurchaseReportedListener()
    {
        return new THBaseBillingInteractorOnPurchaseReportedListener();
    }

    protected class THBaseBillingInteractorOnPurchaseReportedListener implements THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType>
    {
        public THBaseBillingInteractorOnPurchaseReportedListener()
        {
            super();
        }

        @Override public void onPurchaseReported(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
        {
            Timber.d("THBaseBillingInteractor onPurchaseReported " + updatedUserPortfolio);
            handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
            notifyPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            // Children should continue with the sequence
        }

        @Override public void onPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
        {
            handlePurchaseReportFailed(requestCode, reportedPurchase, error);
            notifyPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
    }

    protected void handlePurchaseReportSuccess(THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserProfile)
    {
    }

    protected void notifyPurchaseReported(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        Timber.d("THBaseBillingInteractor notifyPurchaseReported " + updatedUserPortfolio);
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            THPurchaseReporter.OnPurchaseReportedListener<
                    ProductIdentifierType,
                    THOrderIdType,
                    THProductPurchaseType,
                    BillingExceptionType> purchaseReportedListener = billingRequest.getPurchaseReportedListener();
            Timber.d("THBaseBillingInteractor notifyPurchaseReported no request " + updatedUserPortfolio);
            if (purchaseReportedListener != null)
            {
                Timber.d("THBaseBillingInteractor notifyPurchaseReported null listener " + updatedUserPortfolio);
                purchaseReportedListener.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }
        }
    }

    protected void handlePurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.getStartWithProgressDialog())
        {
            dismissProgressDialog();
        }
    }

    protected void notifyPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            THPurchaseReporter.OnPurchaseReportedListener<
                    ProductIdentifierType,
                    THOrderIdType,
                    THProductPurchaseType,
                    BillingExceptionType> purchaseReportedListener = billingRequest.getPurchaseReportedListener();
            if (purchaseReportedListener != null)
            {
                purchaseReportedListener.onPurchaseReportFailed(requestCode, reportedPurchase, error);
            }
        }
        if (billingRequest == null || billingRequest.getPopIfReportFailed())
        {
            popPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
    }

    protected AlertDialog popPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        Context currentContext = currentActivityHolder.getCurrentActivity();
        if (currentContext != null)
        {
            return billingAlertDialogUtil.popFailedToReport(currentContext);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Progress Dialog">
    protected void popInitialProgressDialog(THUIBillingRequestType billingRequest)
    {
        if (billingRequest.getRestorePurchase())
        {
            popRestorePurchaseProgress();
        }
        else
        {
            popProgressDialogLoadingInfo();
        }
    }

    protected void popProgressDialogLoadingInfo()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            dismissProgressDialog();
            progressDialog = progressDialogUtil.show(
                    currentContext,
                    R.string.store_billing_loading_info_window_title,
                    R.string.store_billing_loading_info_window_message);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
    }

    protected void popRestorePurchaseProgress()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            dismissProgressDialog();
            progressDialog = progressDialogUtil.show(
                    currentContext,
                    R.string.store_billing_restoring_purchase_window_title,
                    R.string.store_billing_restoring_purchase_window_message);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
    }
    //</editor-fold>

    protected class THBaseBillingInteractorShowProductDetailCompleteListener implements Milestone.OnCompleteListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            runWaitingRequests();
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            // TODO
        }
    }
}
