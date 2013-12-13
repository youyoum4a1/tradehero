/**
 * PeopleItemViewAdapter.java
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 10, 2013
 */
package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.users.UserBaseKey;

public class PeopleItemViewAdapter extends ArrayDTOAdapter<UserBaseKey, SearchPeopleItemView>
{
    private final static String TAG = PeopleItemViewAdapter.class.getSimpleName();

    public PeopleItemViewAdapter(Context context, LayoutInflater inflater, int peopleItemLayoutResId)
    {
        super(context, inflater, peopleItemLayoutResId);
    }

    @Override protected void fineTune(int position, UserBaseKey dto, SearchPeopleItemView dtoView)
    {
        // Nothing to do
    }
}