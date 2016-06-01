package com.ayondo.academy.api.security.compact;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ayondo.academy.R;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.key.FxPairSecurityId;

public class FxSecurityCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "14";
    public static final int DEFAULT_TEXT_COLOR = R.color.text_primary;

    @JsonIgnore @ColorRes
    public int fxAskTextColorResId = DEFAULT_TEXT_COLOR;
    @JsonIgnore @ColorRes
    public int fxBidTextColorResId = DEFAULT_TEXT_COLOR;

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
    public void setAskPrice(Double newAskPrice) {
        if (askPrice != null)
        {
            if (askPrice.compareTo(newAskPrice) > 0)
            {
                fxAskTextColorResId = R.color.number_red;
            }
            else if (askPrice.compareTo(newAskPrice) < 0)
            {
                fxAskTextColorResId = R.color.number_green;
            }
            else
            {
                fxAskTextColorResId = R.color.text_primary;
            }
        }
        askPrice = newAskPrice;
    }

    @JsonIgnore
    public void setBidPrice(Double newBidPrice) {
        if (bidPrice != null)
        {
            if (bidPrice.compareTo(newBidPrice) > 0)
            {
                fxBidTextColorResId = R.color.number_red;
            }
            else if (bidPrice.compareTo(newBidPrice) < 0)
            {
                fxBidTextColorResId = R.color.number_green;
            }
            else
            {
                fxBidTextColorResId = R.color.text_primary;
            }
        }
        this.bidPrice = newBidPrice;
    }
}
