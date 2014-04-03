package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.InboxVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.ProductPurchase;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Created by xavier on 3/26/14.
 */
abstract public class SamsungPurchase<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId>
        extends PurchaseVo
        implements ProductPurchase<
        SamsungSKUType,
        SamsungOrderIdType>
{
    protected final String groupId;
    private String productCode;

    //<editor-fold desc="Constructors">
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

    public SamsungPurchase(String groupId, InboxVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }
    //</editor-fold>

    public String getGroupId()
    {
        return groupId;
    }

    public String getProductCode()
    {
        return productCode;
    }

    @Override public void setJsonString(String jsonString)
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
