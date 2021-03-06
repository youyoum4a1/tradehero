package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class WatchlistPositionDTOList extends PositionDTOList<WatchlistPositionDTO>
{
    //<editor-fold desc="Constructors">
    public WatchlistPositionDTOList()
    {
        super();
    }
    //</editor-fold>

    @NonNull public SecurityIdList getSecurityIds()
    {
        SecurityIdList created = new SecurityIdList();
        for (WatchlistPositionDTO watchlistPositionDTO : this)
        {
            //noinspection ConstantConditions
            created.add(watchlistPositionDTO.securityDTO.getSecurityId());
        }
        return created;
    }

    @Nullable public Double getInvestedUsd()
    {
        double total = 0;
        Double investedOne;
        for (WatchlistPositionDTO watchlistItem: this)
        {
            investedOne = watchlistItem.getInvestedUsd();
            if (investedOne == null)
            {
                return null;
            }
            total += investedOne;
        }
        return total;
    }

    @Nullable public Double getCurrentValueUsd()
    {
        double total = 0;
        Double currentOne;
        for (WatchlistPositionDTO watchlistItem: this)
        {
            currentOne = watchlistItem.getCurrentValueUsd();
            if (currentOne == null)
            {
                return null;
            }
            total += currentOne;
        }
        return total;
    }

    public boolean contains(SecurityId other)
    {
        for (WatchlistPositionDTO watchlistPositionDTO : this)
        {
            if (watchlistPositionDTO.securityDTO != null && watchlistPositionDTO.securityDTO.getSecurityId().equals(other))
            {
                return true;
            }
        }
        return false;
    }

    public boolean remove(@NonNull SecurityId other)
    {
        boolean changed = false;
        for (WatchlistPositionDTO watchlistPositionDTO : new ArrayList<>(this))
        {
            //noinspection ConstantConditions
            if (watchlistPositionDTO.securityDTO.getSecurityId().equals(other))
            {
                remove(watchlistPositionDTO);
                changed = true;
            }
        }
        return changed;
    }
}
