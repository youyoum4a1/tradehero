package com.tradehero.th.fragments.security;

import android.content.Context;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class SecurityItemViewAdapterNew<SecurityCompactDTOType extends SecurityCompactDTO>
        extends PagedArrayDTOAdapterNew<SecurityCompactDTOType, SecurityItemView<SecurityCompactDTOType>>
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