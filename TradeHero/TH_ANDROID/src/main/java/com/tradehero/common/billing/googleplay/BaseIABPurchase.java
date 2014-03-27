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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Represents an in-app billing purchase.
 */
abstract public class BaseIABPurchase<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId>
        implements IABPurchase<IABSKUType, IABOrderIdType>
{
    public static final String JSON_KEY_ORDER_ID = "orderId";
    public static final String JSON_KEY_PACKAGE_NAME = "packageName";
    public static final String JSON_KEY_PRODUCT_ID = "productId";
    public static final String JSON_KEY_PURCHASE_TIME = "purchaseTime";
    public static final String JSON_KEY_PURCHASE_STATE = "purchaseState";
    public static final String JSON_KEY_DEVELOPER_PAY_LOAD = "developerPayload";
    public static final String JSON_KEY_TOKEN = "token";
    public static final String JSON_KEY_PURCHASE_TOKEN = "purchaseToken";

    public final String itemType;  // IABConstants.ITEM_TYPE_INAPP or IABConstants.ITEM_TYPE_SUBS
    protected final IABOrderIdType orderId;
    public final String packageName;
    protected final IABSKUType iabSKU;
    public final long purchaseTime;
    public final int purchaseState;
    public final String developerPayload;
    public final String token;
    public final String originalJson;
    public final String signature;

    public BaseIABPurchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException
    {
        this.itemType = itemType;
        this.originalJson = jsonPurchaseInfo;
        JSONObject o = new JSONObject(this.originalJson);
        String orderIdString = o.optString(JSON_KEY_ORDER_ID);
        this.orderId = createIABOrderId(orderIdString);
        this.packageName = o.optString(JSON_KEY_PACKAGE_NAME);
        String skuString = o.optString(JSON_KEY_PRODUCT_ID);
        this.iabSKU = createIABSKU(skuString);
        this.purchaseTime = o.optLong(JSON_KEY_PURCHASE_TIME);
        this.purchaseState = o.optInt(JSON_KEY_PURCHASE_STATE);

        // HACK
        //Timber.d("%s {\"userId\":239284,\"portfolioId\"611105}", o.optString(JSON_KEY_DEVELOPER_PAY_LOAD));
        //if (o.optString(JSON_KEY_DEVELOPER_PAY_LOAD) != null &&
        //        o.optString(JSON_KEY_DEVELOPER_PAY_LOAD).equals("{\"userId\":239284,\"portfolioId\"611105}"))
        //{
        //    Timber.d("HACK Fixing bad Json");
        //    developerPayload = "{\"userId\":239284,\"portfolioId\":611105}";
        //}
        //else
        //{
        //this.developerPayload = o.optString(JSON_KEY_DEVELOPER_PAY_LOAD); // This is the only non-HACK line
        //}

        this.developerPayload = o.optString(JSON_KEY_DEVELOPER_PAY_LOAD);
        this.token = o.optString(JSON_KEY_TOKEN, o.optString(JSON_KEY_PURCHASE_TOKEN));
        this.signature = signature;
    }

    abstract protected IABSKUType createIABSKU(String skuString);
    abstract protected IABOrderIdType createIABOrderId(String orderIdString);

    @JsonIgnore
    @Override public String getType()
    {
        return itemType;
    }

    @JsonIgnore
    @Override public String getToken()
    {
        return token;
    }

    @JsonIgnore
    @Override public IABOrderIdType getOrderId()
    {
        return orderId;
    }

    @JsonIgnore
    @Override public IABSKUType getProductIdentifier()
    {
        return iabSKU;
    }

    @JsonIgnore
    @Override public String getOriginalJson()
    {
        return this.originalJson;
    }

    @JsonIgnore
    @Override public String getSignature()
    {
        return this.signature;
    }

    @Override public GooglePlayPurchaseDTO getGooglePlayPurchaseDTO()
    {
        String signature = this.signature;
        if (signature != null)
        {
            // Test its length is a multiple of 4
            int remainderFour = this.signature.length() % 4;
            if (remainderFour != 0)
            {
                Timber.e(new IllegalArgumentException("Patching Google purchase signature that was not of the right length " + signature.length() + " " + this.originalJson + " " + signature), "");
                for (int i = 0; i < 4 - remainderFour; i++)
                {
                    signature += "=";
                }
            }
        }
        return new GooglePlayPurchaseDTO(this.originalJson, signature);
    }

    @Override public String toString()
    {
        return "PurchaseInfo(type:" + itemType + "):" + originalJson + "; signature:" + signature;
    }
}
