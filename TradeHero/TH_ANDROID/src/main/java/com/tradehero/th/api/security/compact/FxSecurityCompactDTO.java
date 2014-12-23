package com.tradehero.th.api.security.compact;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;

public class FxSecurityCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "14";

    @JsonIgnore
    public int fxAskTextColor = 0;
    @JsonIgnore
    public int fxBidTextColor = 0;

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_fx;
    }

    @NonNull public FxPairSecurityId getFxPair()
    {
        String[] split = symbol.split("_");
        return new FxPairSecurityId(split[0], split[1]);
    }

    @JsonIgnore
    public void setAskPrice(Context context, Double newAskPrice) {
        if (askPrice != null)
        {
            if (askPrice.compareTo(newAskPrice) > 0)
            {
                fxAskTextColor = context.getResources().getColor(R.color.number_red);
            }
            else if (askPrice.compareTo(newAskPrice) < 0)
            {
                fxAskTextColor = context.getResources().getColor(R.color.number_green);
            }
            else
            {
                fxAskTextColor = context.getResources().getColor(R.color.text_primary);
            }
        }
        askPrice = newAskPrice;
    }

    @JsonIgnore
    public void setBidPrice(Context context, Double newBidPrice) {
        if (bidPrice != null)
        {
            if (bidPrice.compareTo(newBidPrice) > 0)
            {
                fxBidTextColor = context.getResources().getColor(R.color.number_red);
            }
            else if (bidPrice.compareTo(newBidPrice) < 0)
            {
                fxBidTextColor = context.getResources().getColor(R.color.number_green);
            }
            else
            {
                fxBidTextColor = context.getResources().getColor(R.color.text_primary);
            }
        }
        this.bidPrice = newBidPrice;
    }
}
