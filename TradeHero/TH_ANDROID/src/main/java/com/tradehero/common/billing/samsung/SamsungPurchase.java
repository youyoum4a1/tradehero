package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.InboxVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.ProductPurchase;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

abstract public class SamsungPurchase<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId>
        extends PurchaseVo
        implements ProductPurchase<
        SamsungSKUType,
        SamsungOrderIdType>
{
    @NotNull protected final String groupId;
    private String productCode;

    //<editor-fold desc="Constructors">
    public SamsungPurchase(@NotNull String groupId, @NotNull String _jsonString)
    {
        super(_jsonString);
        this.groupId = groupId;
    }

    public SamsungPurchase(@NotNull String groupId, @NotNull PurchaseVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }

    public SamsungPurchase(@NotNull String groupId, @NotNull InboxVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }
    //</editor-fold>

    @NotNull public String getGroupId()
    {
        return groupId;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    @Override public void setJsonString(@NotNull String jsonString)
    {
        super.setJsonString(jsonString);
        try
        {
            JSONObject jObject = new JSONObject(jsonString);
            productCode = jObject.optString(SamsungConstants.PRODUCT_CODE_JSON_KEY);
        }
        catch(JSONException e)
        {
            Timber.e(new Exception(jsonString, e), "");
        }
    }
}
