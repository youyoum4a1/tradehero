package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface ActorPurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        UserProfileDTOType extends UserProfileDTO>
{
    void forgetRequestCode(int requestCode);

    PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType> getPurchaseReportHandler(int requestCode);
    int registerPurchaseReportedHandler(
            PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType> purchaseReportedHandler);
    void launchReportSequence(int requestCode, ProductPurchaseType purchase);
    UserProfileDTOType launchReportSequenceSync(ProductPurchaseType purchase);
}
