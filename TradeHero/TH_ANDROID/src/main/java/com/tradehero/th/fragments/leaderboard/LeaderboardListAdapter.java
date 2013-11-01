package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ExpandableDTOAdapter;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:13 PM Copyright (c) TradeHero */
public class LeaderboardListAdapter extends
        //DTOAdapter<LeaderboardUserRankDTO, LeaderboardUserRankItemView>
        ExpandableDTOAdapter<LeaderboardUserRankDTO, LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper, LeaderboardUserRankItemView>
{
    private LeaderboardLoader loader;

    public LeaderboardListAdapter(Context context, LayoutInflater layoutInflater, List<LeaderboardUserRankDTO> items, int layoutResourceId)
    {
        super(context, layoutInflater, layoutResourceId);

        setItems(ExpandableLeaderboardUserRankItemWrapper.wrap(items));
    }

    @Override public Object getItem(int position)
    {
        ExpandableLeaderboardUserRankItemWrapper dtoWrapper = (ExpandableLeaderboardUserRankItemWrapper) super.getItem(position);
        dtoWrapper.setPosition(position);
        return dtoWrapper;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        return super.getView(position, convertView, viewGroup);
    }

    @Override protected void fineTune(int position, ExpandableLeaderboardUserRankItemWrapper dto, LeaderboardUserRankItemView dtoView)
    {

    }

    public void setLoader(LeaderboardLoader loader)
    {
        this.loader = loader;
        if (loader != null)
        {
            setUnderlyingItems(loader.getItems());
        }
    }

    @Override protected ExpandableLeaderboardUserRankItemWrapper wrap(LeaderboardUserRankDTO underlyingItem)
    {
        return new ExpandableLeaderboardUserRankItemWrapper(underlyingItem);
    }

    /**
     * Wrapper for LeaderboardUserRankDTO, keep expand state & position on the UI Board
     */
    public static class ExpandableLeaderboardUserRankItemWrapper extends ExpandableListItem<LeaderboardUserRankDTO>
    {
        private int position;

        public ExpandableLeaderboardUserRankItemWrapper(LeaderboardUserRankDTO model)
        {
            super(model);
        }

        public static List<ExpandableLeaderboardUserRankItemWrapper> wrap(List<LeaderboardUserRankDTO> items)
        {
            if (items == null)
            {
                return null;
            }

            List<ExpandableLeaderboardUserRankItemWrapper> wrappedItems = new LinkedList<>();
            for (LeaderboardUserRankDTO item: items)
            {
                wrappedItems.add(new ExpandableLeaderboardUserRankItemWrapper(item));
            }
            return wrappedItems;
        }

        public int getPosition()
        {
            return position;
        }

        public void setPosition(int position)
        {
            this.position = position;
        }
    }
}
