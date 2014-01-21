package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.security.SecurityItemView;

public class SecurityItemViewAdapter extends ArrayDTOAdapter<SecurityId, SecurityItemView>
{
    private final static String TAG = SecurityItemViewAdapter.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, SecurityId securityId, final SecurityItemView dtoView)
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
        return item == null ? 0 : ((SecurityId) item).hashCode();
    }
}