package com.tradehero.th.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

public abstract class WrapperRecyclerAdapter<ExtraItemType>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_WRAPPED = 999;
    private final RecyclerView.Adapter realItemAdapter;
    private final SparseArray<ExtraItemType> extraItems;

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
            return VIEW_TYPE_WRAPPED;
        }
        position = getRealPosition(position);
        return super.getItemViewType(position);
    }

    @Override public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_WRAPPED)
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
}
