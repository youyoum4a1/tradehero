package com.tradehero.th.fragments.settings.photo;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;

public class ChooseImageFromAdapter extends ArrayDTOAdapter<ChooseImageFromDTO, ChooseImageFromItemView>
{
    public ChooseImageFromAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, ChooseImageFromDTO dto,
            ChooseImageFromItemView dtoView)
    {
    }
}
