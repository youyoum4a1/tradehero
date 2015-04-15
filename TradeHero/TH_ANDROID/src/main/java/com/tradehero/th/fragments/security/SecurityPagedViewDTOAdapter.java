package com.tradehero.th.fragments.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.PagedViewDTOAdapterImpl;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityPagedViewDTOAdapter extends PagedViewDTOAdapterImpl<SecurityCompactDTO, SecurityItemView>
{
    @Nullable protected WatchlistPositionDTOList watchedList;
    @Nullable private Map<SecurityId, AlertCompactDTO> mappedAlerts;

    //<editor-fold desc="Constructors">
    public SecurityPagedViewDTOAdapter(Context context, int resource)
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
                    if (securityCompactDTO instanceof FxSecurityCompactDTO)
                    {
                        ((FxSecurityCompactDTO) securityCompactDTO).setAskPrice(quote.ask);
                        ((FxSecurityCompactDTO) securityCompactDTO).setBidPrice(quote.bid);
                    }
                    else
                    {
                        securityCompactDTO.askPrice = quote.ask;
                        securityCompactDTO.bidPrice = quote.bid;
                    }
                }
            }
        }
    }

    public void setWatchList(WatchlistPositionDTOList watchedList)
    {
        this.watchedList = watchedList;
    }

    public void setAlertList(@Nullable Map<SecurityId, AlertCompactDTO> securityIdAlertIdMap)
    {
        mappedAlerts = securityIdAlertIdMap;
    }

    @Override public SecurityItemView getView(int position, View convertView, ViewGroup viewGroup)
    {
        SecurityItemView view = super.getView(position, convertView, viewGroup);
        view.display(mappedAlerts, watchedList);
        return view;
    }
}
