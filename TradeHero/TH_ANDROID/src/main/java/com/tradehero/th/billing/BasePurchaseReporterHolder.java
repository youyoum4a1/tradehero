package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BasePurchaseReporterHolder<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        PurchaseReporterType extends PurchaseReporter<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>,
        OnPurchaseReportedListenerType extends PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements PurchaseReporterHolder<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType,
        OnPurchaseReportedListenerType,
        BillingExceptionType>
{
    public static final String TAG = BasePurchaseReporterHolder.class.getSimpleName();

    protected Map<Integer /*requestCode*/, PurchaseReporterType> purchaseReporters;
    protected Map<Integer /*requestCode*/, PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>> purchaseReportedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<OnPurchaseReportedListenerType>> parentPurchaseReportedHandlers;

    public BasePurchaseReporterHolder()
    {
        super();

        purchaseReporters = new HashMap<>();
        purchaseReportedListeners = new HashMap<>();
        parentPurchaseReportedHandlers = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !purchaseReporters.containsKey(requestCode) &&
                !purchaseReportedListeners.containsKey(requestCode) &&
                !parentPurchaseReportedHandlers.containsKey(requestCode);
    }

    @Override public void unregisterPurchaseReportedListener(int requestCode)
    {
        purchaseReporters.remove(requestCode);
        purchaseReportedListeners.remove(requestCode);
        parentPurchaseReportedHandlers.remove(requestCode);
    }

    @Override public OnPurchaseReportedListenerType getPurchaseReportListener(int requestCode)
    {
        WeakReference<OnPurchaseReportedListenerType> weakHandler = parentPurchaseReportedHandlers.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
    }

    @Override public void registerPurchaseReportedListener(int requestCode, OnPurchaseReportedListenerType purchaseReportedHandler)
    {
        parentPurchaseReportedHandlers.put(requestCode, new WeakReference<>(purchaseReportedHandler));
    }

    @Override public void launchReportSequence(int requestCode, ProductPurchaseType purchase)
    {
        PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> reportedListener = new PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseReported(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                handlePurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }

            @Override public void onPurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
            {
                handlePurchaseReportFailed(requestCode, reportedPurchase, error);
            }
        };
        purchaseReportedListeners.put(requestCode, reportedListener);
        PurchaseReporterType purchaseReporter = createPurchaseReporter();
        purchaseReporter.setPurchaseReporterListener(reportedListener);
        purchaseReporters.put(requestCode, purchaseReporter);
        purchaseReporter.reportPurchase(requestCode, purchase);
    }

    @Override public UserProfileDTO launchReportSequenceSync(ProductPurchaseType purchase) throws BillingExceptionType
    {
        return createPurchaseReporter().reportPurchaseSync(purchase);
    }

    protected void handlePurchaseReported(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        THLog.d(TAG, "handlePurchaseReported Purchase info " + reportedPurchase);

        if (updatedUserPortfolio != null)
        {
            getUserProfileCache().put(updatedUserPortfolio.getBaseKey(), updatedUserPortfolio);
        }

        OwnedPortfolioId applicablePortfolioId = reportedPurchase.getApplicableOwnedPortfolioId();
        if (applicablePortfolioId != null)
        {
            getPortfolioCompactListCache().invalidate(applicablePortfolioId.getUserBaseKey());
            getPortfolioCache().invalidate(applicablePortfolioId);
        }

        OnPurchaseReportedListenerType handler = getPurchaseReportListener(requestCode);
        if (handler != null)
        {
            THLog.d(TAG, "handlePurchaseReported passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReported No PurchaseReportedHandler for requestCode " + requestCode);
        }
    }

    abstract protected UserProfileCache getUserProfileCache();
    abstract protected PortfolioCompactListCache getPortfolioCompactListCache();
    abstract protected PortfolioCache getPortfolioCache();

    protected void handlePurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        THLog.e(TAG, "handlePurchaseReportFailed There was an exception during the report", error);
        OnPurchaseReportedListenerType handler = getPurchaseReportListener(requestCode);
        if (handler != null)
        {
            THLog.d(TAG, "handlePurchaseReportFailed passing on the exception for requestCode " + requestCode);
            handler.onPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReportFailed No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

    @Override public void onDestroy()
    {
        for (PurchaseReporterType purchaseReporter: purchaseReporters.values())
        {
            if (purchaseReporter != null)
            {
                purchaseReporter.setPurchaseReporterListener(null);
            }
        }
        purchaseReporters.clear();
        purchaseReportedListeners.clear();
        parentPurchaseReportedHandlers.clear();
    }

    abstract protected PurchaseReporterType createPurchaseReporter();
}
