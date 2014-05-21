package com.tradehero.common.billing.googleplay;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseIABProductDetail implements IABProductDetail<IABSKU>
{
    public static final String JSON_KEY_PRODUCT_ID = "productId";
    public static final String JSON_KEY_TYPE = "type";
    public static final String JSON_KEY_PRICE = "price";
    public static final String JSON_KEY_PRICE_MICROS = "price_amount_micros";
    public static final String JSON_KEY_PRICE_CURRENCY_CODE = "price_currency_code";
    public static final String JSON_KEY_TITLE = "title";
    public static final String JSON_KEY_DESCRIPTION = "description";

    public final String itemType;
    protected IABSKU iabSKU;
    public final String type;
    public final String price;
    public final Long priceAmountMicros;
    public final String priceCurrencyCode;
    public final String title;
    public final String description;
    protected final String json;

    //<editor-fold desc="Constructors">
    public BaseIABProductDetail(String jsonSkuDetails) throws JSONException
    {
        this(IABConstants.ITEM_TYPE_INAPP, jsonSkuDetails);
    }

    public BaseIABProductDetail(String itemType, String jsonSkuDetails) throws JSONException
    {
        this.itemType = itemType;
        this.json = jsonSkuDetails;
        JSONObject o = new JSONObject(json);
        String skuString = o.optString(JSON_KEY_PRODUCT_ID);
        this.iabSKU =  new IABSKU(skuString);
        this.type = o.optString(JSON_KEY_TYPE);
        this.price = o.optString(JSON_KEY_PRICE);
        // This field is probably dependent on the version of the google play installed.
        // To have it, perhaps only opening the Google Play Store twice is enough...
        // https://code.google.com/p/marketbilling/issues/detail?id=93
        this.priceAmountMicros = o.optLong(JSON_KEY_PRICE_MICROS);
        this.priceCurrencyCode = o.optString(JSON_KEY_PRICE_CURRENCY_CODE);
        this.title = o.optString(JSON_KEY_TITLE);
        this.description = o.optString(JSON_KEY_DESCRIPTION);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "SkuDetails:" + json;
    }

    //<editor-fold desc="IABProductDetail<IABSKU>">
    @Override public IABSKU getProductIdentifier()
    {
        return this.iabSKU;
    }

    @Override public String getType()
    {
        return type;
    }

    @Override public boolean isOfType(String type)
    {
        return this.type == null ? type == null : this.type.equals(type);
    }
    //</editor-fold>
}
