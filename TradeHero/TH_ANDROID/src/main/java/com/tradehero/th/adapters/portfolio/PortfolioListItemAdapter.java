package com.tradehero.th.adapters.portfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.widget.list.BaseListHeaderView;
import com.tradehero.th.widget.portfolio.PortfolioListItemView;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class PortfolioListItemAdapter extends DTOAdapter<DisplayablePortfolioDTO, PortfolioListItemView>
{
    public static final String TAG = PortfolioListItemAdapter.class.getName();

    private Set<DisplayablePortfolioDTO> ownPortfolios;
    private Set<DisplayablePortfolioDTO> otherPortfolios;

    private final int otherHeaderResId;

    public PortfolioListItemAdapter(Context context, LayoutInflater inflater, int portfolioHeaderLayoutResourceId, int otherHeaderResId)
    {
        super(context, inflater, portfolioHeaderLayoutResourceId);
        this.otherHeaderResId = otherHeaderResId;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public void setItems(List<DisplayablePortfolioDTO> items)
    {
        super.setItems(items);

        if (items == null)
        {
            ownPortfolios = null;
            otherPortfolios = null;
        }
        else
        {
            ownPortfolios = new TreeSet<>();
            otherPortfolios = new TreeSet<>();

            for (DisplayablePortfolioDTO displayablePortfolioDTO: items)
            {
                if (displayablePortfolioDTO == null)
                {
                    // Do nothing
                }
                else if (displayablePortfolioDTO.isUserCurrentUser())
                {
                    ownPortfolios.add(displayablePortfolioDTO);
                }
                else
                {
                    otherPortfolios.add(displayablePortfolioDTO);
                }
            }
        }
    }

    public int getOwnPortfolioCount()
    {
        if (ownPortfolios == null)
        {
            return 0;
        }
        return ownPortfolios.size();
    }

    public int getOtherPortfolioCount()
    {
        if (otherPortfolios == null)
        {
            return 0;
        }
        return otherPortfolios.size();
    }

    public boolean hasOtherPortfolioHeader()
    {
        return getOtherPortfolioCount() > 0;
    }

    public int getOtherPortfolioHeaderCount()
    {
        return hasOtherPortfolioHeader() ? 1 : 0;
    }

    public int getOtherPortfolioIndex(int position)
    {
        return position - getOtherPortfolioHeaderCount() - getOwnPortfolioCount();
    }

    @Override public int getCount()
    {
        return getOwnPortfolioCount() + getOtherPortfolioHeaderCount() + getOtherPortfolioCount();
    }

    public boolean isOwnPortfolio(int position)
    {
        return position < getOwnPortfolioCount();
    }

    public boolean isOtherPortfolioHeader(int position)
    {
        return hasOtherPortfolioHeader() && position == getOwnPortfolioCount();
    }

    public boolean isOtherPortfolio(int position)
    {
        return position >= getOwnPortfolioCount() + getOtherPortfolioHeaderCount();
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }

    @Override public Object getItem(int position)
    {
        Iterator<DisplayablePortfolioDTO> iterator = null;
        int index = 0;
        if (isOwnPortfolio(position))
        {
            iterator = ownPortfolios.iterator();
            while (index++ < position)
            {
                iterator.next();
            }
            return iterator.next();
        }
        else if (isOtherPortfolio(position))
        {
            iterator = otherPortfolios.iterator();
            int desiredIndex = getOtherPortfolioIndex(position);
            while (index++ < desiredIndex)
            {
                iterator.next();
            }
            return iterator.next();
        }
        return "otherPortfolioHeader";
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        if (isOwnPortfolio(position))
        {
            view = inflater.inflate(layoutResourceId, parent, false);
            ((PortfolioListItemView) view).display((DisplayablePortfolioDTO) getItem(position));
        }
        else if (isOtherPortfolioHeader(position))
        {
            view = inflater.inflate(otherHeaderResId, parent, false);
            ((BaseListHeaderView) view).setHeaderTextContent(context.getString(R.string.portfolio_recently_viewed));
        }
        else
        {
            view = inflater.inflate(layoutResourceId, parent, false);
            ((PortfolioListItemView) view).display((DisplayablePortfolioDTO) getItem(position));
        }
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return !isOtherPortfolioHeader(position);
    }

    @Override protected void fineTune(int position, DisplayablePortfolioDTO dto, PortfolioListItemView dtoView)
    {
        // Nothing to do
    }
}
