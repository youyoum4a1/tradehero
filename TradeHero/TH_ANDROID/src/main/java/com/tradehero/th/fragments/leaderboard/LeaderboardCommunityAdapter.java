package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.fragments.competition.LeaderboardCompetitionView;
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
import timber.log.Timber;

public class LeaderboardCommunityAdapter extends ArrayDTOAdapter<LeaderboardDefKey, LeaderboardDefView>
        implements StickyListHeadersAdapter
{
    private Map<LeaderboardCommunityType, List<LeaderboardDefKey>> items = new HashMap<>();
    private List<ProviderId> providerDTOs = new ArrayList<>();

    @Inject Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject Lazy<LeaderboardDefCache> leaderboardDefCache;

    private final int competitionCompactViewResourceId;

    public LeaderboardCommunityAdapter(Context context, LayoutInflater inflater,
            int leaderboardDefViewResourceId,
            int competitionCompactViewResourceId)
    {
        super(context, inflater, leaderboardDefViewResourceId);

        this.competitionCompactViewResourceId = competitionCompactViewResourceId;
        DaggerUtils.inject(this);
    }

    public void setCompetitionItems(List<ProviderId> providerDTOs)
    {
        this.providerDTOs = providerDTOs;
        notifyDataSetChanged();
    }

    @Override public void notifyDataSetChanged()
    {
        Map<LeaderboardCommunityType, List<LeaderboardDefKey>> typeMap = new HashMap<>();

        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
        {
            if (type.getKey() != null)
            {
                typeMap.put(type, leaderboardDefListCache.get().get(type.getKey()));
                Timber.d("notifyDataSetChanged map: type %s, value: %s", type,
                        leaderboardDefListCache.get().get(type.getKey()));
            }
            else
            {
                typeMap.put(type, new ArrayList<LeaderboardDefKey>());
            }
        }

        putExtraItems(typeMap);

        items = typeMap;
        super.notifyDataSetChanged();
    }

    // hardcoded stuffs +__+
    private void putExtraItems(Map<LeaderboardCommunityType, List<LeaderboardDefKey>> typeMap)
    {
        List<LeaderboardDefKey> heroAndFollower = typeMap.get(LeaderboardCommunityType.HeroAndFollower);
        if (heroAndFollower != null && heroAndFollower.size() < 3)
        {
            LeaderboardDefDTO fakeDto = new LeaderboardDefDTO();
            //add an entry 'myheros'
            fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_HERO_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_community_heros);

            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            heroAndFollower.add(0, fakeDto.getLeaderboardDefKey());

            //add an entry 'followers'
            fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_FOLLOWER_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_community_followers);

            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            heroAndFollower.add(1, fakeDto.getLeaderboardDefKey());
        }

        //TODO want delete friend by alex, is that right way?
        List<LeaderboardDefKey> skillAndFriend = typeMap.get(LeaderboardCommunityType.SkillAndFriend);

        if (skillAndFriend != null && skillAndFriend.size() < 3)
        {
            LeaderboardDefDTO fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_FRIEND_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_community_friends);
            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            skillAndFriend.add(fakeDto.getLeaderboardDefKey());
        }

        List<LeaderboardDefKey> sectorAndExchange = typeMap.get(LeaderboardCommunityType.SectorAndExchange);

        if (sectorAndExchange != null)
        {
            LeaderboardDefDTO fakeDto;

            fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_community_by_exchange);
            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            sectorAndExchange.add(fakeDto.getLeaderboardDefKey());

            fakeDto = new LeaderboardDefDTO();
            fakeDto.id = LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID;
            fakeDto.name = getContext().getString(R.string.leaderboard_community_by_sector);
            leaderboardDefCache.get().put(fakeDto.getLeaderboardDefKey(), fakeDto);
            sectorAndExchange.add(fakeDto.getLeaderboardDefKey());
        }
    }

    @Override public int getCount()
    {
        int totalItems = 0;
        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
        {
            if (items.get(type) != null)
            {
                totalItems += items.get(type).size();
                Timber.d("getCount type %s,size:%s", type, items.get(type).size());
            }
        }
        Timber.d("getCount CompetitionCount %s", getCompetitionCount());
        return getCompetitionCount() + totalItems;
    }

    public int getCompetitionCount()
    {
        return providerDTOs == null ? 0 : providerDTOs.size();
    }

    @Override public Object getItem(int position)
    {
        // first items of the list are for competition
        if (position < getCompetitionCount())
        {
            return providerDTOs.get(position);
        }

        position -= getCompetitionCount();
        // the rest are for Leaderboard definition
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
        if (getCompetitionCount() > position)
        {
            return LeaderboardCommunityType.Competition.ordinal();
        }
        position -= getCompetitionCount();

        for (LeaderboardCommunityType type : LeaderboardCommunityType.values())
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
        Object item = getItem(position);
        if (item instanceof LeaderboardDefKey)
        {
            return super.getView(position, convertView, viewGroup);
        }
        else if (item instanceof ProviderId)
        {
            LeaderboardCompetitionView competitionView = getCompetitionView(position, (LeaderboardCompetitionView) convertView, viewGroup);
            competitionView.display((ProviderId) item);
            return competitionView;
        }
        else
        {
            return null;
        }
    }

    @Override protected void fineTune(int position, LeaderboardDefKey dto, LeaderboardDefView dtoView)
    {
    }

    //<editor-fold desc="For headers">
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
    //</editor-fold>

    @SuppressWarnings("unchecked")
    public LeaderboardCompetitionView getCompetitionView(int position, LeaderboardCompetitionView convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = (LeaderboardCompetitionView) inflater.inflate(competitionCompactViewResourceId, parent, false);
        }
        return convertView;
    }

    public static enum LeaderboardCommunityType
    {
        Competition(null), // for competition
        HeroAndFollower(null), // for managing heroes & followers
        SkillAndFriend(LeaderboardDefListKey.getMostSkilled()),
        TimeRestricted(LeaderboardDefListKey.getTimePeriod()),
        SectorAndExchange(null); // all fake :v

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
