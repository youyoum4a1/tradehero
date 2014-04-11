package com.tradehero.th.fragments.social;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.users.UserBaseDTO;

/**
 * Created by alex on 14-4-11.
 */
public class RelationsListItemAdapter extends ArrayDTOAdapter<UserBaseDTO, RelationsListItemView>
{
    public RelationsListItemAdapter(Context context, LayoutInflater inflater, int layoutResId)
    {
        super(context, inflater, layoutResId);
    }

    @Override protected void fineTune(int position, UserBaseDTO userBaseDTO,
            RelationsListItemView relationsListItemView)
    {
    }
}
