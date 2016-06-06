package com.androidth.general.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class WrapperRecyclerAdapter<ExtraItemType extends WrapperRecyclerAdapter.ExtraItem>
        extends RecyclerView.Adapter
{
    private final RecyclerView.Adapter realItemAdapter;
    private final SparseArray<ExtraItemType> extraItems;
    private final Set<Integer> extraItemViewTypes = new LinkedHashSet<>();

    public WrapperRecyclerAdapter(final RecyclerView.Adapter realItemAdapter)
    {
        extraItems = new SparseArray<>();
        this.realItemAdapter = realItemAdapter;
        this.realItemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override public void onChanged()
            {
                super.onChanged();
                notifyDataSetChanged();
            }

            @Override public void onItemRangeChanged(int positionStart, int itemCount)
            {
                super.onItemRangeChanged(positionStart, itemCount);
                int adjustedPositionStart = getAdjustedPosition(positionStart);
                int adjustedPositionCount = getAdjustedItemCount(adjustedPositionStart, itemCount);
                notifyItemRangeChanged(adjustedPositionStart, adjustedPositionCount);
            }

            @Override public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                int adjustedPositionStart = getAdjustedPosition(positionStart);
                int adjustedPositionCount = getAdjustedItemCount(adjustedPositionStart, itemCount);
                notifyItemRangeInserted(adjustedPositionStart, adjustedPositionCount);
            }

            @Override public void onItemRangeRemoved(int positionStart, int itemCount)
            {
                super.onItemRangeRemoved(positionStart, itemCount);
                int adjustedPositionStart = getAdjustedPosition(positionStart);
                int adjustedPositionCount = getAdjustedItemCount(adjustedPositionStart, itemCount);
                notifyItemRangeRemoved(adjustedPositionStart, adjustedPositionCount);
            }
        });
    }

    private int getAdjustedPosition(int realPosition)
    {
        int adjustedPosition = realPosition;
        for (int i = 0; i < extraItems.size(); i++)
        {
            int key = extraItems.keyAt(i);
            if (key <= adjustedPosition)
            {
                adjustedPosition++;
            }
            else
            {
                break;
            }
        }
        return adjustedPosition;
    }

    private int getAdjustedItemCount(int adjustedPosition, int realItemCount)
    {
        int adjustedPositionCount = realItemCount;
        for (int i = 0; i < extraItems.size(); i++)
        {
            int key = extraItems.keyAt(i);
            if (key >= adjustedPosition && key < adjustedPosition + realItemCount)
            {
                adjustedPositionCount++;
            }
        }
        return adjustedPositionCount;
    }

    protected abstract RecyclerView.ViewHolder onCreateExtraItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindExtraItemViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override public int getItemViewType(int position)
    {
        if (extraItems.get(position) != null)
        {
            return extraItems.get(position).getViewType();
        }
        position = getRealPosition(position);
        return realItemAdapter.getItemViewType(position);
    }

    @Override public long getItemId(int position)
    {
        if (extraItems.get(position) != null)
        {
            return super.getItemId(position);
        }
        else
        {
            return realItemAdapter.getItemId(getRealPosition(position));
        }
    }

    @Override public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (extraItemViewTypes.contains(viewType))
        {
            return onCreateExtraItemViewHolder(parent, viewType);
        }
        return realItemAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (extraItems.get(position) != null)
        {
            onBindExtraItemViewHolder(holder, position);
        }
        else
        {
            position = getRealPosition(position);
            realItemAdapter.onBindViewHolder(holder, position);
        }
    }

    public void addExtraItem(int position, ExtraItemType extraItemType)
    {
        ExtraItemType existing = extraItems.get(position);
        if (existing != null && !existing.equals(extraItemType))
        {
            //There is an existing extra item at position.
            //Remove any existing viewType and replace the item in array.
            extraItemViewTypes.remove(extraItemType.getViewType());
            extraItems.put(position, extraItemType);
            extraItemViewTypes.add(extraItemType.getViewType());
            notifyItemChanged(position);
        }
        else if (existing != null && existing.equals(extraItemType))
        {
            //There is an existing extra item which equals to one being added.
            //Replace without notify.
            extraItems.put(position, extraItemType);
        }
        else
        {
            //Add the item to array and notify.
            extraItems.put(position, extraItemType);
            extraItemViewTypes.add(extraItemType.getViewType());
            notifyItemInserted(position);
        }
    }

    @Nullable public ExtraItemType getExtraItem(int position)
    {
        return extraItems.get(position);
    }

    private int getRealPosition(int position)
    {
        int offset = 0;
        for (int i = 0; i < extraItems.size(); i++)
        {
            int key = extraItems.keyAt(i);
            if (key < position)
            {
                offset++;
            }
            else if (key > position)
            {
                break;
            }
        }
        return position - offset;
    }

    @Override public int getItemCount()
    {
        return realItemAdapter.getItemCount() + extraItems.size();
    }

    public interface ExtraItem
    {
        int getViewType();
    }
}
