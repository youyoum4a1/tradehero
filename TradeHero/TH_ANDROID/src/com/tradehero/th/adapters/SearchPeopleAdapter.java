/**
 * SearchPeopleAdapter.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 10, 2013
 */
package com.tradehero.th.adapters;

import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.fragments.trending.TrendingUserView;
import com.tradehero.th.http.ImageLoader;
import com.tradehero.th.widget.trending.TrendingSecurityView;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import com.tradehero.th.R;
import com.tradehero.th.application.CircularImageView;
import com.tradehero.th.models.User;
import com.tradehero.th.utills.DateUtils;
import com.tradehero.th.utills.YUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class SearchPeopleAdapter extends ArrayAdapter<UserSearchResultDTO>
{

    //private final static String TAG = SearchPeopleAdapter.class.getSimpleName();
    private ImageLoader mImageLoader;

    public SearchPeopleAdapter(Context context, List<UserSearchResultDTO> userList)
    {
        super(context, 0, userList);
        mImageLoader = new ImageLoader(context);
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