package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class SecurityItemViewAdapter extends DTOAdapter<SecurityCompactDTO, SecurityItemView>
{
    private final static String TAG = SecurityItemViewAdapter.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, SecurityCompactDTO securityCompactDTO, final SecurityItemView dtoView)
    {
        // Nothing to do
    }
}