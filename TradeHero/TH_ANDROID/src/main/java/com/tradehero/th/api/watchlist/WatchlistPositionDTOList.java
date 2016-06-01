package com.ayondo.academy.api.watchlist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.security.SecurityId;
import java.util.ArrayList;

public class WatchlistPositionDTOList extends BaseArrayList<WatchlistPositionDTO>
    implements DTO
{
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

    public boolean contains(@Nullable SecurityId other)
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
