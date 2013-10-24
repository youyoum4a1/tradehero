/**
 * PeopleItemViewAdapter.java
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 10, 2013
 */
package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.widget.trending.SearchPeopleItemView;

public class PeopleItemViewAdapter extends DTOAdapter<UserSearchResultDTO, SearchPeopleItemView>
{
    private final static String TAG = PeopleItemViewAdapter.class.getSimpleName();

    public PeopleItemViewAdapter(Context context, LayoutInflater inflater, int peopleItemLayoutResId)
    {
        super(context, inflater, peopleItemLayoutResId);
    }

    @Override protected void fineTune(int position, UserSearchResultDTO dto, SearchPeopleItemView dtoView)
    {
        // Nothing to do
    }
}