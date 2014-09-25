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
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.fragments.competition.AdView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class CompetitionLeaderboardMarkUserListAdapter extends BaseAdapter
        implements WrapperListAdapter
{
    //private static final int EXTRA_TILE_FREQUENCY = 16;
    //private static final int EXTRA_TILE_MIN_DISTANCE = 10;
    private int extraTileFrequency = -1;
    private int extraTileStartrow = -1;

    private final LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;
    @NotNull private final ProviderDTO providerDTO;
    private final LayoutInflater inflater;
    private int[] masterTilesMarker;
    private int[] extraTilesMarker;
    private CompetitionLeaderboardDTO competitionLeaderboardDTO;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardMarkUserListAdapter(
            @NotNull Context context,
            @NotNull ProviderDTO providerDTO,
            LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter)
    {
        this.inflater = LayoutInflater.from(context);
        this.providerDTO = providerDTO;
        this.leaderboardMarkUserListAdapter = leaderboardMarkUserListAdapter;
    }
    //</editor-fold>

    public void setCompetitionLeaderboardDTO(CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {
        this.competitionLeaderboardDTO = competitionLeaderboardDTO;
    }

    @Override public void notifyDataSetChanged()
    {
        if (competitionLeaderboardDTO != null)
        {
            generateExtraTile();
        }
        super.notifyDataSetChanged();
    }

    private void initAdsContants()
    {
        if (competitionLeaderboardDTO != null)
        {
            extraTileStartrow = competitionLeaderboardDTO.adStartRow;
            extraTileFrequency = competitionLeaderboardDTO.adFrequencyRows;
            Timber.d("InitAdsContants: startrow = %d , frequency = %d", extraTileStartrow, extraTileFrequency);
        }
    }

    private void generateExtraTile()
    {
        initAdsContants();
        //int extraTileCount = Math.round(leaderboardMarkUserListAdapter.getCount() / EXTRA_TILE_FREQUENCY);
        int extraTileCount = Math.round((leaderboardMarkUserListAdapter.getCount() - extraTileStartrow) / extraTileFrequency);
        if (extraTileStartrow <= leaderboardMarkUserListAdapter.getCount())
        {
            extraTileCount += 1;
        }
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
        int[] extraTileIndexes = new int[extraTileCount];

        for (int i = 0; i < extraTileCount; ++i)
        {
            int newTileIndex = i * (extraTileFrequency + 1) + extraTileStartrow;
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
            adView.setProviderId(providerDTO.id);
            adView.display(competitionZoneAdvertisementDTO);
            return adView;
        }
        else
        {
            View view = leaderboardMarkUserListAdapter.getView(getWrappedPosition(position), convertView, parent);
            if (view instanceof CompetitionLeaderboardMarkUserItemView)
            {
                ((CompetitionLeaderboardMarkUserItemView) view).setProviderDTO(providerDTO);
                PrizeDTO prizeDTO = getPrizeDTO(getWrappedPosition(position));
                //There should allow prizeDTO is null,because it has logic,prizeDTO is null means should not show PrizeInfo in viewItem.
                ((CompetitionLeaderboardMarkUserItemView) view).setPrizeDTO(prizeDTO);
            }
            return view;
        }
    }

    public PrizeDTO getPrizeDTO(int position)
    {
        return competitionLeaderboardDTO == null ? null : competitionLeaderboardDTO.getPrizeAt(position);
    }
}
