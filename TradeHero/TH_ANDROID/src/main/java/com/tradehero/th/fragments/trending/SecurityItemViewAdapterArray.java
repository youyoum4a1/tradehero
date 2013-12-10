package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class SecurityItemViewAdapterArray extends ArrayDTOAdapter<SecurityCompactDTO, SecurityItemView>
{
    private final static String TAG = SecurityItemViewAdapterArray.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapterArray(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, SecurityCompactDTO securityCompactDTO, final SecurityItemView dtoView)
    {
        // Nothing to do
    }
}