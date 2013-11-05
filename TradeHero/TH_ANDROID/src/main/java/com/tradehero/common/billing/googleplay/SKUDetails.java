package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductDetails;
import com.tradehero.common.billing.ProductIdentifier;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by julien on 4/11/13
 */
public class SKUDetails implements ProductDetails<SKU>
{
    public static final String JSON_KEY_PRODUCT_ID = "productId";
    public static final String JSON_KEY_TYPE = "type";
    public static final String JSON_KEY_PRICE = "price";
    public static final String JSON_KEY_TITLE = "title";
    public static final String JSON_KEY_DESCRIPTION = "description";

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

    public SKUDetails(String itemType, String jsonSkuDetails) throws JSONException
    {
        this.itemType = itemType;
        this.json = jsonSkuDetails;
        JSONObject o = new JSONObject(json);
        String skuString = o.optString(JSON_KEY_PRODUCT_ID);
        this.sku =  new SKU(skuString);
        this.type = o.optString(JSON_KEY_TYPE);
        this.price = o.optString(JSON_KEY_PRICE);
        this.title = o.optString(JSON_KEY_TITLE);
        this.description = o.optString(JSON_KEY_DESCRIPTION);
    }

    @Override public String toString()
    {
        return "SkuDetails:" + json;
    }

    @Override public SKU getProductIdentifier()
    {
        return this.sku;
    }
}
