package com.tradehero.th.persistence.watchlist;

import android.os.Bundle;
import com.tradehero.th.api.security.SecurityId;

public class WatchlistPositionId extends SecurityId
{
    public WatchlistPositionId(String exchange, String securitySymbol)
    {
        this(exchange, securitySymbol, 0);
    }

    public WatchlistPositionId(String exchange, String securitySymbol, Integer paged)
    {
        super(exchange, securitySymbol);
    }

    public WatchlistPositionId(Bundle args)
    {
        super(args);
    }
}
