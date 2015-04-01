package com.tradehero.th.api.trade;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.api.position.OwnedPositionId;

public class TradeDTOListKeyFactory
{
    @NonNull public static TradeDTOListKey create(@NonNull Bundle args)
    {
        TradeDTOListKey created;
        if (SecurityTradeDTOListKey.isValid(args))
        {
            created = new SecurityTradeDTOListKey(args);
        }
        else if (OwnedPositionId.isValid(args))
        {
            created = new OwnedPositionId(args);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled Bundle type");
        }
        return created;
    }
}
