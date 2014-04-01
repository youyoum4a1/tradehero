package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier on 2/24/14.
 */
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
    public static final int MAX_RANDOM_RETRIES = 50;
    public static final int ACTION_RESET_PORTFOLIO = 1;

    @Inject protected CurrentActivityHolder currentActivityHolder;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;

    protected THBillingInitialMilestone THBillingInitialMilestone;
    protected Milestone.OnCompleteListener billingInitialMilestoneListener;
    protected LinkedList<Integer> requestsToLaunchOnBillingInitialMilestoneComplete;

    protected Map<Integer, THUIBillingRequestType> uiBillingRequests;

    @Inject protected ProgressDialogUtil progressDialogUtil;
    protected ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    public THBaseBillingInteractor()
    {
        super();
        DaggerUtils.inject(this);
        requestsToLaunchOnBillingInitialMilestoneComplete = new LinkedList<>();
        uiBillingRequests = new HashMap<>();

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
            cleanRequest(entry.getValue());
        }
        uiBillingRequests.clear();
    }

    protected void cleanRequest(THUIBillingRequestType uiBillingRequest)
    {
        if (uiBillingRequest != null)
        {
            uiBillingRequest.billingAvailableListener = null;
            uiBillingRequest.followResultListener = null;
            uiBillingRequest.productIdentifierFetchedListener = null;
            uiBillingRequest.inventoryFetchedListener = null;
            uiBillingRequest.purchaseFetchedListener = null;
            uiBillingRequest.purchaseRestorerListener = null;
            uiBillingRequest.purchaseReportedListener = null;
            uiBillingRequest.purchaseFinishedListener = null;
            uiBillingRequest.onDefaultErrorListener = null;
        }
    }
    //</editor-fold>

    abstract protected BillingAlertDialogUtil<
        ProductIdentifierType,
            THProductDetailType,
        THBillingLogicHolderType,
        ProductDetailViewType,
        ProductDetailAdapterType> getBillingAlertDialogUtil();

    @Override public void doAction(int action)
    {
        switch (action)
        {
            case ACTION_RESET_PORTFOLIO:
                // TODO
                break;
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        getBillingLogicHolder().onActivityResult(requestCode, resultCode, data);
    }

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

    @Override public void forgetRequestCode(int requestCode)
    {
        THBillingLogicHolderType logicHolder = getBillingLogicHolder();
        if (logicHolder != null)
        {
            logicHolder.forgetRequestCode(requestCode);
        }
        cleanRequest(uiBillingRequests.get(requestCode));
        uiBillingRequests.remove(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override public int run(THUIBillingRequestType uiBillingRequest)
    {
        int requestCode = getUnusedRequestCode();
        uiBillingRequests.put(requestCode, uiBillingRequest);
        if (uiBillingRequest.startWithProgressDialog)
        {
            popInitialProgressDialog(uiBillingRequest);
        }
        requestsToLaunchOnBillingInitialMilestoneComplete.addLast(requestCode);
        THBillingInitialMilestone.launch();
        return requestCode;
    }

    protected void runWaitingRequests()
    {
        while (requestsToLaunchOnBillingInitialMilestoneComplete.size() > 0)
        {
            runRequestCode(requestsToLaunchOnBillingInitialMilestoneComplete.removeFirst());
        }
    }

    protected void runRequestCode(int requestCode)
    {
        THUIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            getBillingLogicHolder().run(requestCode, createBillingRequest(uiBillingRequest));
        }
    }

    protected THBillingRequestType createBillingRequest(THUIBillingRequestType uiBillingRequest)
    {
        THBillingRequestType billingRequest = createEmptyBillingRequest(uiBillingRequest);
        populateBillingRequest(billingRequest, uiBillingRequest);
        return billingRequest;
    }

    abstract protected THBillingRequestType createEmptyBillingRequest(THUIBillingRequestType uiBillingRequest);

    protected void populateBillingRequest(THBillingRequestType request, THUIBillingRequestType uiBillingRequest)
    {
        request.testBillingAvailable = uiBillingRequest.billingAvailable;
        request.billingAvailableListener = createBillingAvailableListener();
        request.fetchProductIdentifiers = uiBillingRequest.fetchProductIdentifiers;
        request.productIdentifierFetchedListener = createProductIdentifierFetchedListener();
        request.fetchInventory = uiBillingRequest.fetchInventory;
        request.inventoryFetchedListener = createInventoryFetchedListener();
        request.fetchPurchase = uiBillingRequest.fetchPurchase;
        request.purchaseFetchedListener = createPurchaseFetchedListener();
        request.purchaseRestorerListener = createPurchaseRestorerFinishedListener();
        request.purchaseFinishedListener = createPurchaseFinishedListener();
        request.purchaseReportedListener = createPurchaseReportedListener();
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
    protected AlertDialog popBuyDialog(int requestCode, ProductIdentifierDomain productIdentifierDomain)
    {
        return popBuyDialog(requestCode, productIdentifierDomain, productIdentifierDomain.storeTitleResId);
    }

    protected AlertDialog popBuyDialog(int requestCode, ProductIdentifierDomain productIdentifierDomain, int titleResId)
    {
        Activity currentActivity = currentActivityHolder.getCurrentActivity();
        if (currentActivity != null)
        {
            if (progressDialog != null)
            {
                progressDialog.dismiss();
            }
            return getBillingAlertDialogUtil().popBuyDialog(
                    requestCode,
                    currentActivity,
                    getBillingLogicHolder(),
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

    protected void populatePurchaseBillingRequest(int requestCode, THBillingRequestType billingRequest, ProductIdentifierType productIdentifier)
    {
        THUIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            billingRequest.doPurchase = true;
            try
            {
                billingRequest.purchaseOrder = createPurchaseOrder(requestCode, productIdentifier);
            }
            catch (BillingException billingException)
            {
                notifyDefaultErrorListener(requestCode, (BillingExceptionType) billingException);
            }
            billingRequest.purchaseFinishedListener = createPurchaseFinishedListener();
            billingRequest.reportPurchase = true;
            billingRequest.purchaseReportedListener = createPurchaseReportedListener();
        }
    }

    protected THPurchaseOrderType createPurchaseOrder(int requestCode, ProductIdentifierType productIdentifier) throws BillingExceptionType
    {
        THPurchaseOrderType purchaseOrder = createEmptyPurchaseOrder(requestCode, productIdentifier);
        populatePurchaseOrder(requestCode, purchaseOrder);
        return purchaseOrder;
    }

    abstract protected THPurchaseOrderType createEmptyPurchaseOrder(int requestCode, ProductIdentifierType productIdentifier) throws BillingExceptionType;

    protected void populatePurchaseOrder(int requestCode, THPurchaseOrderType purchaseOrder)
    {
        THUIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            purchaseOrder.setUserToFollow(uiBillingRequest.userToFollow);
        }
    }
    //</editor-fold>

    protected void notifyDefaultErrorListener(int requestCode, BillingExceptionType billingException)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.onDefaultErrorListener != null)
            {
                billingRequest.onDefaultErrorListener.onError(requestCode, billingException);
            }
        }
    }

    //<editor-fold desc="Billing Available">
    protected BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> createBillingAvailableListener()
    {
        return new THBaseBillingInteractorBillingAvailableListener();
    }

    protected class THBaseBillingInteractorBillingAvailableListener implements BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType>
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
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.startWithProgressDialog && progressDialog != null)
        {
            progressDialog.hide();
        }
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
            else if (billingRequest.onDefaultErrorListener != null)
            {
                billingRequest.onDefaultErrorListener.onError(requestCode, billingException);
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
        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterProductIdentifierFetchedListener(requestCode);
            }
        }

        @Override public void onFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
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

    protected void handleFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
    {
    }

    protected void notifyFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
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
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.startWithProgressDialog && progressDialog != null)
        {
            progressDialog.hide();
        }
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
            else if (billingRequest.onDefaultErrorListener != null)
            {
                billingRequest.onDefaultErrorListener.onError(requestCode, exception);
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
            THProductDetailType,
            BillingExceptionType> createInventoryFetchedListener()
    {
        return new THBaseBillingInteractorOnInventoryFetchedListener();
    }

    protected class THBaseBillingInteractorOnInventoryFetchedListener implements BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            THProductDetailType,
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

        @Override public void onInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, THProductDetailType> inventory)
        {
            Timber.d("Inventory fetched count %d", productIdentifiers.size());
            forgetListener(requestCode);
            handleInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
            notifyInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }

        @Override public void onInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
        {
            Timber.e(exception, "inventory failed");
            forgetListener(requestCode);
            handleInventoryFetchFail(requestCode, productIdentifiers, exception);
            notifyInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }

    protected void handleInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, THProductDetailType> inventory)
    {
        THUIBillingRequestType thuiBillingRequest = uiBillingRequests.get(requestCode);
        if (thuiBillingRequest != null)
        {
            if (thuiBillingRequest.domainToPresent != null)
            {
                getBillingLogicHolder().forgetRequestCode(requestCode);
                popBuyDialog(requestCode, thuiBillingRequest.domainToPresent);
            }
        }
    }

    protected void notifyInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, THProductDetailType> inventory)
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
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.startWithProgressDialog && progressDialog != null)
        {
            progressDialog.hide();
        }
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
            else if (billingRequest.onDefaultErrorListener != null)
            {
                billingRequest.onDefaultErrorListener.onError(requestCode, exception);
            }
        }
        if (billingRequest == null || billingRequest.popIfInventoryFetchFailed)
        {
            popInventoryFetchFail(requestCode, productIdentifiers, exception);
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
        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterPurchaseFetchedListener(requestCode);
            }
        }

        @Override public void onFetchedPurchases(int requestCode, List<THProductPurchaseType> purchases)
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

    protected void handleFetchedPurchases(int requestCode, List<THProductPurchaseType> purchases)
    {
    }

    protected void notifyFetchedPurchases(int requestCode, List<THProductPurchaseType> purchases)
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
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.startWithProgressDialog && progressDialog != null)
        {
            progressDialog.hide();
        }
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
            else if (billingRequest.onDefaultErrorListener != null)
            {
                billingRequest.onDefaultErrorListener.onError(requestCode, exception);
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

    //<editor-fold desc="Purchase Restore">
    protected BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> createPurchaseRestorerFinishedListener()
    {
        return new BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseRestored(int requestCode, List<THProductPurchaseType> restoredPurchases, List<THProductPurchaseType> failedRestorePurchases,
                    List<BillingExceptionType> failExceptions)
            {
                THBillingLogicHolderType logicHolder = getBillingLogicHolder();
                if (logicHolder != null)
                {
                    logicHolder.unregisterPurchaseRestorerListener(requestCode);
                }
                handlePurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
            }
        };
    }

    protected void handlePurchaseRestored(int requestCode, List<THProductPurchaseType> restoredPurchases, List<THProductPurchaseType> failedRestorePurchases, List<BillingExceptionType> failExceptions)
    {
        notifyPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
    }

    protected void notifyPurchaseRestored(int requestCode, List<THProductPurchaseType> restoredPurchases, List<THProductPurchaseType> failedRestorePurchases, List<BillingExceptionType> failExceptions)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseRestorerListener != null)
            {
                billingRequest.purchaseRestorerListener.onPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
            }
        }
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

        @Override public void onPurchaseFinished(int requestCode, THPurchaseOrderType purchaseOrder, THProductPurchaseType purchase)
        {
            forgetListener(requestCode);
            handlePurchaseFinished(requestCode, purchaseOrder, purchase);
            notifyPurchaseFinished(requestCode, purchaseOrder, purchase);
        }

        @Override public void onPurchaseFailed(int requestCode, THPurchaseOrderType purchaseOrder, BillingExceptionType billingException)
        {
            forgetListener(requestCode);
            handlePurchaseFailed(requestCode, purchaseOrder, billingException);
            notifyPurchaseFailed(requestCode, purchaseOrder, billingException);
        }
    }

    protected void handlePurchaseFinished(int requestCode, THPurchaseOrderType purchaseOrder, THProductPurchaseType purchase)
    {
    }

    protected void notifyPurchaseFinished(int requestCode, THPurchaseOrderType purchaseOrder, THProductPurchaseType purchase)
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

    protected void handlePurchaseFailed(int requestCode, THPurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.startWithProgressDialog && progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    protected void notifyPurchaseFailed(int requestCode, THPurchaseOrderType purchaseOrder, BillingExceptionType billingException)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseFinishedListener != null)
            {
                billingRequest.purchaseFinishedListener.onPurchaseFailed(requestCode, purchaseOrder, billingException);
            }
            else if (billingRequest.onDefaultErrorListener != null)
            {
                billingRequest.onDefaultErrorListener.onError(requestCode, billingException);
            }
        }
        if (billingRequest == null || billingRequest.popIfPurchaseFailed)
        {
            popPurchaseFailed(requestCode, purchaseOrder, billingException, createRestoreClickListener(requestCode));
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

    protected AlertDialog.OnClickListener createRestoreClickListener(final int requestCode)
    {
        return new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                flipRequestFromPurchaseToRestore(requestCode);
                runRequestCode(requestCode);
            }
        };
    }

    protected void flipRequestFromPurchaseToRestore(int requestCode)
    {
        THUIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            uiBillingRequest.startWithProgressDialog = true;
            uiBillingRequest.domainToPresent = null;
            uiBillingRequest.restorePurchase = true;
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

        private void forgetListener(int requestCode)
        {
            THBillingLogicHolderType logicHolder = getBillingLogicHolder();
            if (logicHolder != null)
            {
                logicHolder.unregisterPurchaseReportedListener(requestCode);
            }
        }

        @Override public void onPurchaseReported(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
        {
            Timber.d("THBaseBillingInteractor onPurchaseReported " + updatedUserPortfolio);
            forgetListener(requestCode);
            handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
            notifyPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            // Children should continue with the sequence
        }

        @Override public void onPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
        {
            forgetListener(requestCode);
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
            Timber.d("THBaseBillingInteractor notifyPurchaseReported no request " + updatedUserPortfolio);
            if (billingRequest.purchaseReportedListener != null)
            {
                Timber.d("THBaseBillingInteractor notifyPurchaseReported null listener " + updatedUserPortfolio);
                billingRequest.purchaseReportedListener.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }
        }
    }

    protected void handlePurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null && billingRequest.startWithProgressDialog && progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    protected void notifyPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        THUIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.purchaseReportedListener != null)
            {
                billingRequest.purchaseReportedListener.onPurchaseReportFailed(requestCode, reportedPurchase, error);
            }
            else if (billingRequest.onDefaultErrorListener != null)
            {
                billingRequest.onDefaultErrorListener.onError(requestCode, error);
            }
        }
        if (billingRequest == null || billingRequest.popIfReportFailed)
        {
            popPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
    }

    protected AlertDialog popPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        Context currentContext = currentActivityHolder.getCurrentActivity();
        if (currentContext != null)
        {
            return getBillingAlertDialogUtil().popFailedToReport(currentContext);
        }
        return null;
    }
    //</editor-fold>

    protected void popInitialProgressDialog(THUIBillingRequestType billingRequest)
    {
        if (billingRequest.restorePurchase)
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
            progressDialog = progressDialogUtil.show(
                    currentContext,
                    R.string.store_billing_loading_info_window_title,
                    R.string.store_billing_loading_info_window_message);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
    }

    public void popRestorePurchaseProgress()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = progressDialogUtil.show(
                    currentContext,
                    R.string.store_billing_restoring_purchase_window_title,
                    R.string.store_billing_restoring_purchase_window_message);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
    }

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
