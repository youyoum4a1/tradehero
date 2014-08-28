package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.ItemVo;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Created by xavier on 3/27/14.
 */
public class BaseSamsungProductDetail<SamsungSKUType extends SamsungSKU>
    extends ItemVo
    implements SamsungProductDetail<SamsungSKUType>
{
    private final SamsungSKUType samsungSKU;
    private String productCode;

    //<editor-fold desc="Constructors">
    public BaseSamsungProductDetail(SamsungSKUType samsungSKU)
    {
        super();
        this.samsungSKU = samsungSKU;
    }

    public BaseSamsungProductDetail(SamsungSKUType samsungSKU, String _jsonString)
    {
        super(_jsonString);
        this.samsungSKU = samsungSKU;
    }

    public BaseSamsungProductDetail(SamsungSKUType samsungSKU, ItemVo itemVo)
    {
        super(itemVo.getJsonString());
        this.samsungSKU = samsungSKU;
    }
    //</editor-fold>

    @Override public SamsungSKUType getProductIdentifier()
    {
        return samsungSKU;
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
