package com.tradehero.th.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingLogicHolder;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.BillingInventoryFetcherHolder;
import com.tradehero.common.billing.BillingPurchaseFetcherHolder;
import com.tradehero.common.billing.BillingPurchaserHolder;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.ProductIdentifierListCacheRx;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.request.THBillingRequest;

abstract public class THBaseBillingLogicHolder<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THProductTunerType extends THProductDetailTuner<ProductIdentifierType, THProductDetailType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingRequestType extends THBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    extends
        BaseBillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THProductTunerType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>
    implements
        THBillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>
{
    @NonNull protected final THPurchaseReporterHolder<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType>
            purchaseReporterHolder;

    //<editor-fold desc="Constructors">
    public THBaseBillingLogicHolder(
            @NonNull ProductIdentifierListCacheRx<ProductIdentifierType, ProductIdentifierListKeyType, ProductIdentifierListType> productIdentifierCache,
            @NonNull ProductDetailCacheRx<ProductIdentifierType, THProductDetailType, THProductTunerType> productDetailCache,
            @NonNull BillingAvailableTesterHolder<BillingExceptionType> billingAvailableTesterHolder,
            @NonNull ProductIdentifierFetcherHolder<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetcherHolder,
            @NonNull BillingInventoryFetcherHolder<ProductIdentifierType, THProductDetailType, BillingExceptionType> inventoryFetcherHolder,
            @NonNull BillingPurchaseFetcherHolder<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> purchaseFetcherHolder,
            @NonNull BillingPurchaserHolder<ProductIdentifierType, THPurchaseOrderType, THOrderIdType, THProductPurchaseType, BillingExceptionType> purchaserHolder,
            @NonNull THPurchaseReporterHolder<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> purchaseReporterHolder)
    {
        super(
                productIdentifierCache,
                productDetailCache,
                billingAvailableTesterHolder,
                productIdentifierFetcherHolder,
                inventoryFetcherHolder,
                purchaseFetcherHolder,
                purchaserHolder);
        this.purchaseReporterHolder = purchaseReporterHolder;
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    @Override public void onDestroy()
    {
        purchaseReporterHolder.onDestroy();
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Request Code Management">
    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                purchaseReporterHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        unregisterPurchaseReportedListener(requestCode);

        billingRequests.remove(requestCode);
    }
    //</editor-fold>

    @Override public void registerListeners(int requestCode, @NonNull BillingRequestType billingRequest)
    {
        super.registerListeners(requestCode, billingRequest);
        registerPurchaseReportedListener(requestCode, billingRequest.purchaseReportedListener);
    }

    //<editor-fold desc="Run Logic">
    @Override protected boolean runInternal(int requestCode)
    {
        boolean launched = super.runInternal(requestCode);
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (!launched && billingRequest != null)
        {
            if(billingRequest.reportPurchase && billingRequest.purchaseToReport != null)
            {
                launchReportSequence(requestCode, billingRequest.purchaseToReport);
                launched = true;
            }
        }
        return launched;
    }

    @Override protected boolean prepareToRestoreOnePurchase(int requestCode, BillingRequestType billingRequest)
    {
        boolean prepared = false;
        if (billingRequest != null && billingRequest.fetchedPurchases != null && billingRequest.fetchedPurchases.size() > 0)
        {
            billingRequest.reportPurchase = true;
            billingRequest.purchaseToReport = billingRequest.fetchedPurchases.removeFirst();
            prepared = true;
        }
        return prepared;
    }
    //</editor-fold>

    //<editor-fold desc="Sequence Logic">
    @Override protected void prepareRequestForNextRunAfterPurchaseFinished(int requestCode, THPurchaseOrderType purchaseOrder, THProductPurchaseType purchase)
    {
        super.prepareRequestForNextRunAfterPurchaseFinished(requestCode, purchaseOrder, purchase);
        // In case it was meant to follow a user
        purchase.setUserToFollow(purchaseOrder.getUserToFollow());
    }

    protected void handlePurchaseReportedSuccess(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        notifyPurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
        prepareRequestForNextRunAfterPurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseReportedSuccess(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.reportPurchase = false;
        }
    }

    protected void handlePurchaseReportedFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        notifyPurchaseReportedFailed(requestCode, reportedPurchase, error);
        prepareRequestForNextRunAfterPurchaseReportedFailed(requestCode, reportedPurchase, error);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseReportedFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.reportPurchase = false;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Report Purchase">
    @Override public THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> getPurchaseReportedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.purchaseReportedListener;
    }

    @Override public void registerPurchaseReportedListener(int requestCode, THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> purchaseReportedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseReportedListener = purchaseReportedListener;
            purchaseReporterHolder.registerPurchaseReportedListener(requestCode, createPurchaseReportedListener());
        }
    }

    protected THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> createPurchaseReportedListener()
    {
        return new THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseReported(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                handlePurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
            }

            @Override public void onPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
            {
                handlePurchaseReportedFailed(requestCode, reportedPurchase, error);
            }
        };
    }

    @Override public void unregisterPurchaseReportedListener(int requestCode)
    {
        purchaseReporterHolder.forgetRequestCode(requestCode);
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseReportedListener = null;
        }
    }

    protected void notifyPurchaseReportedSuccess(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> purchaseReportedListener = getPurchaseReportedListener(requestCode);
        if (purchaseReportedListener != null)
        {
            purchaseReportedListener.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
        }
        unregisterPurchaseReportedListener(requestCode);
    }

    protected void notifyPurchaseReportedFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> purchaseReportedListener = getPurchaseReportedListener(requestCode);
        if (purchaseReportedListener != null)
        {
            purchaseReportedListener.onPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
        unregisterPurchaseReportedListener(requestCode);
    }

    @Override public void launchReportSequence(int requestCode, THProductPurchaseType purchase)
    {
        purchaseReporterHolder.launchReportSequence(requestCode, purchase);
    }
    //</editor-fold>
}
