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

package com.androidth.general.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.billing.googleplay.BaseIABPurchase;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.SkuTypeValue;
import com.androidth.general.common.utils.THJsonAdapter;
import com.androidth.general.api.billing.GooglePlayPurchaseReportDTO;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.billing.THProductPurchase;
import org.json.JSONException;
import timber.log.Timber;

public class THIABPurchase
        extends BaseIABPurchase<IABSKU, THIABOrderId>
    implements THProductPurchase<IABSKU, THIABOrderId>
{
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THIABPurchase(
            @NonNull @SkuTypeValue String itemType,
            @NonNull String jsonPurchaseInfo,
            @NonNull String signature) throws JSONException
    {
        super(itemType, jsonPurchaseInfo, signature);
    }
    //</editor-fold>

    @Override @NonNull public GooglePlayPurchaseReportDTO getPurchaseReportDTO()
    {
        String signature = this.signature;
        // Test its length is a multiple of 4
        int remainderFour = signature.length() % 4;
        if (remainderFour != 0)
        {
            Timber.e(new IllegalArgumentException(
                    "Patching Google purchase signature that was not of the right length "
                            + signature.length()
                            + " "
                            + this.originalJson
                            + " "
                            + signature), "");
            for (int i = 0; i < 4 - remainderFour; i++)
            {
                signature += "=";
            }
        }
        return new GooglePlayPurchaseReportDTO(this.originalJson, signature);
    }

    @Override @NonNull protected IABSKU createIABSKU(@NonNull String skuString)
    {
        return new IABSKU(skuString);
    }

    @Override @NonNull protected THIABOrderId createIABOrderId(String orderIdString)
    {
        return new THIABOrderId(orderIdString);
    }

    @JsonIgnore @NonNull
    @Override public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(developerPayload, OwnedPortfolioId.class);
    }

    @JsonIgnore
    @Override public void setUserToFollow(@Nullable UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @JsonIgnore
    @Override @Nullable public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    @Override public String toString()
    {
        return "THIABPurchase(type:" + itemType + "):" + originalJson + "; signature:" + signature;
    }
}
