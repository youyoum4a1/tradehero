package com.tradehero.th.fragments.social;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.users.UserProfileCompactDTO;

public class RelationsListItemAdapter extends ArrayDTOAdapter<UserProfileCompactDTO, RelationsListItemView>
{
    public RelationsListItemAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater, layoutResId);
    }

    @Override protected void fineTune(int position, UserProfileCompactDTO userProfileCompactDTO,
            RelationsListItemView relationsListItemView)
    {
    }
}
