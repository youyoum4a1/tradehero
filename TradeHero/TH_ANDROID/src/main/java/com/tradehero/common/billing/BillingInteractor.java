package com.tradehero.common.billing;

import android.app.AlertDialog;
import android.content.Intent;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingInteractor<
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
                        BillingRequestType,
                        BillingExceptionType>,
        BillingRequestType extends BillingRequest<
                        ProductIdentifierType,
                        ProductDetailType,
                        PurchaseOrderType,
                        OrderIdType,
                        ProductPurchaseType,
                        BillingExceptionType>,
        UIBillingRequestType extends UIBillingRequest<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    void onPause();
    void onStop();
    void onDestroy();

    int getUnusedRequestCode();
    void forgetRequestCode(int requestCode);
    BillingLogicHolderType getBillingLogicHolder();

    void doAction(int action);

    AlertDialog popBillingUnavailable(BillingExceptionType billingException);
    int run(UIBillingRequestType uiBillingRequest);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
