package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;

public class SecurityItemViewAdapterArray extends ArrayDTOAdapter<SecurityId, SecurityItemView>
{
    private final static String TAG = SecurityItemViewAdapterArray.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public SecurityItemViewAdapterArray(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }
    //</editor-fold>

    @Override protected void fineTune(int position, SecurityId securityCompactDTO, final SecurityItemView dtoView)
    {
        // Nothing to do
    }
}