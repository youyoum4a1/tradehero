package com.tradehero.th.fragments.position;

import android.content.Context;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.view.PositionView;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class ExpandablePositionItemAdapter extends PositionItemAdapter
{
    private WeakReference<PositionListener<PositionDTO>> cellListener;

    //<editor-fold desc="Constructors">
    public ExpandablePositionItemAdapter(
            @NotNull Context context,
            @NotNull Map<Integer, Integer> positionItemTypeToLayoutId)
    {
        super(context, positionItemTypeToLayoutId);
    }
    //</editor-fold>

    protected ExpandableListItem<PositionDTO> createExpandableItem(@NotNull PositionDTO dto)
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

    @Override protected void add(@NotNull List<Object> items, @NotNull PositionDTO item)
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
