package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.th.utils.StringUtils;

public class AyondoLeveragedProductList extends BaseArrayList<AyondoLeveragedProduct>
{
    @JsonCreator public AyondoLeveragedProductList(@Nullable String concatenated)
    {
        if (concatenated != null)
        {
            String[] split = concatenated.split(",");
            for (String candidate : split)
            {
                add(AyondoLeveragedProduct.getLeveragedProduct(candidate));
            }
        }
    }

    @JsonValue @NonNull @Override public String toString()
    {
        //noinspection ConstantConditions
        return StringUtils.join(",", this);
    }
}
