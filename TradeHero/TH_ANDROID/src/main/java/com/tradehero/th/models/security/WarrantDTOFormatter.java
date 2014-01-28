package com.tradehero.th.models.security;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import com.tradehero.th.R;
import com.tradehero.th.api.security.WarrantDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/28/14.
 */
@Singleton public class WarrantDTOFormatter
{
    public static final String TAG = WarrantDTOFormatter.class.getSimpleName();

    @Inject public WarrantDTOFormatter()
    {
        super();
    }
    
    public Spanned getCombinedStrikePriceType(Context context, WarrantDTO warrantDTO)
    {
        return Html.fromHtml(context.getString(
                R.string.warrant_strike_value,
                warrantDTO.strikePriceCcy,
                warrantDTO.strikePrice,
                warrantDTO.warrantType
        ));
    }
}
