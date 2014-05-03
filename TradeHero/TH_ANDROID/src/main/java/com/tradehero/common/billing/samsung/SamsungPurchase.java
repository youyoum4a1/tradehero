package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;


abstract public class SamsungPurchase<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId>
        extends PurchaseVo
        implements ProductPurchase<
        SamsungSKUType,
        SamsungOrderIdType>
{
    protected final String groupId;

    public SamsungPurchase(String groupId, String _jsonString)
    {
        super(_jsonString);
        this.groupId = groupId;
    }

    public SamsungPurchase(String groupId, PurchaseVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }

    public String getGroupId()
    {
        return groupId;
    }
}
