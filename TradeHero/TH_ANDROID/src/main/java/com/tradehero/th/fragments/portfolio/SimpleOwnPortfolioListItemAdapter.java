package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOWithinUserComparator;
import com.tradehero.th.api.portfolio.DummyFxDisplayablePortfolioDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimpleOwnPortfolioListItemAdapter extends ArrayDTOAdapter<DisplayablePortfolioDTO, PortfolioListItemView>
{
    private static final int FIRST_POSITION_TYPE = 0;
    private static final int REGULAR_POSITION_TYPE = 1;

    private List<Object> orderedItems;
    private final DisplayablePortfolioDTOWithinUserComparator ownDisplayablePortfolioDTOWithinUserComparator;
    private final boolean isCurrentUser;

    //<editor-fold desc="Constructors">
    public SimpleOwnPortfolioListItemAdapter(
            @NonNull Context context,
            @LayoutRes int portfolioLayoutResourceId,
            boolean isCurrentUser)
    {
        super(context, portfolioLayoutResourceId);
        this.isCurrentUser = isCurrentUser;
        this.ownDisplayablePortfolioDTOWithinUserComparator = new DisplayablePortfolioDTOWithinUserComparator();
        orderedItems = new ArrayList<>();
    }
    //</editor-fold>

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public void setItems(@NonNull List<DisplayablePortfolioDTO> items)
    {
        super.setItems(items);
        // Prepare the data for display
        List<Object> preparedOrderedItems = new ArrayList<>();

        if (items != null)
        {
            // TODO This could be improved
            SortedSet<DisplayablePortfolioDTO> ownPortfolios = new TreeSet<>(this.ownDisplayablePortfolioDTOWithinUserComparator);

            for (DisplayablePortfolioDTO displayablePortfolioDTO : items)
            {
                if (displayablePortfolioDTO != null)
                {
                    ownPortfolios.add(displayablePortfolioDTO);
                }
            }

            if (isCurrentUser)
            {
                Boolean containsFx = containsMainFx(items);
                if (containsFx != null && !containsFx)
                {
                    ownPortfolios.add(new DummyFxDisplayablePortfolioDTO());
                }
            }

            for (DisplayablePortfolioDTO displayablePortfolioDTO : ownPortfolios)
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
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return position == 0 ? FIRST_POSITION_TYPE : REGULAR_POSITION_TYPE;
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
        switch (getItemViewType(position))
        {
            case FIRST_POSITION_TYPE:
                if (convertView == null)
                {
                    convertView = new View(getContext());
                }
                break;

            case REGULAR_POSITION_TYPE:
                Object item = getItem(position);
                convertView = conditionalInflate(position, convertView, parent);
                ((PortfolioListItemView) convertView).display((DisplayablePortfolioDTO) item);
                break;

            default:
                throw new UnsupportedOperationException("Not implemented");
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Nullable protected Boolean containsMainFx(@NonNull List<DisplayablePortfolioDTO> items)
    {
        boolean value = false;
        for (DisplayablePortfolioDTO displayablePortfolio : items)
        {
            if (displayablePortfolio == null
                    || displayablePortfolio.portfolioDTO == null)
            {
                return null;
            }
            if (displayablePortfolio.portfolioDTO.isFx() && displayablePortfolio.portfolioDTO.isDefault())
            {
                value = true;
            }
        }
        return value;
    }
}
