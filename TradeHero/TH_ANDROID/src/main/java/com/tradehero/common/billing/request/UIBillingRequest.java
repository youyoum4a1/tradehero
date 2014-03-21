package com.tradehero.common.billing.request;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xavier on 3/18/14.
 */
public class UIBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    /**
     * The portfolio to pass when making a purchase
     */
    public OwnedPortfolioId applicablePortfolioId;

    /**
     * Indicates whether we want a progress dialog to show while stuff is being prepared.
     */
    public boolean startWithProgressDialog;

    /**
     * When a listener is missing when an error occurs, the error should be sent to this listener.
     */
    public OnErrorListener<BillingExceptionType> onDefaultErrorListener;

    /**
     * Indicates whether we want to test if billing is available
     */
    public boolean billingAvailable;
    /**
     * Indicates whether we want the Interactor to pop a dialog when billing is not available
     */
    public boolean popIfBillingNotAvailable;
    public BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener;

    /**
     * Indicates whether we want to fetch the product identifiers
     */
    public boolean fetchProductIdentifiers;
    /**
     * Indicates whether we want the Interactor to pop a dialog when the product identifier fetch has failed
     */
    public boolean popIfProductIdentifierFetchFailed;
    public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> productIdentifierFetchedListener;

    /**
     * Indicates whether we want to fetch the product details
     */
    public boolean fetchInventory;
    /**
     * Indicates whether we want the Interactor to pop a dialog when the inventory fetch has failed
     */
    public boolean popIfInventoryFetchFailed;
    public BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> inventoryFetchedListener;

    /**
     * Indicates whether we want to fetch the purchases
     */
    public boolean fetchPurchase;
    /**
     * Indicates whether we want the Interactor to pop a dialog when the fetch of purchases has failed
     */
    public boolean popIfPurchaseFetchFailed;
    public BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener;

    /**
     * Indicates whether we want to restore purchases
     */
    public boolean restorePurchase;
    /**
     * Indicates whether we want the Interactor to pop an outcome dialog upon restore.
     */
    public boolean popRestorePurchaseOutcome;
    /**
     * Indicates whether we want the Interactor to pop an outcome dialog upon restore, even if nothing to report.
     */
    public boolean popRestorePurchaseOutcomeVerbose;
    public BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseRestorerListener;

    /**
     * Indicates whether we want the Interactor to pop a dialog when the purchase has failed
     */
    public boolean popIfPurchaseFailed;
    public BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFinishedListener;

    public UIBillingRequest()
    {
        super();
    }

    public static interface OnErrorListener<BillingExceptionType extends BillingException>
    {
        void onError(int requestCode, BillingExceptionType billingException);
    }
}
