package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.competition.key.HelpVideoId;

public class ProviderVideoAdapter extends ArrayDTOAdapter<HelpVideoId, ProviderVideoListItem>
{
    //<editor-fold desc="Constructors">
    public ProviderVideoAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, HelpVideoId videoId, final ProviderVideoListItem dtoView)
    {
        // Nothing to do
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : ((HelpVideoId) item).hashCode();
    }
}