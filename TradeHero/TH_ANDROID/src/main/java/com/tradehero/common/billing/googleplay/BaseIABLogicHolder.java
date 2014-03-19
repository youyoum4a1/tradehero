package com.tradehero.common.billing.googleplay;

import android.content.Intent;
import com.tradehero.common.billing.BaseBillingLogicHolder;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.request.BillingRequest;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABLogicHolder<
        IABSKUType extends IABSKU,
        IABProductIdentifierFetcherHolderType extends ProductIdentifierFetcherHolder<
                IABSKUType,
                IABException>,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABInventoryFetcherHolderType extends IABInventoryFetcherHolder<
                IABSKUType,
                IABProductDetailType,
                IABException>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<
                IABSKUType,
                IABOrderIdType>,
        IABPurchaseFetcherHolderType extends IABPurchaseFetcherHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        IABPurchaserHolderType extends IABPurchaserHolder<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        IABPurchaseConsumerHolderType extends IABPurchaseConsumerHolder<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>,
        BillingRequestType extends BillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    extends BaseBillingLogicHolder<
        IABSKUType,
        IABProductDetailType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType,
        BillingRequestType,
        IABException>
    implements IABLogicHolder<
            IABSKUType,
            IABProductDetailType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType,
            BillingRequestType,
            IABException>
{
    protected IABServiceConnector availabilityTester;
    protected IABProductIdentifierFetcherHolderType productIdentifierFetcherHolder;
    protected IABInventoryFetcherHolderType inventoryFetcherHolder;
    protected IABPurchaseFetcherHolderType purchaseFetcherHolder;
    protected IABPurchaserHolderType purchaserHolder;
    protected IABPurchaseConsumerHolderType purchaseConsumerHolder;

    public BaseIABLogicHolder()
    {
        super();
        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();
        purchaseConsumerHolder = createPurchaseConsumeHolder();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        if (availabilityTester != null)
        {
            availabilityTester.onDestroy();
        }

        if (productIdentifierFetcherHolder != null)
        {
            productIdentifierFetcherHolder.onDestroy();
        }

        if (inventoryFetcherHolder != null)
        {
            inventoryFetcherHolder.onDestroy();
        }

        if (purchaserHolder != null)
        {
            purchaserHolder.onDestroy();
        }

        if (purchaseConsumerHolder != null)
        {
            purchaseConsumerHolder.onDestroy();
        }

        if (availabilityTester != null)
        {
            availabilityTester.onDestroy();
        }
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return
                productIdentifierFetcherHolder.isUnusedRequestCode(requestCode) &&
                inventoryFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaseFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaserHolder.isUnusedRequestCode(requestCode) &&
                purchaseConsumerHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        productIdentifierFetcherHolder.forgetRequestCode(requestCode);
        inventoryFetcherHolder.forgetRequestCode(requestCode);
        purchaseFetcherHolder.forgetRequestCode(requestCode);
        purchaserHolder.forgetRequestCode(requestCode);
        purchaseConsumerHolder.forgetRequestCode(requestCode);
    }

    abstract protected BaseIABSKUList<IABSKUType> getAllSkus();
    abstract protected IABProductIdentifierFetcherHolderType createProductIdentifierFetcherHolder();
    abstract protected IABInventoryFetcherHolderType createInventoryFetcherHolder();
    abstract protected IABPurchaseFetcherHolderType createPurchaseFetcherHolder();
    abstract protected IABPurchaserHolderType createPurchaserHolder();
    abstract protected IABPurchaseConsumerHolderType createPurchaseConsumeHolder();

    @Override public void registerInventoryFetchedListener(int requestCode,
            BillingInventoryFetcher.OnInventoryFetchedListener<
                    IABSKUType,
                    IABProductDetailType,
                    IABException> inventoryFetchedListener)
    {
        inventoryFetcherHolder.registerInventoryFetchedListener(requestCode, inventoryFetchedListener);
    }

    @Override public void registerPurchaseFetchedListener(int requestCode,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<
                    IABSKUType,
                    IABOrderIdType,
                    IABPurchaseType,
                    IABException> purchaseFetchedListener)
    {
        purchaseFetcherHolder.registerPurchaseFetchedListener(requestCode, purchaseFetchedListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        purchaserHolder.onActivityResult(requestCode, resultCode, data);
    }
}
