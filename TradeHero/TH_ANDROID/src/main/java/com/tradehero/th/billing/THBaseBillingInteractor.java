package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.persistence.DTOKey;
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
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class THBaseBillingInteractor<
        ProductIdentifierListKey extends DTOKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<
                ProductIdentifierType,
                OrderIdType>,
        THBillingLogicHolderType extends THBillingLogicHolder<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                THBillingRequestType,
                BillingExceptionType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                ProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                ProductDetailType,
                ProductDetailViewType>,
        THBillingRequestType extends THBillingRequest<
                        ProductIdentifierType,
                        ProductDetailType,
                        PurchaseOrderType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType>,
        THUIBillingRequestType extends THUIBillingRequest<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
        implements THBillingInteractor<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                THBillingLogicHolderType,
                THBillingRequestType,
                THUIBillingRequestType,
                BillingExceptionType>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    @Inject protected CurrentActivityHolder currentActivityHolder;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;

    protected Map<Integer, THUIBillingRequestType> uiBillingRequests;

    protected ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    public THBaseBillingInteractor()
    {
        super();
        DaggerUtils.inject(this);
        uiBillingRequests = new HashMap<>();
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    public void onPause()
    {
    }

    public void onStop()
    {
    }

    public void onDestroy()
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
            progressDialog = null;
        }
        THBillingLogicHolderType logicHolder = getBillingLogicHolder();
        for (Map.Entry<Integer, THUIBillingRequestType> entry : uiBillingRequests.entrySet())
        {
            if (logicHolder != null)
            {
                logicHolder.forgetRequestCode(entry.getKey());
            }
            entry.getValue().followResultListener = null;
            entry.getValue().purchaseFinishedListener = null;
        }
        uiBillingRequests.clear();
    }
    //</editor-fold>

    abstract protected BillingAlertDialogUtil<
        ProductIdentifierType,
        ProductDetailType,
        THBillingLogicHolderType,
        ProductDetailViewType,
        ProductDetailAdapterType> getBillingAlertDialogUtil();

    //<editor-fold desc="Request Code Management">
    @Override public int getUnusedRequestCode()
    {
        int retries = MAX_RANDOM_RETRIES;
        int randomNumber;
        while (retries-- > 0)
        {
            randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
            if (isUnusedRequestCode(randomNumber))
            {
                return randomNumber;
            }
        }
        throw new IllegalStateException("Could not find an unused requestCode after " + MAX_RANDOM_RETRIES + " trials");
    }

    public boolean isUnusedRequestCode(int requestCode)
    {
        return getBillingLogicHolder().isUnusedRequestCode(requestCode)
                && !uiBillingRequests.containsKey(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override public int run(THUIBillingRequestType uiBillingRequest)
    {
        int requestCode = getUnusedRequestCode();
        uiBillingRequests.put(requestCode, uiBillingRequest);
        // TODO show a dialog
        getBillingLogicHolder().run(requestCode, create(uiBillingRequest));
        return requestCode;
    }

    protected THBillingRequestType create(THUIBillingRequestType uiBillingRequest)
    {
        THBillingRequestType billingRequest = createEmptyBillingRequest(uiBillingRequest);
        populateBillingRequest(billingRequest, uiBillingRequest);
        return billingRequest;
    }

    abstract protected THBillingRequestType createEmptyBillingRequest(THUIBillingRequestType uiBillingRequest);

    protected void populateBillingRequest(THBillingRequestType request, THUIBillingRequestType uiBillingRequest)
    {
        request.billingAvailable = uiBillingRequest.billingAvailable;
        request.billingAvailableListener = createBillingAvailableListener();
        request.fetchProductIdentifiers = uiBillingRequest.fetchProductIdentifiers;
        request.productIdentifierFetchedListener = createProductIdentifierFetchedListener();
        request.fetchInventory = uiBillingRequest.fetchInventory;
        request.inventoryFetchedListener = createInventoryFetchedListener();
        request.fetchPurchase = uiBillingRequest.fetchPurchase;
        request.purchaseFetchedListener = createPurchaseFetchedListener();
        request.purchaseFinishedListener = createPurchaseFinishedListener();
        request.purchaseReportedListener = createPurchaseReportedListener();

        if (uiBillingRequest.domainToPresent != null)
        {
            request.fetchInventory = true;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Inventory Preparation">
    protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
        // TODO add a wait to inform the user
    }
    //</editor-fold>

    //<editor-fold desc="Product Detail Presentation">
    protected AlertDialog popBuyDialog(int requestCode, ProductIdentifierDomain skuDomain, int titleResId)
    {
        Activity currentActivity = currentActivityHolder.getCurrentActivity();
        if (currentActivity != null)
        {
            return getBillingAlertDialogUtil().popBuyDialog(
                    requestCode,
                    currentActivity,
                    getBillingLogicHolder(),
                    THBaseBillingInteractor.this,
                    skuDomain,
                    titleResId);
        }
        return null;
    }

    @Override public void onDialogProductDetailClicked(int requestCode, DialogInterface dialogInterface,
            int position, ProductDetailType productDetail)
    {
        launchPurchaseSequence(requestCode, productDetail.getProductIdentifier());
    }
    //</editor-fold>

    //<editor-fold desc="Purchasing Sequence">
    abstract protected THBillingRequestType createPurchaseBillingRequest(int requestCode, ProductIdentifierType productIdentifier);
    abstract protected void launchPurchaseSequence(int requestCode, ProductIdentifierType productIdentifier);
    //</editor-fold>

    //<editor-fold desc="Billing Available">
    protected OnBillingAvailableListener<BillingExceptionType> createBillingAvailableListener()
    {
        return new THBaseBillingInteractorBillingAvailableListener();
    }

    protected class THBaseBillingInteractorBillingAvailableListener implements OnBillingAvailableListener<BillingExceptionType>
    {
        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterBillingAvailableListener(requestCode);
            }
        }

        @Override public void onBillingAvailable(int requestCode)
        {
            forgetListener(requestCode);
            handleBillingAvailable(requestCode);
            notifyBillingAvailable(requestCode);
        }

        @Override public void onBillingNotAvailable(int requestCode, BillingExceptionType billingException)
        {
            forgetListener(requestCode);
            handleBillingNotAvailable(requestCode, billingException);
            notifyBillingNotAvailable(requestCode, billingException);
        }
    }

    protected void handleBillingAvailable(int requestCode)
    {
    }

    protected void notifyBillingAvailable(int requestCode)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.billingAvailableListener != null)
            {
                billingRequest.billingAvailableListener.onBillingAvailable(requestCode);
            }
        }
    }

    protected void handleBillingNotAvailable(int requestCode, BillingExceptionType billingException)
    {
    }

    protected void notifyBillingNotAvailable(int requestCode, BillingExceptionType billingException)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.billingAvailableListener != null)
            {
                billingRequest.billingAvailableListener.onBillingNotAvailable(requestCode, billingException);
            }
        }
        if (billingRequest == null || billingRequest.popIfBillingNotAvailable)
        {
            popBillingUnavailable(billingException);
        }
    }

    @Override public AlertDialog popBillingUnavailable(BillingExceptionType billingException)
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            return getBillingAlertDialogUtil().popBillingUnavailable(
                    currentActivityHolder.getCurrentContext(),
                    getBillingLogicHolder().getBillingHolderName(
                            currentContext.getResources()));
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Product Identifier Fetch">
    protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType> createProductIdentifierFetchedListener()
    {
        return new THBaseBillingInteractorOnProductIdentifierFetchedListener();
    }

    protected class THBaseBillingInteractorOnProductIdentifierFetchedListener implements ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType>
    {
        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterProductIdentifierFetchedListener(requestCode);
            }
        }

        @Override public void onFetchedProductIdentifiers(int requestCode, Map<String, List<ProductIdentifierType>> availableProductIdentifiers)
        {
            forgetListener(requestCode);
            handleFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
            notifyFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
        }

        @Override public void onFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
        {
            forgetListener(requestCode);
            handleFetchProductIdentifiersFailed(requestCode, exception);
            notifyFetchProductIdentifiersFailed(requestCode, exception);
        }
    }

    protected void handleFetchedProductIdentifiers(int requestCode, Map<String, List<ProductIdentifierType>> availableProductIdentifiers)
    {
    }

    protected void notifyFetchedProductIdentifiers(int requestCode, Map<String, List<ProductIdentifierType>> availableProductIdentifiers)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.productIdentifierFetchedListener != null)
            {
                billingRequest.productIdentifierFetchedListener.onFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
            }
        }
    }

    protected void handleFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
    {
    }

    protected void notifyFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.productIdentifierFetchedListener != null)
            {
                billingRequest.productIdentifierFetchedListener.onFetchProductIdentifiersFailed(requestCode, exception);
            }
        }
        if (billingRequest == null || billingRequest.popIfProductIdentifierFetchFailed)
        {
            popFetchProductIdentifiersFailed(requestCode, exception);
        }
    }

    protected AlertDialog popFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Inventory Fetch">
    protected BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> createInventoryFetchedListener()
    {
        return new THBaseBillingInteractorOnInventoryFetchedListener();
    }

    protected class THBaseBillingInteractorOnInventoryFetchedListener implements BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType>
    {
        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterInventoryFetchedListener(requestCode);
            }
        }

        @Override public void onInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
        {
            forgetListener(requestCode);
            handleInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
            notifyInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }

        @Override public void onInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
        {
            forgetListener(requestCode);
            handleInventoryFetchFail(requestCode, productIdentifiers, exception);
            notifyInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }

    protected void handleInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        THUIBillingRequestType thuiBillingRequest = uiBillingRequests.get(requestCode);
        if (thuiBillingRequest != null)
        {
            if (thuiBillingRequest.domainToPresent != null)
            {
                // TODO present domain products
            }
        }
    }

    protected void notifyInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        THUIBillingRequestType thuiBillingRequest = uiBillingRequests.get(requestCode);
        if (thuiBillingRequest != null)
        {
            if (thuiBillingRequest.inventoryFetchedListener != null)
            {
                thuiBillingRequest.inventoryFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
            }
        }
    }

    protected void handleInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
    }

    protected void notifyInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.inventoryFetchedListener != null)
            {
                billingRequest.inventoryFetchedListener.onInventoryFetchFail(requestCode, productIdentifiers, exception);
            }
        }
        if (billingRequest == null || billingRequest.popIfInventoryFetchFailed)
        {
            popInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }

    protected AlertDialog popInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Purchases">
    protected  BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> createPurchaseFetchedListener()
    {
        return new THBaseBillingInteractorOnPurchaseFetchedListener();
    }

    protected class THBaseBillingInteractorOnPurchaseFetchedListener implements BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>
    {
        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterPurchaseFetchedListener(requestCode);
            }
        }

        @Override public void onFetchedPurchases(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
        {
            forgetListener(requestCode);
            handleFetchedPurchases(requestCode, purchases);
            notifyFetchedPurchases(requestCode, purchases);
        }

        @Override public void onFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
        {
            forgetListener(requestCode);
            handleFetchPurchasesFailed(requestCode, exception);
            notifyFetchPurchasesFailed(requestCode, exception);
        }
    }

    protected void handleFetchedPurchases(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
    {
    }

    protected void notifyFetchedPurchases(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseFetchedListener != null)
            {
                billingRequest.purchaseFetchedListener.onFetchedPurchases(requestCode, purchases);
            }
        }
    }

    protected void handleFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
    {
    }

    protected void notifyFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseFetchedListener != null)
            {
                billingRequest.purchaseFetchedListener.onFetchPurchasesFailed(requestCode, exception);
            }
        }
        if (billingRequest == null || billingRequest.popIfPurchaseFetchFailed)
        {
            popFetchPurchasesFailed(requestCode, exception);
        }
    }

    protected AlertDialog popFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase">
    protected BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> createPurchaseFinishedListener()
    {
        return new THBaseBillingInteractorOnPurchaseFinishedListener();
    }

    protected class THBaseBillingInteractorOnPurchaseFinishedListener implements BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>
    {
        public THBaseBillingInteractorOnPurchaseFinishedListener()
        {
            super();
        }

        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterPurchaseFinishedListener(requestCode);
            }
        }

        @Override public void onPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
        {
            forgetListener(requestCode);
            handlePurchaseFinished(requestCode, purchaseOrder, purchase);
            notifyPurchaseFinished(requestCode, purchaseOrder, purchase);
        }

        @Override public void onPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
        {
            forgetListener(requestCode);
            handlePurchaseFailed(requestCode, purchaseOrder, billingException);
            notifyPurchaseFailed(requestCode, purchaseOrder, billingException);
        }
    }

    protected void handlePurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
    }

    protected void notifyPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseFinishedListener != null)
            {
                billingRequest.purchaseFinishedListener.onPurchaseFinished(requestCode, purchaseOrder, purchase);
            }
        }
    }

    protected void handlePurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
    }

    protected void notifyPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseFinishedListener != null)
            {
                billingRequest.purchaseFinishedListener.onPurchaseFailed(requestCode, purchaseOrder, billingException);
            }
        }
        if (billingRequest == null || billingRequest.popIfPurchaseFailed)
        {
            popPurchaseFailed(requestCode, purchaseOrder, billingException);
        }
    }

    protected AlertDialog popPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Reporting Sequence">
    protected PurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> createPurchaseReportedListener()
    {
        return new THBaseBillingInteractorOnPurchaseReportedListener();
    }

    protected class THBaseBillingInteractorOnPurchaseReportedListener implements PurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>
    {
        public THBaseBillingInteractorOnPurchaseReportedListener()
        {
            super();
        }

        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterPurchaseReportedListener(requestCode);
            }
        }

        @Override public void onPurchaseReported(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
        {
            forgetListener(requestCode);
            handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
            notifyPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            // Children should continue with the sequence
        }

        @Override public void onPurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
        {
            forgetListener(requestCode);
            handlePurchaseReportFailed(requestCode, reportedPurchase, error);
            notifyPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
    }

    protected void handlePurchaseReportSuccess(ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserProfile)
    {
    }

    protected void notifyPurchaseReported(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseReportedListener != null)
            {
                billingRequest.purchaseReportedListener.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }
        }
    }

    protected void handlePurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    protected void notifyPurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseReportedListener != null)
            {
                billingRequest.purchaseReportedListener.onPurchaseReportFailed(requestCode, reportedPurchase, error);
            }
        }
        if (billingRequest == null || billingRequest.popIfReportFailed)
        {
            popPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
    }

    protected AlertDialog popPurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        Context currentContext = currentActivityHolder.getCurrentActivity();
        if (currentContext != null)
        {
            return getBillingAlertDialogUtil().popFailedToReport(currentContext);
        }
        return null;
    }
    //</editor-fold>

    protected void popDialogLoadingInfo()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = ProgressDialogUtil.show(
                    currentContext,
                    R.string.store_billing_loading_info_window_title,
                    R.string.store_billing_loading_info_window_message
            );
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
    }


}
