package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import com.tradehero.common.utils.THLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/6/14 Time: 5:38 PM Copyright (c) TradeHero
 */
public class ExtraTileAdapter extends BaseAdapter
        implements WrapperListAdapter
{
    private static final int EXTRA_TILE_FREQUENCY = 16;
    private static final int EXTRA_TILE_MIN_DISTANCE = 10;
    private static final String TAG = ExtraTileAdapter.class.getName();

    private final ListAdapter wrappedAdapter;
    private final LayoutInflater inflater;

    private Pair<TileType, Integer>[] extraTilesMarker;

    // selected marker which contain the most number of tiles and positions
    private Pair<TileType, Integer>[] masterTilesMarker;

    public ExtraTileAdapter(Context context, ListAdapter wrappedAdapter)
    {
        this.inflater = LayoutInflater.from(context);
        this.wrappedAdapter = wrappedAdapter;
        wrappedAdapter.registerDataSetObserver(wrappedAdapterDataSetObserver);
    }

    @Override public void registerDataSetObserver(DataSetObserver observer)
    {
        if (wrappedAdapter != null)
        {
            wrappedAdapter.registerDataSetObserver(observer);
        }
    }

    @Override public void unregisterDataSetObserver(DataSetObserver observer)
    {
        if (wrappedAdapter != null)
        {
            wrappedAdapter.unregisterDataSetObserver(observer);
        }
    }

    @Override public int getCount()
    {
        return wrappedAdapter.getCount() + (extraTilesMarker != null ? extraTilesMarker.length : 0);
    }

    private int getWrappedPosition(int position)
    {
        if (extraTilesMarker != null)
        {
            for (int i = 0; i < extraTilesMarker.length; ++i)
            {
                if (position == extraTilesMarker[i].second)
                {
                    return -1;
                }
                else if (position < extraTilesMarker[i].second)
                {
                    THLog.d(TAG, String.format("position: %d ---> position-i %d, extraTilesMarker[i].second: %d",
                            position, position - i, extraTilesMarker[i].second));
                    return position - i;
                }
            }

            THLog.d(TAG, String.format("%d ---> %d (extraTilesMarker)", position, position - extraTilesMarker.length));
            return position - extraTilesMarker.length;
        }
        throw new IllegalAccessError("extra tile marker should be initialized");
    }

    @Override public Object getItem(int position)
    {
        if (extraTilesMarker != null)
        {
            for (Pair<TileType, Integer> marker : extraTilesMarker)
            {
                if (position == marker.second)
                {
                    return marker.first;
                }
            }

            int wrappedPosition = getWrappedPosition(position);
            if (wrappedPosition >= 0)
            {
                return wrappedAdapter.getItem(wrappedPosition);
            }
        }
        else
        {
            return wrappedAdapter.getItem(position);
        }

        return null;
    }

    @Override public long getItemId(int position)
    {
        return position;
    }

    @Override public boolean hasStableIds()
    {
        return wrappedAdapter.hasStableIds();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        int viewType = getItemViewType(position);
        if (viewType == TileType.Normal.ordinal())
        {
            return wrappedAdapter.getView(getWrappedPosition(position), convertView, parent);
        }

        if (convertView == null)
        {
            convertView = inflater.inflate(TileType.at(viewType).getLayoutResourceId(), parent, false);
        }
        return convertView;
    }

    @Override public int getItemViewType(int position)
    {
        int wrappedPosition = getWrappedPosition(position);

        if (wrappedPosition >= 0)
        {
            return TileType.Normal.ordinal();
        }
        else
        {
            Object item = getItem(position);
            if (item instanceof TileType)
            {
                return ((TileType) item).ordinal();
            }
            throw new IllegalAccessError("Item without viewType at " + position + ", value " + item);
        }
    }

    @Override public int getViewTypeCount()
    {
        if (wrappedAdapter != null)
        {
            return TileType.values().length;
        }
        else
        {
            throw new IllegalAccessError("wrappedAdapter should not be null");
        }
    }

    @Override public boolean isEmpty()
    {
        return wrappedAdapter.isEmpty();
    }

    @Override public ListAdapter getWrappedAdapter()
    {
        return wrappedAdapter;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return wrappedAdapter.areAllItemsEnabled();
    }

    @Override public boolean isEnabled(int position)
    {
        if (getItemViewType(position) == TileType.Normal.ordinal())
        {
            return wrappedAdapter.isEnabled(position);
        }
        else
        {
            // TODO
            return true;
        }
    }

    @Override public void notifyDataSetChanged()
    {
        regenerateExtraTiles();
        super.notifyDataSetChanged();
    }

    private void regenerateExtraTiles()
    {
        int extraTileCount = Math.round(wrappedAdapter.getCount() / EXTRA_TILE_FREQUENCY);

        if (extraTileCount > 0)
        {
            if (masterTilesMarker != null && extraTileCount < masterTilesMarker.length)
            {
                THLog.d(TAG, "Reusing marker!");
                extraTilesMarker = Arrays.copyOf(masterTilesMarker, extraTileCount);
            }
            else
            {
                int[] extraTileIndexes = generateExtraTileIndexes(extraTileCount);
                TileType[] showingTiles = generateRandomTypeForTiles(extraTileIndexes);

                Pair<TileType, Integer>[] tempMarker = new Pair[extraTileCount];


                // TODO make it litter bit better by only generate new tile positions
                for (int i = 0; i < extraTileCount; ++i)
                {
                    tempMarker[i] = new Pair<>(showingTiles[i], extraTileIndexes[i]);
                }
                if (masterTilesMarker != null)
                {
                    System.arraycopy(masterTilesMarker, 0, tempMarker, 0, masterTilesMarker.length);
                }
                extraTilesMarker = tempMarker;
                masterTilesMarker = tempMarker;
            }
        }
        else
        {
            extraTilesMarker = null;
        }
    }

    //<editor-fold desc="Completion functions for regenerateExtraTiles">
    private TileType[] generateRandomTypeForTiles(int[] extraTileIndexes)
    {
        List<TileType> showingTileTypes = new ArrayList<>();
        for (TileType tileType : TileType.values())
        {
            if (tileType.isExtra())
            {
                showingTileTypes.add(tileType);
            }
        }

        List<TileType> showingTiles = new ArrayList<>();

        for (int i = 0; i < extraTileIndexes.length; ++i)
        {
            showingTiles.add(showingTileTypes.get(i % showingTileTypes.size()));
        }
        // and suffer the tile
        Collections.shuffle(showingTiles);
        // always show the first one as survey tile
        showingTiles.set(0, TileType.Survey);

        TileType[] retArray = new TileType[showingTiles.size()];
        showingTiles.toArray(retArray);
        return retArray;
    }

    private int[] generateExtraTileIndexes(int extraTileCount)
    {
        int[] extraTileIndexes = new int[extraTileCount];
        THLog.d(TAG, String.format("Old count: %d, extra: %d", wrappedAdapter.getCount(), extraTileCount));
        int maxTileIndex = wrappedAdapter.getCount() + extraTileCount - 1;
        int previousIndex = -1;

        // first element is always at 0
        extraTileIndexes[0] = 0;
        for (int i = 1; i < extraTileCount; ++i)
        {
            int newTileIndex = i * EXTRA_TILE_FREQUENCY + (int) (Math.random() * EXTRA_TILE_FREQUENCY);
            if (previousIndex > 0 && (newTileIndex - previousIndex < EXTRA_TILE_MIN_DISTANCE))
            {
                newTileIndex = previousIndex + EXTRA_TILE_MIN_DISTANCE;
            }
            // side effect of previous tiles insertion, also there should not be any overlapping between 2 tiles random space
            newTileIndex += i % EXTRA_TILE_MIN_DISTANCE;
            newTileIndex = Math.min(maxTileIndex, newTileIndex);
            previousIndex = newTileIndex;
            extraTileIndexes[i] = newTileIndex;
        }

        return extraTileIndexes;
    }
    //</editor-fold>

    private final DataSetObserver wrappedAdapterDataSetObserver = new DataSetObserver()
    {
        @Override public void onChanged()
        {
            THLog.d(TAG, "onChanged");
            notifyDataSetChanged();
        }

        @Override public void onInvalidated()
        {
            notifyDataSetInvalidated();
        }
    };
}
