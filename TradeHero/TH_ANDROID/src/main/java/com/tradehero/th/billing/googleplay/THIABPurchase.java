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

package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import org.json.JSONException;

/**
 * Represents an in-app billing purchase usable in TradeHero.
 */
public class THIABPurchase extends BaseIABPurchase
{
    public static final String TAG = THIABPurchase.class.getSimpleName();

    public THIABPurchase(String itemType, String jsonPurchaseInfo, String signature) throws JSONException
    {
        super(itemType, jsonPurchaseInfo, signature);
    }

    public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        if (developerPayload != null)
        {
            return (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(developerPayload, OwnedPortfolioId.class);
        }
        return null;
    }

    @Override public String toString()
    {
        return "THIABPurchase(type:" + itemType + "):" + originalJson + "; signature:" + signature;
    }
}
