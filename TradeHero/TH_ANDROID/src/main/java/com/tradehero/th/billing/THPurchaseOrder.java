package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.th.api.users.UserBaseKey;

/**
 * Created by xavier on 3/24/14.
 */
public interface THPurchaseOrder<ProductIdentifierType extends ProductIdentifier>
    extends PurchaseOrder<ProductIdentifierType>
{
    void setUserToFollow(UserBaseKey userToFollow);
    UserBaseKey getUserToFollow();
}
