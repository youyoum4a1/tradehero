package com.tradehero.th.adapters.portfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.THUser;
import com.tradehero.th.widget.portfolio.PortfolioListHeaderView;
import com.tradehero.th.widget.portfolio.PortfolioListItemView;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class PortfolioListItemAdapter extends DTOAdapter<OwnedPortfolioId, PortfolioListItemView>
{
    public static final String TAG = PortfolioListItemAdapter.class.getName();

    private List<OwnedPortfolioId> ownPortfolioIds;
    private List<OwnedPortfolioId> otherPortfolioIds;

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

    @Override public void setItems(List<OwnedPortfolioId> items)
    {
        super.setItems(items);

        if (items == null)
        {
            ownPortfolioIds = null;
            otherPortfolioIds = null;
        }
        else
        {
            ownPortfolioIds = new ArrayList<>();
            otherPortfolioIds = new ArrayList<>();
            UserBaseKey ownUserKey = THUser.getCurrentUserBase().getBaseKey();

            for (OwnedPortfolioId ownedPortfolioId: items)
            {
                if (ownedPortfolioId.getUserBaseKey().equals(ownUserKey))
                {
                    ownPortfolioIds.add(ownedPortfolioId);
                }
                else
                {
                    otherPortfolioIds.add(ownedPortfolioId);
                }
            }
        }
    }

    public int getOwnPortfolioIdCount()
    {
        if (ownPortfolioIds == null)
        {
            return 0;
        }
        return ownPortfolioIds.size();
    }

    public int getOtherPortfolioIdCount()
    {
        if (otherPortfolioIds == null)
        {
            return 0;
        }
        return otherPortfolioIds.size();
    }

    public boolean hasOtherPortfolioHeader()
    {
        return getOtherPortfolioIdCount() > 0;
    }

    public int getOtherPortfolioHeaderCount()
    {
        return hasOtherPortfolioHeader() ? 1 : 0;
    }

    public int getOtherPortfolioIdIndex(int position)
    {
        return position - getOtherPortfolioHeaderCount() - getOwnPortfolioIdCount();
    }

    @Override public int getCount()
    {
        return getOwnPortfolioIdCount() + getOtherPortfolioHeaderCount() + getOtherPortfolioIdCount();
    }

    public boolean isOwnPortfolio(int position)
    {
        return position < getOwnPortfolioIdCount();
    }

    public boolean isOtherPortfolioHeader(int position)
    {
        return hasOtherPortfolioHeader() && position == getOwnPortfolioIdCount();
    }

    public boolean isOtherPortfolio(int position)
    {
        return position >= getOwnPortfolioIdCount() + getOtherPortfolioHeaderCount();
    }

    @Override public long getItemId(int position)
    {
        long itemId;
        if (isOwnPortfolio(position))
        {
            itemId = ownPortfolioIds.get(position).hashCode();
        }
        else if (isOtherPortfolioHeader(position))
        {
            itemId = "otherPortfolioHeader".hashCode();
        }
        else
        {
            itemId = otherPortfolioIds.get(getOtherPortfolioIdIndex(position)).hashCode();
        }
        return itemId;
    }

    @Override public Object getItem(int position)
    {
        if (isOwnPortfolio(position))
        {
            return ownPortfolioIds.get(position);
        }
        else if (isOtherPortfolio(position))
        {
            return otherPortfolioIds.get(getOtherPortfolioIdIndex(position));
        }
        return null;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        if (isOwnPortfolio(position))
        {
            view = inflater.inflate(layoutResourceId, parent, false);
            ((PortfolioListItemView) view).display((OwnedPortfolioId) getItem(position));
        }
        else if (isOtherPortfolioHeader(position))
        {
            view = inflater.inflate(otherHeaderResId, parent, false);
            ((PortfolioListHeaderView) view).setHeaderTextContent(context.getString(R.string.portfolio_recently_viewed));
        }
        else
        {
            view = inflater.inflate(layoutResourceId, parent, false);
            ((PortfolioListItemView) view).display((OwnedPortfolioId) getItem(position));
        }
        return view;
    }

    @Override protected void fineTune(int position, OwnedPortfolioId dto, PortfolioListItemView dtoView)
    {
        // Nothing to do
    }
}
