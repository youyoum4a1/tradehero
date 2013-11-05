package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.ProductIdentifier;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by julien on 4/11/13
 */
public class SKUDetails implements ProductDetails
{
    public final String itemType;
    protected SKU sku;
    public final String type;
    public final String price;
    public final String title;
    public final String description;
    protected final String json;

    public SKUDetails(String jsonSkuDetails) throws JSONException
    {
        this(Constants.ITEM_TYPE_INAPP, jsonSkuDetails);
    }

    public SKUDetails(String itemType, String jsonSkuDetails) throws JSONException {
        this.itemType = itemType;
        this.json = jsonSkuDetails;
        JSONObject o = new JSONObject(json);
        String skuString = o.optString("productId");
        this.sku =  new SKU(skuString);
        this.type = o.optString("type");
        this.price = o.optString("price");
        this.title = o.optString("title");
        this.description = o.optString("description");
    }

    @Override public String toString() {
        return "SkuDetails:" + json;
    }

    @Override public ProductIdentifier getProductIdentifier()
    {
        return this.sku;
    }
}
