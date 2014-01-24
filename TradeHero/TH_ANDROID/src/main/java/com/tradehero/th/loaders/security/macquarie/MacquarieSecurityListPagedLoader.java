package com.tradehero.th.loaders.security.macquarie;

import android.content.Context;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;

/**
 * Created by xavier on 12/13/13.
 */
// TODO fix server and remove this class
public class MacquarieSecurityListPagedLoader extends SecurityListPagedLoader
{
    public static final String TAG = MacquarieSecurityListPagedLoader.class.getSimpleName();


    public MacquarieSecurityListPagedLoader(Context context)
    {
        super(context);
    }

    @Override public SecurityIdList loadInBackground()
    {
        // HACK otherwise the server keeps returning the same value over and over
        if (this.getQueryKey().page > 1)
        {
            getCache().put(getQueryKey(), new SecurityIdList());
            setHasNoMorePages(true);
            return new SecurityIdList();
        }
        return super.loadInBackground();
    }
}
