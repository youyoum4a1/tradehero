package com.tradehero.th.models.security;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.models.number.THSignedMoney;
import javax.inject.Inject;

public class WarrantDTOFormatter
{
    //<editor-fold desc="Constructors">
    @Inject public WarrantDTOFormatter()
    {
        super();
    }
    //</editor-fold>

    @NonNull public String getCombinedStrikePriceType(@NonNull Context context, @NonNull WarrantDTO warrantDTO)
    {
        if (warrantDTO.strikePrice == null)
        {
            return context.getString(R.string.na);
        }
        return THSignedMoney.builder(warrantDTO.strikePrice).currency(warrantDTO.strikePriceCcy).build().toString();
    }
}
