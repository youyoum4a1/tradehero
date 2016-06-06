package com.androidth.general.fragments.security;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.adapters.PagedViewDTOAdapterImpl;
import com.androidth.general.api.quote.QuoteDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FXSecurityPagedViewDTOAdapter extends PagedViewDTOAdapterImpl<SecurityCompactDTO, FXItemView>
{
    //<editor-fold desc="Constructors">
    public FXSecurityPagedViewDTOAdapter(Context context, int resource)
    {
        super(context, resource);
    }
    //</editor-fold>

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).id;
    }

    public void updatePrices(@NonNull List<? extends QuoteDTO> quotes)
    {
        Map<SecurityIntegerId, QuoteDTO> map = new HashMap<>();
        for (QuoteDTO quote : quotes)
        {
            map.put(quote.getSecurityIntegerId(), quote);
        }
        updatePrices(map);
    }

    public void updatePrices(@NonNull Map<SecurityIntegerId, ? extends QuoteDTO> quotes)
    {
        SecurityCompactDTO securityCompactDTO;
        QuoteDTO quote;
        if (getCount() > 0)
        {
            for (int index = 0; index < getCount(); index++)
            {
                securityCompactDTO = getItem(index);
                quote = quotes.get(securityCompactDTO.getSecurityIntegerId());
                if (quote != null)
                {
                    ((FxSecurityCompactDTO) securityCompactDTO).setAskPrice(quote.ask);
                    ((FxSecurityCompactDTO) securityCompactDTO).setBidPrice(quote.bid);
                }
            }
        }
    }
}
