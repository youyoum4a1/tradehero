package com.ayondo.academy.fragments.competition;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.ayondo.academy.adapters.ArrayDTOAdapterNew;
import com.ayondo.academy.api.competition.HelpVideoDTO;

public class ProviderVideoAdapter extends ArrayDTOAdapterNew<HelpVideoDTO, ProviderVideoListItemView>
{
    //<editor-fold desc="Constructors">
    public ProviderVideoAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
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