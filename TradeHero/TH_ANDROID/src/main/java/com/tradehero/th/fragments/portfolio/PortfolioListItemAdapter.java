package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOWithinUserComparator;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.list.BaseListHeaderView;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class PortfolioListItemAdapter extends ArrayDTOAdapter<DisplayablePortfolioDTO, PortfolioListItemView>
{
    public static final String TAG = PortfolioListItemAdapter.class.getName();

    public static final int ITEM_TYPE_OWN_HEADER = 0;
    public static final int ITEM_TYPE_OWN = 1;
    public static final int ITEM_TYPE_HERO_HEADER = 2;
    public static final int ITEM_TYPE_HERO = 3;
    public static final int ITEM_TYPE_OTHER_HEADER = 4;
    public static final int ITEM_TYPE_OTHER = 5;

    private List<Integer> orderedTypes;
    private List<Object> orderedItems;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private final DisplayablePortfolioDTOWithinUserComparator ownDisplayablePortfolioDTOWithinUserComparator;
    private final int otherHeaderResId;

    public PortfolioListItemAdapter(Context context, LayoutInflater inflater, int portfolioLayoutResourceId, int otherHeaderResId)
    {
        super(context, inflater, portfolioLayoutResourceId);
        this.otherHeaderResId = otherHeaderResId;
        this.ownDisplayablePortfolioDTOWithinUserComparator = new DisplayablePortfolioDTOWithinUserComparator();
        orderedTypes = new ArrayList<>();
        orderedItems = new ArrayList<>();
        DaggerUtils.inject(this);
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public void setItems(List<DisplayablePortfolioDTO> items)
    {
        super.setItems(items);
        // Prepare the data for display
        List<Integer> preparedOrderedTypes = new ArrayList<>();
        List<Object> preparedOrderedItems = new ArrayList<>();

        if (items != null)
        {
            // TODO This could be improved
            // Here it is relying on the cache to be already filled to separate the heroes from the others.
            UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserId.toUserBaseKey());
            SortedSet<DisplayablePortfolioDTO> ownPortfolios = new TreeSet<>(this.ownDisplayablePortfolioDTOWithinUserComparator);
            Set<DisplayablePortfolioDTO> heroPortfolios = new HashSet<>();
            Set<DisplayablePortfolioDTO> otherPortfolios = new HashSet<>();

            for (DisplayablePortfolioDTO displayablePortfolioDTO: items)
            {
                if (displayablePortfolioDTO == null)
                {
                    // Do nothing
                }
                else if (currentUserProfile != null && currentUserProfile.isFollowingUser(displayablePortfolioDTO.userBaseDTO))
                {
                    heroPortfolios.add(displayablePortfolioDTO);
                }
                else if (currentUserId.toUserBaseKey().equals(displayablePortfolioDTO.ownedPortfolioId.getUserBaseKey()))
                {
                    ownPortfolios.add(displayablePortfolioDTO);
                }
                else
                {
                    otherPortfolios.add(displayablePortfolioDTO);
                }
            }

            preparedOrderedTypes.add(ITEM_TYPE_OWN_HEADER);
            preparedOrderedItems.add(R.string.portfolio_own_header);
            for (DisplayablePortfolioDTO displayablePortfolioDTO: ownPortfolios)
            {
                preparedOrderedTypes.add(ITEM_TYPE_OWN);
                preparedOrderedItems.add(displayablePortfolioDTO);
            }
            if (heroPortfolios.size() > 0)
            {
                preparedOrderedTypes.add(ITEM_TYPE_HERO_HEADER);
                preparedOrderedItems.add(R.string.portfolio_recently_viewed_heroes);
                for (DisplayablePortfolioDTO displayablePortfolioDTO: heroPortfolios)
                {
                    preparedOrderedTypes.add(ITEM_TYPE_HERO);
                    preparedOrderedItems.add(displayablePortfolioDTO);
                }
            }
            if (otherPortfolios.size() > 0)
            {
                preparedOrderedTypes.add(ITEM_TYPE_OTHER_HEADER);
                preparedOrderedItems.add(R.string.portfolio_recently_viewed_others);
                for (DisplayablePortfolioDTO displayablePortfolioDTO: otherPortfolios)
                {
                    preparedOrderedTypes.add(ITEM_TYPE_OTHER);
                    preparedOrderedItems.add(displayablePortfolioDTO);
                }
            }
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
        return 6;
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
        return ITEM_TYPE_OWN_HEADER;
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
        return R.string.portfolio_own_header;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        Object item = getItem(position);
        int itemType = getItemViewType(position);
        switch (itemType)
        {
            case ITEM_TYPE_OWN:
            case ITEM_TYPE_HERO:
            case ITEM_TYPE_OTHER:
                view = inflater.inflate(layoutResourceId, parent, false);
                if (item instanceof DisplayablePortfolioDTO)
                {
                    ((PortfolioListItemView) view).display((DisplayablePortfolioDTO) item);
                }
                else
                {
                    THLog.w(TAG, "type " + itemType + ", item not DisplayablePortfolioDTO " + item);
                }
                break;

            case ITEM_TYPE_OWN_HEADER:
            case ITEM_TYPE_HERO_HEADER:
            case ITEM_TYPE_OTHER_HEADER:
                view = inflater.inflate(otherHeaderResId, parent, false);
                if (item instanceof Integer)
                {
                    ((BaseListHeaderView) view).setHeaderTextContent(context.getString((int) item));
                }
                else
                {
                    THLog.w(TAG, "type " + itemType + ", item not Integer " + item);
                }
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
        return viewType != ITEM_TYPE_HERO_HEADER && viewType != ITEM_TYPE_OTHER_HEADER;
    }

    @Override protected void fineTune(int position, DisplayablePortfolioDTO dto, PortfolioListItemView dtoView)
    {
        // Nothing to do
    }
}
