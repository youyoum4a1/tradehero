package com.tradehero.common.billing.samsung;

import android.content.Intent;
import com.tradehero.common.billing.BaseBillingLogicHolder;
import com.tradehero.common.billing.BillingAvailableTesterHolder;
import com.tradehero.common.billing.ProductDetailTuner;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class BaseSamsungLogicHolder<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungBillingAvailableTesterHolderType extends SamsungBillingAvailableTesterHolder<SamsungException>,
        SamsungProductIdentifierFetcherHolderType extends SamsungProductIdentifierFetcherHolder<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungException>,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        SamsungInventoryFetcherHolderType extends SamsungInventoryFetcherHolder<
                SamsungSKUType,
                SamsungProductDetailType,
                SamsungException>,
        ProductTunerType extends ProductDetailTuner<SamsungSKUType, SamsungProductDetailType>,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<
                SamsungSKUType,
                SamsungOrderIdType>,
        SamsungPurchaseFetcherHolderType extends SamsungPurchaseFetcherHolder<
                SamsungSKUType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                SamsungException>,
        SamsungPurchaserHolderType extends SamsungPurchaserHolder<
                SamsungSKUType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                SamsungException>,
        BillingRequestType extends BillingRequest<
                SamsungSKUListKeyType,
                SamsungSKUType,
                SamsungSKUListType,
                SamsungProductDetailType,
                SamsungPurchaseOrderType,
                SamsungOrderIdType,
                SamsungPurchaseType,
                SamsungException>>
    extends BaseBillingLogicHolder<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungProductDetailType,
        ProductTunerType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        BillingRequestType,
        SamsungException>
    implements SamsungLogicHolder<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungProductDetailType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        BillingRequestType,
        SamsungException>
{
    protected SamsungBillingAvailableTesterHolderType availabilityTesterHolder;
    protected SamsungProductIdentifierFetcherHolderType productIdentifierFetcherHolder;
    protected SamsungInventoryFetcherHolderType inventoryFetcherHolder;
    protected SamsungPurchaseFetcherHolderType purchaseFetcherHolder;
    protected SamsungPurchaserHolderType purchaserHolder;

    public BaseSamsungLogicHolder()
    {
        super();
        availabilityTesterHolder = createBillingAvailableTesterHolder();
        productIdentifierFetcherHolder = createProductIdentifierFetcherHolder();
        inventoryFetcherHolder = createInventoryFetcherHolder();
        purchaseFetcherHolder = createPurchaseFetcherHolder();
        purchaserHolder = createPurchaserHolder();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        if (availabilityTesterHolder != null)
        {
            availabilityTesterHolder.onDestroy();
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

        if (availabilityTesterHolder != null)
        {
            availabilityTesterHolder.onDestroy();
        }
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return
                productIdentifierFetcherHolder.isUnusedRequestCode(requestCode) &&
                inventoryFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaseFetcherHolder.isUnusedRequestCode(requestCode) &&
                purchaserHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        productIdentifierFetcherHolder.forgetRequestCode(requestCode);
        inventoryFetcherHolder.forgetRequestCode(requestCode);
        purchaseFetcherHolder.forgetRequestCode(requestCode);
        purchaserHolder.forgetRequestCode(requestCode);
    }

    abstract protected BaseSamsungSKUList<SamsungSKUType> getAllSkus();

    abstract protected SamsungBillingAvailableTesterHolderType createBillingAvailableTesterHolder();
    abstract protected SamsungProductIdentifierFetcherHolderType createProductIdentifierFetcherHolder();
    abstract protected SamsungInventoryFetcherHolderType createInventoryFetcherHolder();
    abstract protected SamsungPurchaseFetcherHolderType createPurchaseFetcherHolder();
    abstract protected SamsungPurchaserHolderType createPurchaserHolder();

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
