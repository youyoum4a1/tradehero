package com.tradehero.th.fragments.security;

import android.content.Context;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class SecurityItemViewAdapterNew
        extends PagedArrayDTOAdapterNew<SecurityCompactDTO, SecurityItemView>
{
    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapterNew(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
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
}