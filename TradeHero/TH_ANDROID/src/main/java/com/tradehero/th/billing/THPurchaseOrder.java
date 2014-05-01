package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.th.api.users.UserBaseKey;

public interface THPurchaseOrder<ProductIdentifierType extends ProductIdentifier>
    extends PurchaseOrder<ProductIdentifierType>
{
    void setUserToFollow(UserBaseKey userToFollow);
    UserBaseKey getUserToFollow();
}
