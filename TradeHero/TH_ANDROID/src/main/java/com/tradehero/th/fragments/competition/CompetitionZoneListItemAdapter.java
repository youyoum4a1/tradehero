package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.fragments.competition.zone.AbstractCompetitionZoneListItemView;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTOUtil;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLegalMentionsView;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneListItemView;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.list.BaseListHeaderView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class CompetitionZoneListItemAdapter extends ArrayDTOAdapter<CompetitionZoneDTO, CompetitionZoneListItemView>
{
    public static final String TAG = CompetitionZoneListItemAdapter.class.getName();

    public static final int ITEM_TYPE_TRADE_NOW = 0;
    public static final int ITEM_TYPE_HEADER = 1;
    public static final int ITEM_TYPE_ZONE_ITEM = 2;
    public static final int ITEM_TYPE_LEADERBOARD = 3;
    public static final int ITEM_TYPE_LEGAL_MENTIONS = 4;

    private List<Integer> orderedTypes;
    private List<Object> orderedItems;

    private final Context context;
    private final int tradeNowResId;
    private final int headerResId;
    private final int leaderboardResId;
    private final int legalResId;
    @Inject protected CompetitionZoneDTOUtil competitionZoneDTOUtil;
    private CompetitionZoneLegalMentionsView.OnElementClickedListener parentOnLegalElementClicked;

    private ProviderDTO providerDTO;
    private List<CompetitionDTO> competitionDTOs;

    public CompetitionZoneListItemAdapter(Context context, LayoutInflater inflater, int zoneItemLayoutResId, int tradeNowResId, int headerResId,
            int leaderboardResId, int legalResId)
    {
        super(context, inflater, zoneItemLayoutResId);
        this.context = context;
        this.tradeNowResId = tradeNowResId;
        this.headerResId = headerResId;
        this.leaderboardResId = leaderboardResId;
        this.legalResId = legalResId;
        orderedTypes = new ArrayList<>();
        orderedItems = new ArrayList<>();
        DaggerUtils.inject(this);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    public void setProvider(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        repopulateLists();
    }

    public void setCompetitionDTOs(List<CompetitionDTO> competitionDTOs)
    {
        this.competitionDTOs = competitionDTOs;
        repopulateLists();
    }

    private void repopulateLists()
    {
        if (providerDTO != null)
        {
            List<Integer> preparedOrderedTypes = new ArrayList<>();
            List<Object> preparedOrderedItems = new ArrayList<>();

            this.competitionZoneDTOUtil.populateLists(context, providerDTO, competitionDTOs, preparedOrderedTypes, preparedOrderedItems);

            this.orderedTypes = preparedOrderedTypes;
            this.orderedItems = preparedOrderedItems;
        }
    }

    @Override public void setItems(List<CompetitionZoneDTO> items)
    {
        throw new RuntimeException();
    }

    @Override public int getCount()
    {
        return this.orderedTypes.size();
    }

    @Override public int getViewTypeCount()
    {
        return 5;
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
        return ITEM_TYPE_TRADE_NOW;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public Object getItem(int position)
    {
        List<Object> orderedItemsCopy = this.orderedItems;
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
        View view;
        Object item = getItem(position);
        int itemType = getItemViewType(position);
        THLog.d(TAG, "getView " + item);
        switch (itemType)
        {
            case ITEM_TYPE_TRADE_NOW:
                view = inflater.inflate(tradeNowResId, parent, false);
                ((AbstractCompetitionZoneListItemView) view).display((CompetitionZoneDTO) item);
                break;

            case ITEM_TYPE_HEADER:
                view = inflater.inflate(headerResId, parent, false);
                ((BaseListHeaderView) view).setHeaderTextContent(((CompetitionZoneDTO) item).title);
                break;

            case ITEM_TYPE_ZONE_ITEM:
                view = inflater.inflate(layoutResourceId, parent, false);
                ((AbstractCompetitionZoneListItemView) view).display((CompetitionZoneDTO) item);
                break;

            case ITEM_TYPE_LEADERBOARD:
                view = inflater.inflate(leaderboardResId, parent, false);
                ((AbstractCompetitionZoneListItemView) view).display((CompetitionZoneDTO) item);
                break;

            case ITEM_TYPE_LEGAL_MENTIONS:
                view = inflater.inflate(legalResId, parent, false);
                ((AbstractCompetitionZoneListItemView) view).display((CompetitionZoneDTO) item);
                ((CompetitionZoneLegalMentionsView) view).setOnElementClickedListener(this.parentOnLegalElementClicked);
                break;

            default:
                throw new UnsupportedOperationException("Not implemented"); // You should not use this method
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
        return viewType == ITEM_TYPE_ZONE_ITEM || viewType == ITEM_TYPE_TRADE_NOW || viewType == ITEM_TYPE_LEADERBOARD;
    }

    @Override protected void fineTune(int position, CompetitionZoneDTO dto, CompetitionZoneListItemView dtoView)
    {
        // Nothing to do
    }

    public void setParentOnLegalElementClicked(CompetitionZoneLegalMentionsView.OnElementClickedListener parentOnLegalElementClicked)
    {
        this.parentOnLegalElementClicked = parentOnLegalElementClicked;
    }
}
