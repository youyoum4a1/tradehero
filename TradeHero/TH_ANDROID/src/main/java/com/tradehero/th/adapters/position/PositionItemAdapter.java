package com.tradehero.th.adapters.position;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.widget.position.PositionLongView;
import com.tradehero.th.widget.position.PositionSectionHeaderItemView;
import com.tradehero.th.widget.position.PositionView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 4:12 PM To change this template use File | Settings | File Templates. */
public class PositionItemAdapter extends BaseAdapter
{
    public static final String TAG = PositionItemAdapter.class.getName();

    private List<PositionDTO> receivedPositions;
    private List<OwnedPositionId> openPositions; // If nothing, it will show the positionNothingId layout
    private List<OwnedPositionId> closedPositions;

    protected final Context context;
    protected final LayoutInflater inflater;
    private final int headerLayoutId;
    private final int positionLayoutId;
    private final int positionLayoutExpandedId;
    private final int positionNothingId;

    private WeakReference<View> latestView = new WeakReference<>(null);

    private WeakReference<PositionLongView.OnListedPositionInnerLongClickedListener> parentMoreInfoRequestedListener = new WeakReference<>(null);
    private PositionLongView.OnListedPositionInnerLongClickedListener moreInfoRequestedListener;
    private int moreInfoPositionClicked = Integer.MIN_VALUE;

