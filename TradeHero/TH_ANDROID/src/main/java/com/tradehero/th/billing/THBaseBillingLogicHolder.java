package com.tradehero.th.billing;

import com.tradehero.common.billing.BaseBillingLogicHolder;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;

/**
 * Created by xavier on 3/14/14.
 */
abstract public class THBaseBillingLogicHolder<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingRequestType extends THBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    extends
        BaseBillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingRequestType,
                BillingExceptionType>
    implements
        THBillingLogicHolder<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
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
    protected void handlePurchaseReportedSuccess(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        if (updatedUserPortfolio != null)
        {
            userProfileCache.put(updatedUserPortfolio.getBaseKey(), updatedUserPortfolio);
        }
        notifyPurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
        prepareRequestForNextRunAfterPurchaseReportedSuccess(requestCode, reportedPurchase, updatedUserPortfolio);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseReportedSuccess(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.reportPurchase = false;
        }
    }

    protected void handlePurchaseReportedFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        notifyPurchaseReportedFailed(requestCode, reportedPurchase, error);
        prepareRequestForNextRunAfterPurchaseReportedFailed(requestCode, reportedPurchase, error);
        runInternal(requestCode);
    }

    protected void prepareRequestForNextRunAfterPurchaseReportedFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        BillingRequestType billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.reportPurchase = false;
        }
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
