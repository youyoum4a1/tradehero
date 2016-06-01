package com.ayondo.academy.fragments.portfolio;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.GenericArrayAdapter;
import com.ayondo.academy.api.portfolio.DisplayablePortfolioDTO;
import com.ayondo.academy.api.portfolio.DisplayablePortfolioDTOWithinUserComparator;
import com.ayondo.academy.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.ayondo.academy.fragments.timeline.TimelineFragment;
import com.ayondo.academy.fragments.timeline.TimelineHeaderButtonView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import rx.Observable;
import rx.subjects.PublishSubject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SimpleOwnPortfolioListItemAdapter extends GenericArrayAdapter<Object>
        implements StickyListHeadersAdapter
{
    private static final int VIEW_TYPE_FIRST_POSITION = 0;
    private static final int VIEW_TYPE_REGULAR_POSITION = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private static final int VIEW_TYPE_SPACING = 3;

    public static final String DTO_LOADING = "Loading";
    public static final String DTO_SPACING = "Spacing";

    private List<Object> orderedItems;
    private final DisplayablePortfolioDTOWithinUserComparator ownDisplayablePortfolioDTOWithinUserComparator;
    private final boolean isCurrentUser;
    @LayoutRes private final int loadingLayoutRes;
    @LayoutRes private final int spacingLayoutRes;
    @NonNull private final PublishSubject<TimelineFragment.TabType> tabTypeSubject;

    @NonNull private TimelineFragment.TabType currentTabType = TimelineFragment.TabType.TIMELINE;

    //<editor-fold desc="Constructors">
    public SimpleOwnPortfolioListItemAdapter(
            @NonNull Context context,
            boolean isCurrentUser,
            @LayoutRes int portfolioLayoutResourceId,
            @LayoutRes int loadingLayoutRes,
            @LayoutRes int spacingLayoutRes)
    {
        super(context, portfolioLayoutResourceId);
        this.isCurrentUser = isCurrentUser;
        this.loadingLayoutRes = loadingLayoutRes;
        this.spacingLayoutRes = spacingLayoutRes;
        this.ownDisplayablePortfolioDTOWithinUserComparator = new DisplayablePortfolioDTOWithinUserComparator();
        this.tabTypeSubject = PublishSubject.create();
        orderedItems = new ArrayList<>();
    }
    //</editor-fold>

    @NonNull public Observable<TimelineFragment.TabType> getTabTypeObservable()
    {
        return tabTypeSubject.asObservable();
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public void setItems(@NonNull List<Object> items)
    {
        super.setItems(items);
        // Prepare the data for display
        List<Object> preparedOrderedItems = new ArrayList<>();

        // TODO This could be improved
        SortedSet<DisplayablePortfolioDTO> ownPortfolios = new TreeSet<>(this.ownDisplayablePortfolioDTOWithinUserComparator);

        for (Object item : items)
        {
            if (item instanceof DisplayablePortfolioDTO)
            {
                ownPortfolios.add((DisplayablePortfolioDTO) item);
            }
        }

        if (ownPortfolios.size() > 0)
        {
            if (isCurrentUser)
            {
                Boolean containsFx = containsMainFx(ownPortfolios);
                if (containsFx != null && !containsFx)
                {
                    ownPortfolios.add(new DummyFxDisplayablePortfolioDTO());
                }
            }
            boolean hadCompetition = false;
            for (DisplayablePortfolioDTO displayablePortfolioDTO : ownPortfolios)
            {
                if (displayablePortfolioDTO.portfolioDTO != null)
                {
                    if (displayablePortfolioDTO.portfolioDTO.isWatchlist)
                    {
                        preparedOrderedItems.add(DTO_SPACING);
                    }
                    else if (displayablePortfolioDTO.portfolioDTO.providerId != null && !hadCompetition)
                    {
                        hadCompetition = true;
                        preparedOrderedItems.add(DTO_SPACING);
                    }
                }
                preparedOrderedItems.add(displayablePortfolioDTO);
            }
        }
        else
        {
            preparedOrderedItems.addAll(items);
        }

        this.orderedItems = preparedOrderedItems;
    }

    @Override public int getCount()
    {
        int count = this.orderedItems.size(); // HACK because first item is not clickable
        return count == 0 ? 0 : count + 1;
    }

    @Override public int getViewTypeCount()
    {
        return 4;
    }

    @Override public int getItemViewType(int position)
    {
        if (position == 0)
        {
            return VIEW_TYPE_FIRST_POSITION;
        }
        Object item = getItem(position);
        if (item.equals(DTO_LOADING))
        {
            return VIEW_TYPE_LOADING;
        }
        if (item.equals(DTO_SPACING))
        {
            return VIEW_TYPE_SPACING;
        }

        return VIEW_TYPE_REGULAR_POSITION;
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
            case VIEW_TYPE_FIRST_POSITION:
                if (convertView == null)
                {
                    convertView = new View(getContext());
                }
                break;

            case VIEW_TYPE_REGULAR_POSITION:
                Object item = getItem(position);
                convertView = conditionalInflate(position, convertView, parent);
                ((PortfolioListItemView) convertView).display((DisplayablePortfolioDTO) item);
                break;

            case VIEW_TYPE_LOADING:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(loadingLayoutRes, parent, false);
                }
                break;

            case VIEW_TYPE_SPACING:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(spacingLayoutRes, parent, false);
                }
                break;

            default:
                throw new UnsupportedOperationException("Not implemented");
        }
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        int viewType = getItemViewType(position);
        return viewType != VIEW_TYPE_LOADING
                && viewType != VIEW_TYPE_SPACING;
    }

    @Nullable protected Boolean containsMainFx(@NonNull Collection<DisplayablePortfolioDTO> items)
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

    public void setCurrentTabType(@NonNull TimelineFragment.TabType currentTabType)
    {
        this.currentTabType = currentTabType;
    }

    @Override public View getHeaderView(int i, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_detail_bottom_buttons, viewGroup, false);
            ((TimelineHeaderButtonView) convertView).getTabTypeObservable().subscribe(tabTypeSubject);
        }
        ((TimelineHeaderButtonView) convertView).setActive(currentTabType);
        return convertView;
    }

    @Override public long getHeaderId(int i)
    {
        return 0;
    }
}
