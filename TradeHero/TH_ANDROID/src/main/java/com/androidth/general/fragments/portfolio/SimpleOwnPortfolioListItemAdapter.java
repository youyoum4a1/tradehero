package com.androidth.general.fragments.portfolio;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.adapters.GenericArrayAdapter;
import com.androidth.general.api.portfolio.DisplayablePortfolioDTO;
import com.androidth.general.api.portfolio.DisplayablePortfolioDTOWithinUserComparator;
import com.androidth.general.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.androidth.general.api.portfolio.LiveAccountPortfolioItemHeader;
import com.androidth.general.fragments.timeline.TimelineFragment;
import com.androidth.general.fragments.timeline.TimelineHeaderButtonView;
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
    private static final int VIEW_TYPE_SPACING_PORTFOLIO = 3;
    private static final int VIEW_TYPE_SPACING_WATCHLISTS = 4;
    private static final int VIEW_TYPE_SPACING_COMPETITIONS = 5;


    private static final int VIEW_TYPE_LIVE_ACCOUNT = 200;

    public static final String DTO_LOADING = "Loading";
    public static final String DTO_SPACING_PORTFOLIOS = "Portfolios";
    public static final String DTO_SPACING_COMPETITIONS = "Competitions";
    public static final String DTO_SPACING_WATCHLISTS = "Watchlists";

    public static final String DTO_LIVE_ACCOUNT = "TH Live Account";

    private List<Object> orderedItems;
    private final DisplayablePortfolioDTOWithinUserComparator ownDisplayablePortfolioDTOWithinUserComparator;
    private final boolean isCurrentUser;
    @LayoutRes private final int loadingLayoutRes;
    @LayoutRes private final int spacingLayoutRes;
    @NonNull private final PublishSubject<TimelineFragment.TabType> tabTypeSubject;

    @NonNull private TimelineFragment.TabType currentTabType = TimelineFragment.TabType.TIMELINE;

    private int currentCount = 0;

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
            DisplayablePortfolioDTO.PORTFOLIO_TYPE currentDisplay = DisplayablePortfolioDTO.PORTFOLIO_TYPE.OTHERS;

            for (DisplayablePortfolioDTO displayablePortfolioDTO : ownPortfolios)
            {
                if (displayablePortfolioDTO.portfolioDTO != null)
                {
                    if(currentDisplay.equals(DisplayablePortfolioDTO.PORTFOLIO_TYPE.OTHERS)
                            && !currentDisplay.equals(DisplayablePortfolioDTO.PORTFOLIO_TYPE.PORTFOLIOS)){

                        currentDisplay = DisplayablePortfolioDTO.PORTFOLIO_TYPE.PORTFOLIOS;
                        preparedOrderedItems.add(DTO_SPACING_PORTFOLIOS);

                    } else if (displayablePortfolioDTO.portfolioDTO.isWatchlist) {

                        if(!currentDisplay.equals(DisplayablePortfolioDTO.PORTFOLIO_TYPE.WATCHLISTS)){
                            currentDisplay = DisplayablePortfolioDTO.PORTFOLIO_TYPE.WATCHLISTS;

                            //add spacing
                            preparedOrderedItems.add(DTO_SPACING_WATCHLISTS);
                        }

                    } else if (displayablePortfolioDTO.portfolioDTO.providerId != null && !hadCompetition) {
                        hadCompetition = true;

                        if(!currentDisplay.equals(DisplayablePortfolioDTO.PORTFOLIO_TYPE.COMPETITIONS)){
                            currentDisplay = DisplayablePortfolioDTO.PORTFOLIO_TYPE.COMPETITIONS;

                            preparedOrderedItems.add(DTO_SPACING_COMPETITIONS);
                        }

                    }
                }
                preparedOrderedItems.add(displayablePortfolioDTO);
            }
        }
        else
        {
            preparedOrderedItems.addAll(items);
        }

        currentCount = ownPortfolios.size() + 1;

        this.orderedItems = preparedOrderedItems;
    }

    @Override public int getCount()
    {
        int count = this.orderedItems.size(); // HACK because first item is not clickable
        return count == 0 ? 0 : count;
    }

    @Override public int getViewTypeCount()
    {
        return currentCount;
    }

    @Override public int getItemViewType(int position)
    {
//        if (position == 0)
//        {
//            return VIEW_TYPE_FIRST_POSITION;
//        }
        Object item = getItem(position);
        if(item==null){
            return VIEW_TYPE_LOADING;
        }

        if (item.equals(DTO_LOADING))
        {
            return VIEW_TYPE_LOADING;
        }
        if (item.equals(DTO_SPACING_PORTFOLIOS))
        {
            return VIEW_TYPE_SPACING_PORTFOLIO;
        }
        if (item.equals(DTO_SPACING_COMPETITIONS))
        {
            return VIEW_TYPE_SPACING_COMPETITIONS;
        }
        if (item.equals(DTO_SPACING_WATCHLISTS))
        {
            return VIEW_TYPE_SPACING_WATCHLISTS;
        }
        if(item instanceof LiveAccountPortfolioItemHeader){
            return VIEW_TYPE_FIRST_POSITION;
        }

        return VIEW_TYPE_REGULAR_POSITION;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public Object getItem(int position)
    {
//        position = position == 0 ? position : position - 1; // HACK because first item is not clickable
        if(this.orderedItems!=null && this.orderedItems.size()>0){
            return this.orderedItems.get(position);
        }else{
            return null;
        }
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_FIRST_POSITION:
                Object liveItem = getItem(position);

                if(liveItem!=null && liveItem.equals(DTO_LOADING)){
                    if (convertView == null)
                    {
                        convertView = new View(getContext());
                    }

                }else if(liveItem instanceof LiveAccountPortfolioItemHeader){
                    convertView = conditionalInflate(position, convertView, parent);

                    if(convertView instanceof LinearLayout){
                        ((LinearLayout) convertView).removeAllViews();
                        convertView = inflate(position, parent);
                    }
                    try{
                        ((PortfolioListItemView) convertView).display(getContext(), (LiveAccountPortfolioItemHeader) liveItem);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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

            case VIEW_TYPE_SPACING_PORTFOLIO:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(spacingLayoutRes, parent, false);
                    ((TextView)convertView).setText("Portfolios");
                }
                break;

            case VIEW_TYPE_SPACING_COMPETITIONS:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(spacingLayoutRes, parent, false);
                    ((TextView)convertView).setText("Competitions");
                }
                break;

            case VIEW_TYPE_SPACING_WATCHLISTS:
                if (convertView == null)
                {
                    convertView = getInflater().inflate(spacingLayoutRes, parent, false);
                    ((TextView)convertView).setText("Watchlists");
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
                && (viewType != VIEW_TYPE_SPACING_PORTFOLIO
                || viewType != VIEW_TYPE_SPACING_COMPETITIONS
                || viewType != VIEW_TYPE_SPACING_WATCHLISTS);
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
//        if (convertView == null)
//        {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_profile_detail_bottom_buttons, viewGroup, false);
//            ((TimelineHeaderButtonView) convertView).getTabTypeObservable().subscribe(tabTypeSubject);
//        }
//        ((TimelineHeaderButtonView) convertView).setActive(currentTabType);
//
//        return convertView;
        return new View(viewGroup.getContext());
    }

    @Override public long getHeaderId(int i)
    {
        return 0;
    }
}
