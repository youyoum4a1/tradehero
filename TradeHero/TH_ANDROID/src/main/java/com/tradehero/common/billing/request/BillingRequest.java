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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 3/13/14.
 */
public class BillingRequest<
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
     * Indicates whether we want to test if billing is available
     */
    public boolean testBillingAvailable;
    public BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener;

    /**
     * Indicates whether we want to fetch the product identifiers
     */
    public boolean fetchProductIdentifiers;
    public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> productIdentifierFetchedListener;

    /**
     * Indicates whether we want to fetch the product details
     */
    public boolean fetchInventory;
    public List<ProductIdentifierType> productIdentifiersForInventory;
    public BillingInventoryFetcher.OnInventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailType,
                BillingExceptionType> inventoryFetchedListener;

    /**
     * Indicates whether we want to fetch the purchases
     */
    public boolean fetchPurchase;
    public BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseFetchedListener;
    public LinkedList<ProductPurchaseType> fetchedPurchases;

    /**
     * Indicates whether we want to restore purchases
     */
    public boolean restorePurchase;
    public List<ProductPurchaseType> restoredPurchases = new ArrayList<>();
    public List<ProductPurchaseType> restoreFailedPurchases = new ArrayList<>();
    public List<BillingExceptionType> restoreFailedErrors = new ArrayList<>();
    public BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseRestorerListener;

    public boolean doPurchase;
    public PurchaseOrderType purchaseOrder;
    public BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseFinishedListener;

    public BillingRequest()
    {
    }

    public void onDestroy()
    {
        this.billingAvailableListener = null;
        this.productIdentifierFetchedListener = null;
        this.inventoryFetchedListener = null;
        this.purchaseFetchedListener = null;
        this.purchaseFinishedListener = null;
    }
}
