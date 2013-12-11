package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import java.util.List;

public class SecurityItemViewAdapter extends DTOAdapter<SecurityId, SecurityItemView>
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

    @Override public long getItemId(int i)
    {
        return ((SecurityId) getItem(i)).hashCode();
    }
}