package com.tradehero.th.api.watchlist;

import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WatchlistPositionDTOList extends PositionDTOList<WatchlistPositionDTO>
{
    //<editor-fold desc="Constructors">
    public WatchlistPositionDTOList()
    {
        super();
    }
    //</editor-fold>

    @NotNull public SecurityIdList getSecurityIds()
    {
        SecurityIdList created = new SecurityIdList();
        for (@NotNull WatchlistPositionDTO watchlistPositionDTO : this)
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
        for (@NotNull WatchlistPositionDTO watchlistItem: this)
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
        for (@NotNull WatchlistPositionDTO watchlistItem: this)
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

    public boolean remove(@NotNull SecurityId other)
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
