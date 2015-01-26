
package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.th.adapters.PagedViewDTOAdapter;
import com.tradehero.th.api.users.UserSearchResultDTO;

public class PeopleItemViewAdapter extends PagedViewDTOAdapter<UserSearchResultDTO, SearchPeopleItemView>
{
    //<editor-fold desc="Constructors">
    public PeopleItemViewAdapter(
            @NonNull Context context,
            @LayoutRes int peopleItemLayoutResId)
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