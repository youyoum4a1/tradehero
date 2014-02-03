package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefMostSkilledListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefTimePeriodListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/3/14 Time: 3:48 PM Copyright (c) TradeHero
 */
public class LeaderboardCommunityAdapter extends ArrayDTOAdapter<LeaderboardDefKey, LeaderboardDefView>
    implements StickyListHeadersAdapter
{
    private Map<LeaderboardCommunityType, List<LeaderboardDefKey>> items = new HashMap<>();
    @Inject Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject Lazy<LeaderboardDefCache> leaderboardDefCache;

    public LeaderboardCommunityAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        DaggerUtils.inject(this);
    }

    @Override public void notifyDataSetChanged()
    {
        Map<LeaderboardCommunityType, List<LeaderboardDefKey>> typeMap = new HashMap<>();

        for (LeaderboardCommunityType type: LeaderboardCommunityType.values())
        {
            if (type.getKey() != null)
            {
                typeMap.put(type, leaderboardDefListCache.get().get(type.getKey()));
            }
            else
            {
                typeMap.put(type, new ArrayList<LeaderboardDefKey>());
            }
        }

        // hardcoded stuffs +__+
        putExtraItems(typeMap);

        items = typeMap;
        super.notifyDataSetChanged();
    }

    private void putExtraItems(Map<LeaderboardCommunityType, List<LeaderboardDefKey>> typeMap)
    {
        List<LeaderboardDefKey> skillAndFriend = typeMap.get(LeaderboardCommunityType.SkillAndFriend);
        
        if (skillAndFriend != null)
        {
            LeaderboardDefDTO fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_FRIEND_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_friends);
            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);

            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            skillAndFriend.add(fakeDto.getLeaderboardDefKey());
        }

        List<LeaderboardDefKey> sectorAndExchange = typeMap.get(LeaderboardCommunityType.SectorAndExchange);

        if (sectorAndExchange != null)
        {
            LeaderboardDefDTO fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_by_sector);
            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            sectorAndExchange.add(fakeDto.getLeaderboardDefKey());

            fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_by_exchange);
            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            sectorAndExchange.add(fakeDto.getLeaderboardDefKey());
        }

    }

    @Override public int getCount()
    {
        int totalItems = 0;
        for (LeaderboardCommunityType type: LeaderboardCommunityType.values())
        {
            if (items.get(type) != null)
            {
                totalItems += items.get(type).size();
            }
        }
        return totalItems;
    }

    @Override public Object getItem(int position)
    {
        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
        {
            if (items.get(type) != null)
            {
                int currentSize = items.get(type).size();
                if (currentSize > position)
                {
                    return items.get(type).get(position);
                }
                else
                {
                    position -= currentSize;
                }
            }
        }
        return null;
    }

    @Override public int getItemViewType(int position)
    {
        for (LeaderboardCommunityType type: LeaderboardCommunityType.values())
        {
            if (items.get(type) != null)
            {
                int currentSize = items.get(type).size();
                if (currentSize > position)
                {
                    return type.ordinal();
                }
                else
                {
                    position -= currentSize;
                }
            }
        }
        return 0;
    }

    @Override public int getViewTypeCount()
    {
        return LeaderboardCommunityType.values().length;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        return super.getView(position, convertView, viewGroup);
    }

    @Override protected void fineTune(int position, LeaderboardDefKey dto, LeaderboardDefView dtoView)
    {

    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.leaderboard_separator, parent, false);
        }
        return convertView;
    }

    @Override public long getHeaderId(int position)
    {

        return getItemViewType(position);
    }

    public static enum LeaderboardCommunityType
    {
        SkillAndFriend(new LeaderboardDefMostSkilledListKey()),
        TimeRestricted(new LeaderboardDefTimePeriodListKey()),
        SectorAndExchange(null);// all fake :v

        private final LeaderboardDefListKey key;

        LeaderboardCommunityType(LeaderboardDefListKey leaderboardDefListKey)
        {
            this.key = leaderboardDefListKey;
        }

        public LeaderboardDefListKey getKey()
        {
            return key;
        }
    }
}