    public PositionItemAdapter(Context context, LayoutInflater inflater, int headerLayoutId, int positionLayoutId, int positionLayoutExpandedId, int positionNothingId)
    {
        super();
        this.context = context;
        this.inflater = inflater;
        this.headerLayoutId = headerLayoutId;
        this.positionLayoutId = positionLayoutId;
        this.positionLayoutExpandedId = positionLayoutExpandedId;
        this.positionNothingId = positionNothingId;
        this.moreInfoRequestedListener = new PositionLongView.OnListedPositionInnerLongClickedListener()
        {
            @Override public void onAddAlertClicked(int position, OwnedPositionId clickedOwnedPositionId)
            {
                PositionLongView.OnListedPositionInnerLongClickedListener listener = parentMoreInfoRequestedListener.get();
                if (listener != null)
                {
                    listener.onAddAlertClicked(position, clickedOwnedPositionId);
                }
            }

            @Override public void onBuyClicked(int position, OwnedPositionId clickedOwnedPositionId)
            {
                PositionLongView.OnListedPositionInnerLongClickedListener listener = parentMoreInfoRequestedListener.get();
                if (listener != null)
                {
                    listener.onBuyClicked(position, clickedOwnedPositionId);
                }
            }

            @Override public void onSellClicked(int position, OwnedPositionId clickedOwnedPositionId)
            {
                PositionLongView.OnListedPositionInnerLongClickedListener listener = parentMoreInfoRequestedListener.get();
                if (listener != null)
                {
                    listener.onSellClicked(position, clickedOwnedPositionId);
                }
            }

            @Override public void onStockInfoClicked(int position, OwnedPositionId clickedOwnedPositionId)
            {
                PositionLongView.OnListedPositionInnerLongClickedListener listener = parentMoreInfoRequestedListener.get();
                if (listener != null)
                {
                    listener.onStockInfoClicked(position, clickedOwnedPositionId);
                }
            }

            @Override public void onMoreInfoClicked(int position, OwnedPositionId clickedOwnedPositionId)
            {
                PositionLongView.OnListedPositionInnerLongClickedListener listener = parentMoreInfoRequestedListener.get();
                if (listener != null)
                {
                    listener.onMoreInfoClicked(position, clickedOwnedPositionId);
                }
            }

            @Override public void onTradeHistoryClicked(int position, OwnedPositionId clickedOwnedPositionId)
            {
                PositionLongView.OnListedPositionInnerLongClickedListener listener = parentMoreInfoRequestedListener.get();
                if (listener != null)
                {
                    listener.onTradeHistoryClicked(position, clickedOwnedPositionId);
                }
            }
        };
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    //<editor-fold desc="Counting Methods">
    public int getOpenPositionsCount()
    {
        return openPositions == null ? 0 : openPositions.size();
    }

    public int getVisibleOpenPositionsCount()
    {
        // If the list is empty, it will still show "tap to trade"
        return openPositions == null ? 0 : Math.max(1, openPositions.size());
    }

    public int getClosedPositionsCount()
    {
        return closedPositions == null ? 0 : closedPositions.size();
    }
    //</editor-fold>

    //<editor-fold desc="Index Methods">
    public int getOpenPositionIndex(int position)
    {
        return position - 1;
    }

    public int getClosedPositionIndex(int position)
    {
        return position - 2 - getVisibleOpenPositionsCount();
    }

    public boolean isPositionHeaderOpen(int position)
    {
        return position == 0;
    }

    public boolean isOpenPosition(int position)
    {
        return position >= 1 && position <= getVisibleOpenPositionsCount();
    }

    public boolean isPositionHeaderClosed(int position)
    {
        return position == (getVisibleOpenPositionsCount() + 1);
    }

    public boolean isClosedPosition(int position)
    {
        return position >= (getVisibleOpenPositionsCount() + 2);
    }
    //</editor-fold>

    @Override public long getItemId(int position)
    {
        long itemId;
        if (isPositionHeaderOpen(position))
        {
            itemId = "openPositionHeader".hashCode();
        }
        else if (isOpenPosition(position))
        {
            if (getOpenPositionsCount() == 0)
            {
                itemId = "tapToTrade".hashCode();
            }
            else
            {
                itemId = openPositions.get(position - 1).hashCode();
            }
        }
        else if (isPositionHeaderClosed(position))
        {
            itemId = "closedPositionHeader".hashCode();
        }
        else
        {
            itemId = closedPositions.get(position - 2 - getVisibleOpenPositionsCount()).hashCode();
        }
        return itemId;
    }

    @Override public int getCount()
    {
        return 2 + getVisibleOpenPositionsCount() + getClosedPositionsCount();
    }

    @Override public Object getItem(int position)
    {
        if (isOpenPosition(position) && getOpenPositionsCount() > 0)
        {
            return openPositions.get(getOpenPositionIndex(position));
        }
        else if (isClosedPosition(position))
        {
            return closedPositions.get(getClosedPositionIndex(position));
        }
        return null;
    }

    public String getHeaderText(boolean isForOpenPositions)
    {
        int stringResId;
        int count;
        if (isForOpenPositions)
        {
            stringResId = (openPositions == null) ? R.string.position_list_header_open_unsure : R.string.position_list_header_open;
            count = getOpenPositionsCount();
        }
        else
        {
            stringResId = (closedPositions == null) ? R.string.position_list_header_closed_unsure : R.string.position_list_header_closed;
            count = getClosedPositionsCount();
        }
        return String.format(context.getResources().getString(stringResId), count);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = null;
        if (isPositionHeaderOpen(position))
        {
            view = inflater.inflate(headerLayoutId, parent, false);
            ((PositionSectionHeaderItemView) view).setHeaderTextContent(getHeaderText(true));
        }
        else if (isOpenPosition(position) && getOpenPositionsCount() == 0)
        {
            view = inflater.inflate(positionNothingId, parent, false);
        }
        else if (isOpenPosition(position) && getOpenPositionsCount() > 0)
        {
            view = inflater.inflate(position == moreInfoPositionClicked ? positionLayoutExpandedId : positionLayoutId, parent, false);
            ((PositionView) view).display((OwnedPositionId) getItem(position));
            //((PositionQuickView) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            ((PositionView) view).setPosition(position);
            ((PositionView) view).setPositionClickedListener(moreInfoRequestedListener);
        }
        else if (isPositionHeaderClosed(position))
        {
            view = inflater.inflate(headerLayoutId, parent, false);
            ((PositionSectionHeaderItemView) view).setHeaderTextContent(getHeaderText(false));
        }
        else
        {
            view = inflater.inflate(position == moreInfoPositionClicked ? positionLayoutExpandedId : positionLayoutId, parent, false);
            ((PositionView) view).display((OwnedPositionId) getItem(position));
            //((PositionQuickView) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            ((PositionView) view).setPosition(position);
            ((PositionView) view).setPositionClickedListener(moreInfoRequestedListener);
        }
        latestView = new WeakReference<>(view);
        return view;
    }

    /**
     * The listener needs to be strongly referenced elsewhere
     * @param parentMoreInfoRequestedListener
     */
    public void setParentMoreInfoRequestedListener(PositionLongView.OnListedPositionInnerLongClickedListener parentMoreInfoRequestedListener)
    {
        this.parentMoreInfoRequestedListener = new WeakReference<>(parentMoreInfoRequestedListener);
    }

    public void setPositions(List<PositionDTO> positions, PortfolioId portfolioId)
    {
        this.receivedPositions = positions;

        if (positions == null)
        {
            openPositions = null;
            closedPositions = null;
        }
        else
        {
            openPositions = new ArrayList<>();
            closedPositions = new ArrayList<>();

            for (PositionDTO positionDTO: positions)
            {
                if (positionDTO.isOpen() == null)
                {
                    // TODO decide what to do
                }
                else if (positionDTO.isOpen())
                {
                    openPositions.add(positionDTO.getFiledPositionId(portfolioId.key));
                }
                else
                {
                    closedPositions.add(positionDTO.getFiledPositionId(portfolioId.key));
                }
            }
        }
    }

    public void togglePositionClicked(int newPosition)
    {
        setMoreInfoPositionClicked(this.moreInfoPositionClicked == newPosition ? Integer.MIN_VALUE : newPosition);
    }

    public void setMoreInfoPositionClicked(Integer moreInfoPositionClicked)
    {
        this.moreInfoPositionClicked = moreInfoPositionClicked == null ? Integer.MIN_VALUE : moreInfoPositionClicked;
        postDataSetChanged();
    }

    public void postDataSetChanged()
    {
        View anyView = this.latestView.get();
        if (anyView != null)
        {
            anyView.post(new Runnable()
            {
                @Override public void run()
                {
                    notifyDataSetChanged();
                }
            });
        }
    }
}
