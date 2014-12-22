package com.tradehero.th.models.security;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.models.number.THSignedMoney;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class WarrantDTOFormatter
{
    @Inject public WarrantDTOFormatter()
    {
        super();
    }

    public String getCombinedStrikePriceType(Context context, WarrantDTO warrantDTO)
    {
        if (warrantDTO.strikePrice == null)
        {
            return context.getString(R.string.na);
        }
        return THSignedMoney.builder(warrantDTO.strikePrice).currency(warrantDTO.strikePriceCcy).build().toString();
    }
}
