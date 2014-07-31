package com.tradehero.th.fragments.competition;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.competition.HelpVideoDTO;

public class ProviderVideoAdapter extends ArrayDTOAdapterNew<HelpVideoDTO, ProviderVideoListItem>
{
    //<editor-fold desc="Constructors">
    public ProviderVideoAdapter(Context context, int layoutResourceId)
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
        return getItem(position).getHelpVideoId().hashCode();
    }
}