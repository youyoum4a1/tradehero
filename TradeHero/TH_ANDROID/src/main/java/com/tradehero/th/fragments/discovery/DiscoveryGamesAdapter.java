package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.games.GamesDTO;
import java.util.ArrayList;
import java.util.List;

public class DiscoveryGamesAdapter extends ArrayDTOAdapter<GamesDTO, GamesItemView>
{
    public DiscoveryGamesAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }

    @Override protected void fineTune(int position, GamesDTO dto, GamesItemView dtoView)
    {
    }

    @Override
    public void setItems(@NonNull List<GamesDTO> items) {
        List<GamesDTO> newItems = new ArrayList<>();
        List<GamesDTO> disableItems = new ArrayList<>();
        for (GamesDTO gamesDTO : items)
        {
            if (gamesDTO.comingSoon)
            {
                disableItems.add(gamesDTO);
            }
            else
            {
                newItems.add(gamesDTO);
            }
        }
        if (disableItems.size() > 0)
        {
            GamesDTO gamesDTO = new GamesDTO();
            gamesDTO.title = getContext().getString(R.string.coming_soon);
            newItems.add(gamesDTO);
            newItems.addAll(disableItems);
        }
        super.setItems(newItems);
    }
}
