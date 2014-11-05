package com.tradehero.th.billing.request;

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
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BaseUIBillingRequest;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.THPurchaseReporter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract public class BaseTHUIBillingRequest<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends BaseUIBillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        THPurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
        implements THUIBillingRequest<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        ProductDetailType,
        THPurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    //<editor-fold desc="Product Identifiers To Present">
    /**
     * The domain of product identifiers to present to the user.
     */
    private ProductIdentifierDomain domainToPresent;

    @Override public ProductIdentifierDomain getDomainToPresent()
    {
        return domainToPresent;
    }

    @Override public void setDomainToPresent(ProductIdentifierDomain domainToPresent)
    {
        this.domainToPresent = domainToPresent;
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Reporting">
    private boolean reportPurchase;

    @Override public boolean getReportPurchase()
    {
        return reportPurchase;
    }

    @Override public void setReportPurchase(boolean reportPurchase)
    {
        this.reportPurchase = reportPurchase;
    }

    /**
     * The portfolio to pass when making a purchase
     */
    @NonNull private final OwnedPortfolioId applicablePortfolioId; // TODO Move out

    @Override @NonNull public OwnedPortfolioId getApplicablePortfolioId()
    {
        return applicablePortfolioId;
    }

    /**
     * Indicates whether we want the Interactor to pop a dialog when reporting fails
     */
    public final boolean popIfReportFailed;

    @Override public boolean getPopIfReportFailed()
    {
        return popIfReportFailed;
    }

    @Nullable private THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseReportedListener;

    @Nullable @Override public THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseReportedListener()
    {
        return purchaseReportedListener;
    }

    @Override public void setPurchaseReportedListener(
            @Nullable THPurchaseReporter.OnPurchaseReportedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseReportedListener)
    {
        this.purchaseReportedListener = purchaseReportedListener;
    }
    //</editor-fold>

    //<editor-fold desc="User Following">
    private UserBaseKey userToPremiumFollow;

    @Override public UserBaseKey getUserToPremiumFollow()
    {
        return userToPremiumFollow;
    }

    @Override public void setUserToPremiumFollow(UserBaseKey userToPremiumFollow)
    {
        this.userToPremiumFollow = userToPremiumFollow;
    }
    //</editor-fold>

    //<editor-fold desc="Constructors">
    protected BaseTHUIBillingRequest(
            @NonNull BaseTHUIBillingRequest.Builder<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    ProductDetailType,
                    THPurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType,
                    ?> builder)
    {
        super(builder);

        this.domainToPresent = builder.domainToPresent;

        this.reportPurchase = builder.reportPurchase;
        this.applicablePortfolioId = builder.applicablePortfolioId;
        this.popIfReportFailed = builder.popIfReportFailed;
        this.purchaseReportedListener = builder.purchaseReportedListener;

        this.userToPremiumFollow = builder.userToPremiumFollow;
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        purchaseReportedListener = null;
        super.onDestroy();
    }

    @Override abstract public THBillingRequest.Builder<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductDetailType,
            THPurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType,
            ?> createEmptyBillingRequestBuilder();

    //<editor-fold desc="Builder">
    public static abstract class Builder<
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
            ProductDetailType extends ProductDetail<ProductIdentifierType>,
            THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException,
            BuilderType extends Builder<
                    ProductIdentifierListKeyType,
                    ProductIdentifierType,
                    ProductIdentifierListType,
                    ProductDetailType,
                    THPurchaseOrderType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType,
                    BuilderType>>
            extends BaseUIBillingRequest.Builder<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductDetailType,
            THPurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType,
            BuilderType>
    {
        //<editor-fold desc="Just so the builder model works as intended, since users only know up to this class">
        @Override public BuilderType startWithProgressDialog(boolean startWithProgressDialog)
        {
            return super.startWithProgressDialog(startWithProgressDialog);
        }

        @Override public BuilderType testBillingAvailable(boolean testBillingAvailable)
        {
            return super.testBillingAvailable(testBillingAvailable);
        }

        @Override public BuilderType popIfBillingNotAvailable(boolean popIfBillingNotAvailable)
        {
            return super.popIfBillingNotAvailable(popIfBillingNotAvailable);
        }

        @Override public BuilderType billingAvailableListener(
                BillingAvailableTester.OnBillingAvailableListener<BillingExceptionType> billingAvailableListener)
        {
            return super.billingAvailableListener(billingAvailableListener);
        }

        @Override public BuilderType fetchProductIdentifiers(boolean fetchProductIdentifiers)
        {
            return super.fetchProductIdentifiers(fetchProductIdentifiers);
        }

        @Override public BuilderType popIfProductIdentifierFetchFailed(boolean popIfProductIdentifierFetchFailed)
        {
            return super.popIfProductIdentifierFetchFailed(popIfProductIdentifierFetchFailed);
        }

        @Override public BuilderType productIdentifierFetchedListener(@Nullable
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetchedListener)
        {
            return super.productIdentifierFetchedListener(productIdentifierFetchedListener);
        }

        @Override public BuilderType fetchInventory(boolean fetchInventory)
        {
            return super.fetchInventory(fetchInventory);
        }

        @Override public BuilderType popIfInventoryFetchFailed(boolean popIfInventoryFetchFailed)
        {
            return super.popIfInventoryFetchFailed(popIfInventoryFetchFailed);
        }

        @Override public BuilderType inventoryFetchedListener(@Nullable
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener)
        {
            return super.inventoryFetchedListener(inventoryFetchedListener);
        }

        @Override public BuilderType fetchPurchases(boolean fetchPurchases)
        {
            return super.fetchPurchases(fetchPurchases);
        }

        @Override public BuilderType popIfPurchaseFetchFailed(boolean popIfPurchaseFetchFailed)
        {
            return super.popIfPurchaseFetchFailed(popIfPurchaseFetchFailed);
        }

        @Override public BuilderType purchaseFetchedListener(@Nullable
        BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetchedListener)
        {
            return super.purchaseFetchedListener(purchaseFetchedListener);
        }

        @Override public BuilderType restorePurchase(boolean restorePurchase)
        {
            return super.restorePurchase(restorePurchase);
        }

        @Override public BuilderType popRestorePurchaseOutcome(boolean popRestorePurchaseOutcome)
        {
            return super.popRestorePurchaseOutcome(popRestorePurchaseOutcome);
        }

        @Override public BuilderType popRestorePurchaseOutcomeVerbose(boolean popRestorePurchaseOutcomeVerbose)
        {
            return super.popRestorePurchaseOutcomeVerbose(popRestorePurchaseOutcomeVerbose);
        }

        @Override public BuilderType purchaseRestorerListener(@Nullable
        BillingPurchaseRestorer.OnPurchaseRestorerListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseRestorerListener)
        {
            return super.purchaseRestorerListener(purchaseRestorerListener);
        }

        @Override public BuilderType doPurchase(boolean doPurchase)
        {
            return super.doPurchase(doPurchase);
        }

        @Override public BuilderType popIfPurchaseFailed(boolean popIfPurchaseFailed)
        {
            return super.popIfPurchaseFailed(popIfPurchaseFailed);
        }

        @Override public BuilderType purchaseFinishedListener(@Nullable
        BillingPurchaser.OnPurchaseFinishedListener<ProductIdentifierType, THPurchaseOrderType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFinishedListener)
        {
            return super.purchaseFinishedListener(purchaseFinishedListener);
        }
        //</editor-fold>

        //<editor-fold desc="Product Identifiers To Present">
        private ProductIdentifierDomain domainToPresent;

        public BuilderType domainToPresent(ProductIdentifierDomain domainToPresent)
        {
            this.domainToPresent = domainToPresent;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="Purchase Reporting">
        private boolean reportPurchase;
        @NonNull private OwnedPortfolioId applicablePortfolioId;

        private boolean popIfReportFailed;
        @Nullable private THPurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> purchaseReportedListener;

        public BuilderType reportPurchase(boolean reportPurchase)
        {
            this.reportPurchase = reportPurchase;
            return self();
        }

        public BuilderType applicablePortfolioId(@NonNull OwnedPortfolioId applicablePortfolioId)
        {
            this.applicablePortfolioId = applicablePortfolioId;
            return self();
        }

        public BuilderType popIfReportFailed(boolean popIfReportFailed)
        {
            this.popIfReportFailed = popIfReportFailed;
            return self();
        }

        public BuilderType purchaseReportedListener(
                @Nullable THPurchaseReporter.OnPurchaseReportedListener<
                        ProductIdentifierType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType> purchaseReportedListener)
        {
            this.purchaseReportedListener = purchaseReportedListener;
            return self();
        }
        //</editor-fold>

        //<editor-fold desc="User Following">
        private UserBaseKey userToPremiumFollow;

        public BuilderType userToPremiumFollow(UserBaseKey userToPremiumFollow)
        {
            this.userToPremiumFollow = userToPremiumFollow;
            return self();
        }
        //</editor-fold>

        @Override abstract public BaseTHUIBillingRequest<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                ProductDetailType,
                THPurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> build();
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "BaseTHUIBillingRequest:{" +
                super.toString() +
                ", domainToPresent=" + domainToPresent +
                ", reportPurchase=" + reportPurchase +
                ", applicablePortfolioId=" + applicablePortfolioId +
                ", popIfReportFailed=" + popIfReportFailed +
                ", purchaseReportedListener=" + purchaseReportedListener +
                ", userToPremiumFollow=" + userToPremiumFollow +
                '}';
    }
}
