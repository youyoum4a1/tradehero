package com.tradehero.th.billing;

import com.tradehero.common.billing.BaseBillingLogicHolder;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;

/**
 * Created by xavier on 3/14/14.
 */
abstract public class THBaseBillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingRequestType extends THBillingRequest<
                        ProductIdentifierType,
                        ProductDetailType,
                        PurchaseOrderType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType>,
        BillingExceptionType extends BillingException>
    extends
        BaseBillingLogicHolder<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>
    implements
        THBillingLogicHolder<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>
{
    protected PurchaseReporterHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReporterHolder;

    protected UserProfileCache userProfileCache;
    protected UserServiceWrapper userServiceWrapper;

    public THBaseBillingLogicHolder(UserProfileCache userProfileCache, UserServiceWrapper userServiceWrapper)
    {
        super();
        this.userProfileCache = userProfileCache;
        this.userServiceWrapper = userServiceWrapper;
        purchaseReporterHolder = createPurchaseReporterHolder();
    }

    @Override public void onDestroy()
    {
        purchaseReporterHolder.onDestroy();
        super.onDestroy();
    }

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

    //<editor-fold desc="Sequence Logic">
    @Override public boolean run(int requestCode, BillingRequestType billingRequest)
    {
        boolean launched = super.run(requestCode, billingRequest);
        // TODO other stuff
        if (!launched && billingRequest != null)
        {
            if (billingRequest.fetchProductIdentifiers)
            {
                launchProductIdentifierFetchSequence(requestCode);
                launched = true;
            }
            else if (billingRequest.fetchInventory && billingRequest.productIdentifiersForInventory != null)
            {
                launchInventoryFetchSequence(requestCode, billingRequest.productIdentifiersForInventory);
                launched = true;
            }
            else if (billingRequest.fetchPurchase)
            {
                launchFetchPurchaseSequence(requestCode);
                launched = true;
            }
            else if (billingRequest.purchaseOrder != null)
            {
                launchPurchaseSequence(requestCode, billingRequest.purchaseOrder);
                launched = true;
            }
            else if (billingRequest.purchaseToReport != null)
            {
                launchReportSequence(requestCode, billingRequest.purchaseToReport);
                launched = true;
            }
        }
        return launched;
    }
    //</editor-fold>

    //<editor-fold desc="Holder Creation">
    protected abstract PurchaseReporterHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaseReporterHolder();
    //</editor-fold>

    //<editor-fold desc="Report Purchase">
    @Override public PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseReportedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.purchaseReportedListener;
    }

    @Override public void registerPurchaseReportedListener(int requestCode, PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseReportedListener = purchaseReportedListener;
            purchaseReporterHolder.registerPurchaseReportedListener(requestCode, createPurchaseReportedListener());
        }
    }

    protected PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaseReportedListener()
    {
        return new PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseReported(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                handlePurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
            }

            @Override public void onPurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
            {
                notifyPurchaseReportedFailed(requestCode, reportedPurchase, error);
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

    protected void handlePurchaseReportedSuccess(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        if (updatedUserPortfolio != null)
        {
            userProfileCache.put(updatedUserPortfolio.getBaseKey(), updatedUserPortfolio);
        }
        notifyPurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
        // Sequence logic handled by child class
    }

    protected void notifyPurchaseReportedSuccess(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener = getPurchaseReportedListener(requestCode);
        if (purchaseReportedListener != null)
        {
            purchaseReportedListener.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
        }
        unregisterPurchaseReportedListener(requestCode);
    }

    protected void notifyPurchaseReportedFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener = getPurchaseReportedListener(requestCode);
        if (purchaseReportedListener != null)
        {
            purchaseReportedListener.onPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
        unregisterPurchaseReportedListener(requestCode);
    }

    @Override public void launchReportSequence(int requestCode, ProductPurchaseType purchase)
    {
        purchaseReporterHolder.launchReportSequence(requestCode, purchase);
    }

    @Override public UserProfileDTO launchReportSequenceSync(ProductPurchaseType purchase) throws BillingExceptionType
    {
        return purchaseReporterHolder.launchReportSequenceSync(purchase);
    }
    //</editor-fold>
}
