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

    //<editor-fold desc="Constructors">
    public THSamsungPurchaseOrder(String groupId, String itemId)
    {
        this(groupId, itemId, 1);
    }

    public THSamsungPurchaseOrder(String groupId, String itemId, int quantity)
    {
        this(new SamsungSKU(groupId, itemId), quantity);
    }

    public THSamsungPurchaseOrder(SamsungSKU productIdentifier)
    {
        this(productIdentifier, 1);
    }

    public THSamsungPurchaseOrder(SamsungSKU productIdentifier, int quantity)
    {
        this.productIdentifier = productIdentifier;
        this.quantity = quantity;
    }
    //</editor-fold>

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
