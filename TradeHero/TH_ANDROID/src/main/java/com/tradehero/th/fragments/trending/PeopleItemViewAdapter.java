
package com.tradehero.th.fragments.trending;

import android.content.Context;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.users.UserSearchResultDTO;

public class PeopleItemViewAdapter extends PagedArrayDTOAdapterNew<UserSearchResultDTO, SearchPeopleItemView>
{
    //<editor-fold desc="Constructors">
    public PeopleItemViewAdapter(Context context, int peopleItemLayoutResId)
    {
        super(context, peopleItemLayoutResId);
    }
    //</editor-fold>

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).userId;
    }
}