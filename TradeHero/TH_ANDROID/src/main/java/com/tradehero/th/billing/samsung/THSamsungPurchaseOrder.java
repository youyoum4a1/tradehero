package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaseOrder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;

/**
 * Created by xavier on 3/27/14.
 */
public class THSamsungPurchaseOrder
    implements SamsungPurchaseOrder<SamsungSKU>,
        THPurchaseOrder<SamsungSKU>
{
    protected final SamsungSKU productIdentifier;
    protected final int quantity;
    protected UserBaseKey userToFollow;

    public THSamsungPurchaseOrder(SamsungSKU productIdentifier, int quantity)
    {
        this.productIdentifier = productIdentifier;
        this.quantity = quantity;
    }

    public THSamsungPurchaseOrder(String groupId, String itemId, int quantity)
    {
        this.productIdentifier = new SamsungSKU(groupId, itemId);
        this.quantity = quantity;
    }

    @Override public SamsungSKU getProductIdentifier()
    {
        return productIdentifier;
    }

    @Override public int getQuantity()
    {
        return quantity;
    }

    @Override public void setUserToFollow(UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @Override public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }
}
