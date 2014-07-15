package com.tradehero.th.loaders.security.macquarie;

import android.content.Context;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;

// TODO fix server and remove this class
public class MacquarieSecurityListPagedLoader extends SecurityListPagedLoader
{
    public MacquarieSecurityListPagedLoader(Context context)
    {
        super(context);
    }

    @Override public SecurityCompactDTOList loadInBackground()
    {
        // HACK otherwise the server keeps returning the same value over and over
        if (this.getQueryKey().page != null && this.getQueryKey().page > 1)
        {
            getCache().put(getQueryKey(), new SecurityCompactDTOList());
            setHasNoMorePages(true);
            return new SecurityCompactDTOList();
        }
        return super.loadInBackground();
    }
}
