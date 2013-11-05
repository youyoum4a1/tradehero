/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an in-app billing purchase.
 */
public class GooglePurchase implements ProductPurchase<GoogleOrderId, SKU>
{
    public static final String JSON_KEY_ORDER_ID = "orderId";
    public static final String JSON_KEY_PACKAGE_NAME = "packageName";
    public static final String JSON_KEY_PRODUCT_ID = "productId";
    public static final String JSON_KEY_PURCHASE_TIME = "purchaseTime";
    public static final String JSON_KEY_PURCHASE_STATE = "purchaseState";
    public static final String JSON_KEY_DEVELOPER_PAY_LOAD = "developerPayload";
    public static final String JSON_KEY_TOKEN = "token";
    public static final String JSON_KEY_PURCHASE_TOKEN = "purchaseToken";

    public final String itemType;  // ITEM_TYPE_INAPP or ITEM_TYPE_SUBS
    protected final GoogleOrderId orderId;
    public final String packageName;
    protected final SKU sku;
    public final long purchaseTime;
    public final int purchaseState;
    public final String developerPayload;
    public final String token;
    public final String originalJson;
    public final String signature;

    public GooglePurchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException
    {
        this.itemType = itemType;
        originalJson = jsonPurchaseInfo;
        JSONObject o = new JSONObject(originalJson);
        String orderIdString = o.optString(JSON_KEY_ORDER_ID);
        orderId = new GoogleOrderId(orderIdString);
        packageName = o.optString(JSON_KEY_PACKAGE_NAME);
        String skuString = o.optString(JSON_KEY_PRODUCT_ID);
        sku = new SKU(skuString);
        purchaseTime = o.optLong(JSON_KEY_PURCHASE_TIME);
        purchaseState = o.optInt(JSON_KEY_PURCHASE_STATE);
        developerPayload = o.optString(JSON_KEY_DEVELOPER_PAY_LOAD);
        token = o.optString(JSON_KEY_TOKEN, o.optString(JSON_KEY_PURCHASE_TOKEN));
        this.signature = signature;
    }

    @Override public GoogleOrderId getOrderId()
    {
        return orderId;
    }

    @Override public SKU getProductIdentifier()
    {
        return sku;
    }

    @Override public String toString()
    {
        return "PurchaseInfo(type:" + itemType + "):" + originalJson;
    }
}
