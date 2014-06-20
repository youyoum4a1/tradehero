package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOWithinUserComparator;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.timeline.MainTimelineAdapter;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.inject.Inject;
import timber.log.Timber;

public class SimpleOwnPortfolioListItemAdapter extends ArrayDTOAdapter<DisplayablePortfolioDTO, PortfolioListItemView>
{
    private List<Object> orderedItems;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private final DisplayablePortfolioDTOWithinUserComparator ownDisplayablePortfolioDTOWithinUserComparator;

    public SimpleOwnPortfolioListItemAdapter(Context context, LayoutInflater inflater, int portfolioLayoutResourceId)
    {
        super(context, inflater, portfolioLayoutResourceId);
        this.ownDisplayablePortfolioDTOWithinUserComparator = new DisplayablePortfolioDTOWithinUserComparator();
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
        List<Object> preparedOrderedItems = new ArrayList<>();

        if (items != null)
        {
            // TODO This could be improved
            // Here it is relying on the cache to be already filled to separate the heroes from the others.
            UserProfileDTO currentUserProfile = userProfileCache.get().get(currentUserId.toUserBaseKey());
            SortedSet<DisplayablePortfolioDTO> ownPortfolios = new TreeSet<>(this.ownDisplayablePortfolioDTOWithinUserComparator);

            for (DisplayablePortfolioDTO displayablePortfolioDTO: items)
            {
                if (displayablePortfolioDTO != null)
                {
                    ownPortfolios.add(displayablePortfolioDTO);
                }
            }

            for (DisplayablePortfolioDTO displayablePortfolioDTO: ownPortfolios)
            {
                preparedOrderedItems.add(displayablePortfolioDTO);
            }
            this.orderedItems = preparedOrderedItems;
        }
    }

    @Override public int getCount()
    {
        int count = this.orderedItems.size(); // HACK because first item is not clickable
        return count == 0 ? 0 : count + 1;
    }

    @Override public int getViewTypeCount()
    {
        return 1;
    }

    @Override public int getItemViewType(int position)
    {
        return MainTimelineAdapter.PORTFOLIO_ITEM_TYPE;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public Object getItem(int position)
    {
        position = position == 0 ? position : position - 1; // HACK because first item is not clickable
        return this.orderedItems.get(position);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        if (position == 0) // HACK because first item is not clickable
        {
            view = new View(getContext());
        }
        else
        {
            Object item = getItem(position);
            int itemType = getItemViewType(position);
            switch (itemType)
            {
                case MainTimelineAdapter.PORTFOLIO_ITEM_TYPE:
                    view = inflater.inflate(layoutResourceId, parent, false);
                    if (item instanceof DisplayablePortfolioDTO)
                    {
                        ((PortfolioListItemView) view).display((DisplayablePortfolioDTO) item);
                    }
                    else
                    {
                        Timber.w("type %d, item not DisplayablePortfolioDTO %s", itemType, item);
                    }
                    break;

                default:
                    throw new UnsupportedOperationException("Not implemented"); // You should not use this method
            }
        }
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override protected void fineTune(int position, DisplayablePortfolioDTO dto, PortfolioListItemView dtoView)
    {
        // Nothing to do
    }
}
