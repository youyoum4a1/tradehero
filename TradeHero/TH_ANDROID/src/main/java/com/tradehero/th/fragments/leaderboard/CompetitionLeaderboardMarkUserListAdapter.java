package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.fragments.competition.AdView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import java.util.Arrays;

public class CompetitionLeaderboardMarkUserListAdapter extends BaseAdapter
    implements WrapperListAdapter
{
    private static final int EXTRA_TILE_FREQUENCY = 16;
    private static final int EXTRA_TILE_MIN_DISTANCE = 10;

    private final Context context;
    private final LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;
    private final ProviderDTO providerDTO;
    private final LayoutInflater inflater;
    private int[] masterTilesMarker;
    private int[] extraTilesMarker;

    public CompetitionLeaderboardMarkUserListAdapter(Context context, ProviderDTO providerDTO, LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.providerDTO = providerDTO;
        this.leaderboardMarkUserListAdapter = leaderboardMarkUserListAdapter;
        if (leaderboardMarkUserListAdapter != null && leaderboardMarkUserListAdapter.getCount() > 0)
        {
            generateExtraTile();
        }
    }

    @Override public void notifyDataSetChanged()
    {
        generateExtraTile();
        super.notifyDataSetChanged();
    }

    private void generateExtraTile()
    {
        int extraTileCount = Math.round(leaderboardMarkUserListAdapter.getCount() / EXTRA_TILE_FREQUENCY);

        if (extraTileCount > 0)
        {
            int[] tempMarker;
            if (masterTilesMarker != null && extraTileCount < masterTilesMarker.length)
            {
                tempMarker = Arrays.copyOf(masterTilesMarker, extraTileCount);
            }
            else
            {
                tempMarker = generateAdsTile(extraTileCount);
                if (masterTilesMarker != null)
                {
                    System.arraycopy(masterTilesMarker, 0, tempMarker, 0, masterTilesMarker.length);
                }
                masterTilesMarker = tempMarker;
            }
            extraTilesMarker = tempMarker;
        }
    }

    private int[] generateAdsTile(int extraTileCount)
    {
        int maxTileIndex = leaderboardMarkUserListAdapter.getCount() + extraTileCount - 1;
        int previousIndex = -1;
        int[] extraTileIndexes = new int[extraTileCount];

        for (int i = 0; i < extraTileCount; ++i)
        {
            int newTileIndex = i * EXTRA_TILE_FREQUENCY + (int) (Math.random() * EXTRA_TILE_FREQUENCY);
            if (previousIndex > 0 && (newTileIndex - previousIndex < EXTRA_TILE_MIN_DISTANCE))
            {
                newTileIndex = previousIndex + EXTRA_TILE_MIN_DISTANCE;
            }
            // side effect of previous tiles insertion, also there should not be any overlapping between 2 tiles random space
            newTileIndex += i % EXTRA_TILE_MIN_DISTANCE;
            newTileIndex = Math.min(maxTileIndex, newTileIndex);
            previousIndex = newTileIndex;
            extraTileIndexes[i] = newTileIndex;
        }

        return extraTileIndexes;
    }

    private int getExtraTileCount()
    {
        return extraTilesMarker != null ? extraTilesMarker.length : 0;
    }

    private int getWrappedPosition(int position)
    {
        if (extraTilesMarker != null)
        {
            for (int i = 0; i < extraTilesMarker.length; ++i)
            {
                if (position == extraTilesMarker[i])
                {
                    return -1;
                }
                else if (position < extraTilesMarker[i])
                {
                    return position - i;
                }
            }

            return position - extraTilesMarker.length;
        }
        else
        {
            return position;
        }
    }

    @Override public ListAdapter getWrappedAdapter()
    {
        return leaderboardMarkUserListAdapter;
    }

    @Override public int getCount()
    {
        return leaderboardMarkUserListAdapter.getCount() + getExtraTileCount();
    }

    @Override public Object getItem(int position)
    {
        int wrappedPosition = getWrappedPosition(position);
        if (wrappedPosition >= 0)
        {
            return leaderboardMarkUserListAdapter.getItem(wrappedPosition);
        }
        else if (providerDTO.hasAdvertisement())
        {
            int randomAds = (int) (Math.random() * providerDTO.advertisements.size());
            return providerDTO.advertisements.get(randomAds);
        }
        return null;
    }

    @Override public long getItemId(int position)
    {
        int wrappedPosition = getWrappedPosition(position);
        if (wrappedPosition >= 0)
        {
            return leaderboardMarkUserListAdapter.getItemId(wrappedPosition);
        }
        else
        {
            return position;
        }
    }

    @Override public int getItemViewType(int position)
    {
        int wrappedPosition = getWrappedPosition(position);
        if (wrappedPosition >= 0)
        {
            return leaderboardMarkUserListAdapter.getItemViewType(wrappedPosition);
        }
        else
        {
            return leaderboardMarkUserListAdapter.getViewTypeCount();
        }
    }

    @Override public int getViewTypeCount()
    {
        return leaderboardMarkUserListAdapter.getViewTypeCount() + 1;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        Object item = getItem(position);
        if (item instanceof AdDTO)
        {
            CompetitionZoneAdvertisementDTO competitionZoneAdvertisementDTO = new CompetitionZoneAdvertisementDTO(null, null, 0, (AdDTO) item);
            AdView adView = (AdView) inflater.inflate(R.layout.competition_zone_ads, parent, false);
            adView.display(competitionZoneAdvertisementDTO);
            return adView;
        }
        else
        {
            View view = leaderboardMarkUserListAdapter.getView(getWrappedPosition(position), convertView, parent);
            if (view instanceof CompetitionLeaderboardMarkUserItemView)
            {
                ((CompetitionLeaderboardMarkUserItemView) view).setProviderDTO(providerDTO);
            }
            return view;
        }
    }
}
