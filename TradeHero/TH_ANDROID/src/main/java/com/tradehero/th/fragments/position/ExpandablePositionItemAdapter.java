package com.tradehero.th.fragments.position;

import android.content.Context;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.adapters.ExpandableListReporter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.view.PositionView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.support.annotation.NonNull;

public class ExpandablePositionItemAdapter extends PositionItemAdapter
        implements ExpandableListReporter
{
    private WeakReference<PositionListener<PositionDTO>> cellListener;

    //<editor-fold desc="Constructors">
    public ExpandablePositionItemAdapter(
            @NonNull Context context,
            @NonNull Map<Integer, Integer> positionItemTypeToLayoutId)
    {
        super(context, positionItemTypeToLayoutId);
    }
    //</editor-fold>

    protected ExpandableListItem<PositionDTO> createExpandableItem(@NonNull PositionDTO dto)
    {
        return new ExpandableListItem<>(dto);
    }

    @Override public int getItemViewType(int position)
    {
        Object item = getItem(position);
        if (item instanceof ExpandableListItem)
        {
            return super.getItemViewType(((PositionDTO) ((ExpandableListItem) item).getModel()));
        }
        return super.getItemViewType(position);
    }

    @Override protected void add(@NonNull List<Object> items, @NonNull PositionDTO item)
    {
        items.add(createExpandableItem(item));
    }

    @Override protected void preparePositionView(PositionView cell, Object item, int position)
    {
        //noinspection unchecked
        cell.linkWith((ExpandableListItem<PositionDTO>) item, false);
        cell.display();
        cell.setListener(new AbstractPositionItemAdapterPositionListener());
    }

    /**
     * The listener needs to be strongly referenced elsewhere
     * @param cellListener
     */
    public void setCellListener(PositionListener<PositionDTO> cellListener)
    {
        this.cellListener = new WeakReference<>(cellListener);
    }

    //<editor-fold desc="ExpandableListReporter">
    @Override public List<Boolean> getExpandedStatesPerPosition()
    {
        List<Boolean> expandedStates = new ArrayList<>();

        int position = 0;
        Object itemAtPosition = null;
        while (position < getCount())
        {
            itemAtPosition = getItem(position);
            if (itemAtPosition instanceof ExpandableListItem)
            {
                expandedStates.add(((ExpandableListItem) itemAtPosition).isExpanded());
            }
            else
            {
                expandedStates.add(false);
            }
            position++;
        }

        return expandedStates;
    }

    @Override public void setExpandedStatesPerPosition(boolean[] expandedStatesPerPosition)
    {
        if (expandedStatesPerPosition == null)
        {
            return;
        }
        List<Boolean> expandedStates = new ArrayList<>();
        for (boolean state: expandedStatesPerPosition)
        {
            expandedStates.add(state);
        }
        setExpandedStatesPerPosition(expandedStates);
    }

    @Override public void setExpandedStatesPerPosition(List<Boolean> expandedStatesPerPosition)
    {
        if (expandedStatesPerPosition == null)
        {
            return;
        }

        int position = 0;
        while (position < getCount() && position < expandedStatesPerPosition.size())
        {
            Object itemAtPosition = getItem(position);
            if (itemAtPosition instanceof ExpandableListItem)
            {
                ((ExpandableListItem) itemAtPosition).setExpanded(expandedStatesPerPosition.get(position));
            }
            position++;
        }
    }
    //</editor-fold>

    protected class AbstractPositionItemAdapterPositionListener
            implements PositionListener<PositionDTO>
    {
        @Override public void onTradeHistoryClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onTradeHistoryClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onBuyClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onBuyClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onSellClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onSellClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onAddAlertClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onAddAlertClicked(clickedOwnedPositionId);
            }
        }

        @Override public void onStockInfoClicked(PositionDTO clickedOwnedPositionId)
        {
            PositionListener<PositionDTO> listener = cellListener.get();
            if (listener != null)
            {
                listener.onStockInfoClicked(clickedOwnedPositionId);
            }
        }
    }
}
