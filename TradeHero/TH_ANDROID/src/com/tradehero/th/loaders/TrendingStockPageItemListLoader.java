package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/20/13 Time: 1:12 PM To change this template use File | Settings | File Templates. */
public class TrendingStockPageItemListLoader extends PagedItemListLoader<SecurityCompactDTO>
{
    private SecurityService securityService;

    public TrendingStockPageItemListLoader(Context context)
    {
        super(context);
    }

    @Override protected void onLoadPreviousPage(SecurityCompactDTO startItemId)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override protected void onLoadNextPage(SecurityCompactDTO lastItemId)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override protected boolean shouldReload()
    {
        return true;
        // TODO be cleverer
    }

    @Override public List<SecurityCompactDTO> loadInBackground()
    {
        if (securityService == null)
        {
            securityService = NetworkEngine.createService(SecurityService.class);
        }
        return securityService.getTrendingSecurities();
    }
}
