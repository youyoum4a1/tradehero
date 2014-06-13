package com.tradehero.th.fragments.security;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.List;

public class SecurityItemViewAdapterNew<SecurityCompactDTOType extends SecurityCompactDTO>
        extends ArrayDTOAdapterNew<SecurityCompactDTOType, SecurityItemView<SecurityCompactDTOType>>
{
    private Integer lastPageLoaded;

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

    public Integer getLastPageLoaded()
    {
        return lastPageLoaded;
    }

    public void addPage(int page, List<SecurityCompactDTOType> securityCompactDTOs)
    {
        this.lastPageLoaded = page;
        addAll(securityCompactDTOs);
    }

    @Override public void clear()
    {
        super.clear();
        this.lastPageLoaded = null;
    }
}