/**
 * SearchPeopleAdapter.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 10, 2013
 */
package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.widget.trending.TrendingUserView;
import java.util.List;

public class SearchPeopleAdapter extends ArrayAdapter<UserSearchResultDTO>
{
    private final static String TAG = SearchPeopleAdapter.class.getSimpleName();

    public SearchPeopleAdapter(Context context, List<UserSearchResultDTO> userList)
    {
        super(context, 0, userList);
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_people_item, null);
        }

        final TrendingUserView trendingUserView = ((TrendingUserView) convertView);
        trendingUserView.display(getItem(position));

        return convertView;
    }
}