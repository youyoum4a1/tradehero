package com.tradehero.th.billing;

import android.app.AlertDialog;
import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.OnBillingAvailableListener;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import javax.inject.Inject;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class THBaseBillingInteractor<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<
                ProductIdentifierType,
                OrderIdType>,
        BillingLogicHolderType extends BillingLogicHolder<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingInteractor<
        ProductIdentifierType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingLogicHolderType,
        BillingExceptionType>
{
    protected OnPurchaseVirtualDollarListener purchaseVirtualDollarListener;

    @Inject BillingAlertDialogUtil billingAlertDialogUtil;
    @Inject CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    public THBaseBillingInteractor()
    {
        super();
    }
    //</editor-fold>

    //<editor-fold desc="Billing Available">
    @Override public Boolean isBillingAvailable()
    {
        BillingLogicHolderType billingActorCopy = this.getBillingLogicHolder();
        return billingActorCopy == null ? null : billingActorCopy.isBillingAvailable();
    }

    @Override public AlertDialog conditionalPopBillingNotAvailable()
    {
        Boolean billingAvailable = isBillingAvailable();
        if (billingAvailable == null || !billingAvailable) // TODO wait when is null
        {
            return popBillingUnavailable();
        }
        return null;
    }

    protected void postPopBillingUnavailable()
    {
        currentActivityHolder.getCurrentHandler().post(new Runnable()
        {
            @Override public void run()
            {
                popBillingUnavailable();
            }
        });
    }

    @Override public AlertDialog popBillingUnavailable()
    {
        return billingAlertDialogUtil.popBillingUnavailable(
                currentActivityHolder.getCurrentActivity(),
                getBillingLogicHolder().getBillingHolderName(
                        currentActivityHolder.getCurrentActivity().getResources()));
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Virtual Dollars">
    abstract public void purchaseVirtualDollar(OwnedPortfolioId ownedPortfolioId);

    public void setPurchaseVirtualDollarListener(OnPurchaseVirtualDollarListener purchaseVirtualDollarListener)
    {
        this.purchaseVirtualDollarListener = purchaseVirtualDollarListener;
    }

    protected OnBillingAvailableListener<BillingExceptionType> createPurchaseVirtualDollarWhenAvailableListener(OwnedPortfolioId ownedPortfolioId)
    {
        return new THBaseBillingInteractorPurchaseVirtualDollarWhenAvailableListener(ownedPortfolioId);
    }

    protected class THBaseBillingInteractorPurchaseVirtualDollarWhenAvailableListener implements OnBillingAvailableListener<BillingExceptionType>
    {
        protected OwnedPortfolioId applicablePortfolioId;

        public THBaseBillingInteractorPurchaseVirtualDollarWhenAvailableListener(OwnedPortfolioId portfolioId)
        {
            super();
            this.applicablePortfolioId = portfolioId;
        }

        @Override public void onBillingAvailable()
        {
            // TODO
        }

        @Override public void onBillingNotAvailable(BillingExceptionType billingException)
        {
            OnPurchaseVirtualDollarListener listenerCopy = purchaseVirtualDollarListener;
            if (listenerCopy != null)
            {
                listenerCopy.onPurchasedVirtualDollarFailed(applicablePortfolioId, billingException);
            }
        }
    }
    //</editor-fold>
}
