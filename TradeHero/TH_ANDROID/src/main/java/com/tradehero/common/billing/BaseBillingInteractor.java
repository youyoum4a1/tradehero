package com.tradehero.common.billing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

abstract public class BaseBillingInteractor<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<
                ProductIdentifierType,
                OrderIdType>,
        BillingLogicHolderType extends BillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>,
        BillingRequestType extends BillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        UIBillingRequestType extends UIBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingInteractor<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingLogicHolderType,
        BillingRequestType,
        UIBillingRequestType,
        BillingExceptionType>
{
    public static final int MAX_RANDOM_RETRIES = 50;

    @NotNull protected final BillingLogicHolderType billingLogicHolder;
    @NotNull protected final Map<Integer, UIBillingRequestType> uiBillingRequests;
    @Nullable protected ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    public BaseBillingInteractor(@NotNull BillingLogicHolderType billingLogicHolder)
    {
        super();
        this.billingLogicHolder = billingLogicHolder;
        uiBillingRequests = new HashMap<>();
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    public void onDestroy()
    {
        dismissProgressDialog();
        for (Map.Entry<Integer, UIBillingRequestType> entry : uiBillingRequests.entrySet())
        {
            billingLogicHolder.forgetRequestCode(entry.getKey());
            entry.getValue().onDestroy();
        }
        uiBillingRequests.clear();
    }
    //</editor-fold>

    protected void dismissProgressDialog()
    {
        ProgressDialog progressDialogCopy = progressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.dismiss();
        }
        progressDialog = null;
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
        return billingLogicHolder.isUnusedRequestCode(requestCode)
                && !uiBillingRequests.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        billingLogicHolder.forgetRequestCode(requestCode);
        UIBillingRequestType thuiBillingRequest = uiBillingRequests.get(requestCode);
        if (thuiBillingRequest != null)
        {
            thuiBillingRequest.onDestroy();
        }
        uiBillingRequests.remove(requestCode);
    }
    //</editor-fold>

    //<editor-fold desc="Request Handling">
    @Override public int run(@NotNull UIBillingRequestType uiBillingRequest)
    {
        int requestCode = getUnusedRequestCode();
        uiBillingRequests.put(requestCode, uiBillingRequest);
        return requestCode;
    }

    protected void runRequestCode(int requestCode)
    {
        UIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            billingLogicHolder.run(requestCode, createBillingRequest(uiBillingRequest));
        }
    }

    abstract protected BillingRequestType createBillingRequest(
            @NotNull UIBillingRequestType uiBillingRequest);
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        billingLogicHolder.onActivityResult(requestCode, resultCode, data);
    }

    //<editor-fold desc="Billing Available">
    protected void notifyBillingAvailable(int requestCode)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener = billingRequest.getBillingAvailableListener();
            if (billingAvailableListener != null)
            {
                billingAvailableListener.onBillingAvailable(requestCode);
            }
        }
    }

    protected void notifyBillingNotAvailable(int requestCode, BillingExceptionType billingException)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener = billingRequest.getBillingAvailableListener();
            if (billingAvailableListener != null)
            {
                billingAvailableListener.onBillingNotAvailable(requestCode, billingException);
            }
        }
        if (billingRequest == null || billingRequest.getPopIfBillingNotAvailable())
        {
            popBillingUnavailable(billingException);
        }
    }

    abstract protected AlertDialog popBillingUnavailable(BillingExceptionType billingException);
    //</editor-fold>

    //<editor-fold desc="Product Identifier Fetch">
    protected void notifyFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    BillingExceptionType> productIdentifierFetchedListener = billingRequest.getProductIdentifierFetchedListener();
            if (productIdentifierFetchedListener != null)
            {
                productIdentifierFetchedListener.onFetchedProductIdentifiers(requestCode, availableProductIdentifiers);
            }
        }
    }

    protected void notifyFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    BillingExceptionType> productIdentifierFetchedListener = billingRequest.getProductIdentifierFetchedListener();
            if (productIdentifierFetchedListener != null)
            {
                productIdentifierFetchedListener.onFetchProductIdentifiersFailed(requestCode, exception);
            }
        }
        if (billingRequest == null || billingRequest.getPopIfProductIdentifierFetchFailed())
        {
            popFetchProductIdentifiersFailed(requestCode, exception);
        }
    }

    abstract protected AlertDialog popFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception);
    //</editor-fold>

    //<editor-fold desc="Inventory Fetch">
    protected void notifyInventoryFetchSuccess(
            int requestCode,
            List<ProductIdentifierType> productIdentifiers,
            Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        UIBillingRequestType thuiBillingRequest = uiBillingRequests.get(requestCode);
        if (thuiBillingRequest != null)
        {
            BillingInventoryFetcher.OnInventoryFetchedListener<
                    ProductIdentifierType,
                    ProductDetailType,
                    BillingExceptionType> inventoryFetchedListener = thuiBillingRequest.getInventoryFetchedListener();
            if (inventoryFetchedListener != null)
            {
                inventoryFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
            }
        }
    }

    protected void notifyInventoryFetchFail(
            int requestCode,
            List<ProductIdentifierType> productIdentifiers,
            BillingExceptionType exception)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            BillingInventoryFetcher.OnInventoryFetchedListener<
                    ProductIdentifierType,
                    ProductDetailType,
                    BillingExceptionType> inventoryFetchedListener = billingRequest.getInventoryFetchedListener();
            if (inventoryFetchedListener != null)
            {
                inventoryFetchedListener.onInventoryFetchFail(requestCode, productIdentifiers, exception);
            }
        }
        if (billingRequest == null || billingRequest.getPopIfInventoryFetchFailed())
        {
            popInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }

    abstract protected AlertDialog popInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception);

    protected BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> createInventoryFetchedListener()
    {
        return new BaseBillingInteractorOnInventoryFetchListenerWrapper();
    }

    protected class BaseBillingInteractorOnInventoryFetchListenerWrapper implements BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType>
    {
        @Override public void onInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers,
                Map<ProductIdentifierType, ProductDetailType> inventory)
        {
            notifyInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }

        @Override public void onInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
        {
            notifyInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Purchases Fetch">
    protected void notifyFetchedPurchases(int requestCode, List<ProductPurchaseType> purchases)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            BillingPurchaseFetcher.OnPurchaseFetchedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFetchedListener = billingRequest.getPurchaseFetchedListener();
            if (purchaseFetchedListener != null)
            {
                purchaseFetchedListener.onFetchedPurchases(requestCode, purchases);
            }
        }
    }

    protected void notifyFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            BillingPurchaseFetcher.OnPurchaseFetchedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFetchedListener = billingRequest.getPurchaseFetchedListener();
            if (purchaseFetchedListener != null)
            {
                purchaseFetchedListener.onFetchPurchasesFailed(requestCode, exception);
            }
        }
        if (billingRequest == null || billingRequest.getPopIfPurchaseFetchFailed())
        {
            popFetchPurchasesFailed(requestCode, exception);
        }
    }

    abstract protected AlertDialog popFetchPurchasesFailed(int requestCode, BillingExceptionType exception);
    //</editor-fold>

    //<editor-fold desc="Purchases Restore">
    protected void notifyPurchaseRestored(
            int requestCode,
            List<ProductPurchaseType> restoredPurchases,
            List<ProductPurchaseType> failedRestorePurchases,
            List<BillingExceptionType> failExceptions)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        Timber.d("%s", billingRequest);
        if (billingRequest != null)
        {
            BillingPurchaseRestorer.OnPurchaseRestorerListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseRestorerListener = billingRequest.getPurchaseRestorerListener();
            Timber.d("%s", purchaseRestorerListener);
            if (purchaseRestorerListener != null)
            {
                purchaseRestorerListener.onPurchaseRestored(requestCode, restoredPurchases, failedRestorePurchases, failExceptions);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Purchase">
    protected void notifyPurchaseFinished(
            int requestCode,
            PurchaseOrderType purchaseOrder,
            ProductPurchaseType purchase)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            BillingPurchaser.OnPurchaseFinishedListener<
                    ProductIdentifierType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFinishedListener = billingRequest.getPurchaseFinishedListener();
            if (purchaseFinishedListener != null)
            {
                purchaseFinishedListener.onPurchaseFinished(requestCode, purchaseOrder, purchase);
            }
        }
    }

    protected void notifyPurchaseFailed(
            int requestCode,
            PurchaseOrderType purchaseOrder,
            BillingExceptionType billingException)
    {
        UIBillingRequestType billingRequest = uiBillingRequests.get(requestCode);
        if (billingRequest != null)
        {
            BillingPurchaser.OnPurchaseFinishedListener<
                    ProductIdentifierType,
                    PurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFinishedListener = billingRequest.getPurchaseFinishedListener();
            if (purchaseFinishedListener != null)
            {
                Timber.d("notify purchase Finished");
                purchaseFinishedListener.onPurchaseFailed(requestCode, purchaseOrder, billingException);
            }
        }
        if (billingRequest == null || billingRequest.getPopIfPurchaseFailed())
        {
            Timber.d("calling popAlert");
            popPurchaseFailed(requestCode, purchaseOrder, billingException, createRestoreClickListener(requestCode));
        }
    }

    abstract protected AlertDialog popPurchaseFailed(
            int requestCode,
            PurchaseOrderType purchaseOrder,
            BillingExceptionType billingException,
            AlertDialog.OnClickListener restoreClickListener);

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
        UIBillingRequestType uiBillingRequest = uiBillingRequests.get(requestCode);
        if (uiBillingRequest != null)
        {
            uiBillingRequest.setRestorePurchase(true);
        }
    }
    //</editor-fold>
}
