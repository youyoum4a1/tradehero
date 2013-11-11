package com.tradehero.th.adapters.social;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.widget.portfolio.PortfolioListHeaderView;
import com.tradehero.th.widget.portfolio.PortfolioListItemView;
import com.tradehero.th.widget.social.FollowerListHeaderView;
import com.tradehero.th.widget.social.FollowerListItemView;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class FollowerListItemAdapter extends DTOAdapter<UserFollowerDTO, FollowerListItemView>
{
    public static final String TAG = FollowerListItemAdapter.class.getName();

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    public static final int POSITION_HEADER = 0;
    public static final int SUPER_POSITION_OFFSET = 1;

    private final int headerResId;

    public FollowerListItemAdapter(Context context, LayoutInflater inflater, int followerLayoutResourceId, int headerResId)
    {
        super(context, inflater, followerLayoutResourceId);
        this.headerResId = headerResId;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return position == POSITION_HEADER ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override public int getCount()
    {
        return super.getCount() + 1;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public Object getItem(int position)
    {
        return getItemViewType(position) == VIEW_TYPE_HEADER ? "header" : super.getItem(position - SUPER_POSITION_OFFSET);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (getItemViewType(position) == VIEW_TYPE_HEADER)
        {
            if (!(convertView instanceof FollowerListHeaderView))
            {
                convertView = inflater.inflate(headerResId, parent, false);
            }
            ((FollowerListHeaderView) convertView).setHeaderTextContent(context.getString(R.string.manage_followers_list_header));
        }
        else
        {
            if (!(convertView instanceof FollowerListItemView))
            {
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }
            ((FollowerListItemView) convertView).display((UserFollowerDTO) getItem(position));
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItemViewType(position) != VIEW_TYPE_HEADER;
    }

    @Override protected void fineTune(int position, UserFollowerDTO dto, FollowerListItemView dtoView)
    {
        // Nothing to do
    }
}
