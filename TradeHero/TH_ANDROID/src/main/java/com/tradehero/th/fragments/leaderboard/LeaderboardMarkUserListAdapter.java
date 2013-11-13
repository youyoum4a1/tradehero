package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ExpandableDTOAdapter;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:13 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserListAdapter extends
        //DTOAdapter<LeaderboardUserDTO, LeaderboardMarkUserItemView>
        ExpandableDTOAdapter<LeaderboardUserDTO, LeaderboardMarkUserListAdapter.ExpandableLeaderboardUserRankItemWrapper, LeaderboardMarkUserItemView>
{
    private LeaderboardMarkUserLoader loader;

    public LeaderboardMarkUserListAdapter(Context context, LayoutInflater layoutInflater, List<LeaderboardUserDTO> items, int layoutResourceId)
    {
        super(context, layoutInflater, layoutResourceId);

        setItems(ExpandableLeaderboardUserRankItemWrapper.wrap(items));
    }

    @Override public Object getItem(int position)
    {
        ExpandableLeaderboardUserRankItemWrapper dtoWrapper = (ExpandableLeaderboardUserRankItemWrapper) super.getItem(position);
        dtoWrapper.setPosition(position);
        dtoWrapper.setLeaderboardId(loader.getLeaderboardId());

        return dtoWrapper;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        return super.getView(position, convertView, viewGroup);
    }

    @Override protected void fineTune(int position, ExpandableLeaderboardUserRankItemWrapper dto, LeaderboardMarkUserItemView dtoView)
    {

    }

    public void setLoader(LeaderboardMarkUserLoader loader)
    {
        this.loader = loader;
        if (loader != null)
        {
            setUnderlyingItems(loader.getItems());
        }
    }

    @Override protected ExpandableLeaderboardUserRankItemWrapper wrap(LeaderboardUserDTO underlyingItem)
    {
        return new ExpandableLeaderboardUserRankItemWrapper(underlyingItem);
    }

    /**
     * Wrapper for LeaderboardUserDTO, keep expand state & position on the UI Board
     */
    public static class ExpandableLeaderboardUserRankItemWrapper extends ExpandableListItem<LeaderboardUserDTO>
    {
        private int position;
        private int leaderboardId;

        public ExpandableLeaderboardUserRankItemWrapper(LeaderboardUserDTO model)
        {
            super(model);
        }

        public static List<ExpandableLeaderboardUserRankItemWrapper> wrap(List<LeaderboardUserDTO> items)
        {
            if (items == null)
            {
                return null;
            }

            List<ExpandableLeaderboardUserRankItemWrapper> wrappedItems = new LinkedList<>();
            for (LeaderboardUserDTO item: items)
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

        public int getLeaderboardId()
        {
            return leaderboardId;
        }

        public void setLeaderboardId(int leaderboardId)
        {
            this.leaderboardId = leaderboardId;
        }
    }
}
