package com.tradehero.th.billing;

import com.tradehero.common.billing.BaseBillingLogicHolder;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;

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

    public THBaseBillingLogicHolder()
    {
        super();
        purchaseReporterHolder = createPurchaseReporterHolder();
    }

    protected abstract PurchaseReporterHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> createPurchaseReporterHolder();

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
    }
    //</editor-fold>

    //<editor-fold desc="Report Purchase">
    @Override public PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseReportedListener(int requestCode)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest == null)
        {
            return null;
        }
        return billingRequest.getPurchaseReportedListener();
    }

    @Override public void registerPurchaseReportedListener(int requestCode, PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseReportedListener)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.setPurchaseReportedListener(purchaseReportedListener);
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
            billingRequest.setPurchaseReportedListener(null);
        }
    }

    protected void handlePurchaseReportedSuccess(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        notifyPurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
        // TODO further action?
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
