package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDisplayCellDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLegalMentionsView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTOUtil;
import com.tradehero.th.inject.HierarchyInjector;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CompetitionZoneListItemAdapter extends DTOAdapterNew<CompetitionZoneDTO>
{
    public static final int ITEM_TYPE_ADS = 0;
    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_PORTFOLIO = 2;
    public static final int ITEM_TYPE_ZONE_ITEM = 3;
    public static final int ITEM_TYPE_LEADERBOARD = 4;
    public static final int ITEM_TYPE_LEGAL_MENTIONS = 5;
    public static final int ITEM_TYPE_LOADING = 6;

    @NonNull private final Integer[] viewTypeToResId;
    private List<Integer> orderedTypes;
    private List<CompetitionZoneDTO> orderedItems;

    @Inject protected CompetitionZoneDTOUtil competitionZoneDTOUtil;
    private CompetitionZoneLegalMentionsView.OnElementClickedListener parentOnLegalElementClicked;

    private UserProfileCompactDTO portfolioUserProfileCompactDTO;
    private ProviderDTO providerDTO;
    private List<CompetitionDTO> competitionDTOs;
    private List<ProviderDisplayCellDTO> providerDisplayCellDTOs;

    //<editor-fold desc="Constructors">
    public CompetitionZoneListItemAdapter(
            @NonNull Context context,
            int zoneItemLayoutResId,
            int adsResId,
            int headerResId,
            int portfolioResId,
            int leaderboardResId,
            int legalResId)
    {
        super(context, zoneItemLayoutResId);

        this.viewTypeToResId = new Integer[7];
        this.viewTypeToResId[ITEM_TYPE_ADS] = adsResId;
        this.viewTypeToResId[ITEM_TYPE_HEADER] = headerResId;
        this.viewTypeToResId[ITEM_TYPE_PORTFOLIO] = portfolioResId;
        this.viewTypeToResId[ITEM_TYPE_ZONE_ITEM] = layoutResourceId;
        this.viewTypeToResId[ITEM_TYPE_LEADERBOARD] = leaderboardResId;
        this.viewTypeToResId[ITEM_TYPE_LEGAL_MENTIONS] = legalResId;
        this.viewTypeToResId[ITEM_TYPE_LOADING] = R.layout.loading_item;

        orderedTypes = new ArrayList<>();
        orderedItems = new ArrayList<>();
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public boolean hasStableIds()
    {
        return true;
    }

    public void setPortfolioUserProfileCompactDTO(UserProfileCompactDTO portfolioUserProfileCompactDTO)
    {
        this.portfolioUserProfileCompactDTO = portfolioUserProfileCompactDTO;
    }

    public void setProvider(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
    }

    public void setCompetitionDTOs(List<CompetitionDTO> competitionDTOs)
    {
        this.competitionDTOs = competitionDTOs;
    }

    public void setDisplayCellDTOS(@Nullable List<ProviderDisplayCellDTO> providerDisplayCellDTOList)
    {
        this.providerDisplayCellDTOs = providerDisplayCellDTOList;
    }

    @Override public void notifyDataSetChanged()
    {
        repopulateLists();
        super.notifyDataSetChanged();
    }

    private void repopulateLists()
    {
        if (providerDTO != null)
        {
            List<Integer> preparedOrderedTypes = new ArrayList<>();
            List<CompetitionZoneDTO> preparedOrderedItems = new ArrayList<>();

            this.competitionZoneDTOUtil.populateLists(
                    getContext(),
                    portfolioUserProfileCompactDTO,
                    providerDTO,
                    competitionDTOs,
                    providerDisplayCellDTOs,
                    preparedOrderedTypes,
                    preparedOrderedItems);

            this.orderedTypes = preparedOrderedTypes;
            this.orderedItems = preparedOrderedItems;
        }
    }

    @Override public int getCount()
    {
        return this.orderedTypes.size();
    }

    @Override public int getViewTypeCount()
    {
        return this.viewTypeToResId.length;
    }

    @Override public int getItemViewType(int position)
    {
        List<Integer> orderedTypesCopy = this.orderedTypes;
        int size = orderedTypesCopy.size();
        if (position < size)
        {
            return orderedTypesCopy.get(position);
        }
        if (size > 0)
        {
            return orderedTypesCopy.get(size - 1);
        }
        return ITEM_TYPE_PORTFOLIO;
    }

    @Override @LayoutRes public int getViewResId(int position)
    {
        return this.viewTypeToResId[getItemViewType(position)];
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item != null ? item.hashCode() : position;
    }

    @Override public CompetitionZoneDTO getItem(int position)
    {
        List<CompetitionZoneDTO> orderedItemsCopy = this.orderedItems;
        int size = orderedItemsCopy.size();
        if (position < size)
        {
            return orderedItemsCopy.get(position);
        }
        if (size > 0)
        {
            return orderedItemsCopy.get(size - 1);
        }
        return new CompetitionZoneDTO(null, null);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        switch (getItemViewType(position))
        {
            case ITEM_TYPE_LEGAL_MENTIONS:
                ((CompetitionZoneLegalMentionsView) view).setOnElementClickedListener(this.parentOnLegalElementClicked);
                break;
        }
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        int viewType = getItemViewType(position);
        return viewType != ITEM_TYPE_HEADER &&
                viewType != ITEM_TYPE_LEGAL_MENTIONS;
    }

    public void setParentOnLegalElementClicked(CompetitionZoneLegalMentionsView.OnElementClickedListener parentOnLegalElementClicked)
    {
        this.parentOnLegalElementClicked = parentOnLegalElementClicked;
    }
}
