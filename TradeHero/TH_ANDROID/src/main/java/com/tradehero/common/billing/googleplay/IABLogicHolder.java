package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.googleplay.IABProductIdentifierFetcherHolder;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface IABLogicHolder<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABConsumeFinishedListenerType extends IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABExceptionType extends IABException>
    extends
        BillingLogicHolder<
                        IABSKUType,
                        IABProductDetailType,
                        IABPurchaseOrderType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABPurchaseFinishedListenerType,
                        IABExceptionType>,
        IABPurchaseFetcherHolder<
                        IABSKUType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABPurchaseFetchedListenerType,
                        IABExceptionType>,
        IABPurchaseConsumerHolder<
                        IABSKUType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABConsumeFinishedListenerType,
                        IABExceptionType>
{
}
