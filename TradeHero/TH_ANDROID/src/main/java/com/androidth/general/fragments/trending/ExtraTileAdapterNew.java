package com.androidth.general.fragments.trending;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.WrapperListAdapter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class ExtraTileAdapterNew extends BaseAdapter
        implements WrapperListAdapter
{
    private static final int EXTRA_TILE_RANGE = 6;
    private static final int EXTRA_TILE_MIN_DISTANCE = 6;
    private static final int MAX_RANDOM_RETRIES = 50;
    private static final int tileTypeCount = TileType.values().length;

    @NonNull private final LayoutInflater inflater;
    @NonNull private final ListAdapter wrappedAdapter;
    @NonNull private final Random random;

    @NonNull private Map<Integer, TileType> extraTiles;
    private boolean isSurveyEnabled = false;
    private boolean isProviderEnabled = false;

    //<editor-fold desc="Constructors">
    public ExtraTileAdapterNew(@NonNull Context context, @NonNull ListAdapter wrappedAdapter)
    {
        this.inflater = LayoutInflater.from(context);
        this.wrappedAdapter = wrappedAdapter;
        if (!(wrappedAdapter instanceof SpinnerAdapter))
        {
            throw new IllegalArgumentException("Adapter needs to be a Spinner Adapter " + wrappedAdapter.getClass());
        }
        this.random = new Random(wrappedAdapter.hashCode());
        this.extraTiles = new LinkedHashMap<>();
        //putCompetitionExtraTilePosition();
        //putFirstExtraTilePosition();
    }
    //</editor-fold>

    @Override @NonNull public ListAdapter getWrappedAdapter()
    {
        return wrappedAdapter;
    }

    public void setSurveyEnabled(boolean isSurveyEnabled)
    {
        this.isSurveyEnabled = isSurveyEnabled;
    }

    public void setProviderEnabled(boolean isProviderEnabled)
    {
        this.isProviderEnabled = isProviderEnabled;
        if(isProviderEnabled)
        {
            putCompetitionExtraTilePosition();
            notifyDataSetChanged();
        }
    }

    public void setLiveAccountTile(){
        this.extraTiles.put(1, TileType.LiveToggle);
    }

    @Override public void registerDataSetObserver(DataSetObserver observer)
    {
        wrappedAdapter.registerDataSetObserver(observer);
    }

    @Override public void unregisterDataSetObserver(DataSetObserver observer)
    {
        wrappedAdapter.unregisterDataSetObserver(observer);
    }

    @Override public int getCount()
    {
        int wrappedCount = wrappedAdapter.getCount();
        addRandomCompetitionTiles(wrappedCount);
        int interstitial = 0;
        for (Integer extraPosition : extraTiles.keySet())
        {
            if (extraPosition < wrappedCount + interstitial)
            {
                interstitial++;
            }
            else
            {
                break;
            }
        }
        return wrappedCount + interstitial;
    }

    @Override public Object getItem(int position)
    {
        int offset = 0;
        for (Integer extraPosition : extraTiles.keySet())
        {
            if (extraPosition < position)
            {
                offset++;
            }
            else if (extraPosition == position)
            {
                return extraTiles.get(position);
            }
            else
            {
                break;
            }
        }
        return wrappedAdapter.getItem(position - offset);
    }

    @Override public int getViewTypeCount()
    {
        return wrappedAdapter.getViewTypeCount() + tileTypeCount;
    }

    @Override public int getItemViewType(int position)
    {
        int offset = 0;
        for (Integer extraPosition : extraTiles.keySet())
        {
            if (extraPosition < position)
            {
                offset++;
            }
            else if (extraPosition == position)
            {
                return extraTiles.get(position).ordinal() + wrappedAdapter.getViewTypeCount();
            }
            else
            {
                break;
            }
        }
        return wrappedAdapter.getItemViewType(position - offset);
    }

    @Override public long getItemId(int position)
    {
        int offset = 0;
        for (Integer extraPosition : extraTiles.keySet())
        {
            if (extraPosition < position)
            {
                offset++;
            }
            else if (extraPosition == position)
            {
                return extraTiles.get(position).hashCode();
            }
            else
            {
                break;
            }
        }
        return wrappedAdapter.getItemId(position - offset);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        int offset = 0;
        for (Integer extraPosition : extraTiles.keySet())
        {
            if (extraPosition < position)
            {
                offset++;
            }
            else if (extraPosition == position)
            {
                if (convertView == null)
                {
                    return inflater.inflate(extraTiles.get(position).getLayoutResourceId(), parent, false);
                }
                return convertView;
            }
            else
            {
                break;
            }
        }
        return wrappedAdapter.getView(position - offset, convertView, parent);
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return ((SpinnerAdapter) wrappedAdapter).getDropDownView(position, convertView, parent);
    }

    @NonNull protected TileType getRandomisedTile()
    {
        int remainingTries = MAX_RANDOM_RETRIES;
        TileType tileType;
        do
        {
            if (remainingTries-- <= 0)
            {
                return TileType.ExtraCash;
            }
            // HACK to avoid Normal
            tileType = TileType.at(random.nextInt(tileTypeCount - 1) + 1);
        }
        while (!isValid(tileType));
        return tileType;
    }

    protected boolean isValid(@NonNull TileType tileType)
    {
        return !tileType.equals(TileType.Normal)
                && (isSurveyEnabled || !tileType.equals(TileType.Survey))
                && (isProviderEnabled || !tileType.equals(TileType.FromProvider));
    }

    public void clearExtraTiles()
    {
        extraTiles.clear();
        putFirstExtraTilePosition();
        notifyDataSetChanged();
    }

    public void newClearExtraTiles()
    {
        if(isProviderEnabled) {
            extraTiles.clear();
            putCompetitionExtraTilePosition();
            notifyDataSetChanged();
        }
    }

    protected void putFirstExtraTilePosition()
    {
        this.extraTiles.put(random.nextInt(EXTRA_TILE_RANGE), getRandomisedTile());
    }

    protected void putCompetitionExtraTilePosition()
    {
        this.extraTiles.put(0, getCompetitionTile());
        this.notifyDataSetChanged();
        //this.extraTiles.put(random.nextInt(EXTRA_TILE_RANGE)+8, getCompetitionTile());
    }

    @NonNull protected TileType getCompetitionTile()
    {
        return TileType.FromProvider;
    }

    protected int getMaxExtraTilePosition()
    {
        int max = 0;
        for (int position : extraTiles.keySet())
        {
            max = Math.max(max, position);
        }
        return max;
    }

    protected void addRandomTiles(int wrappedCount)
    {
        int maxExtraPosition = getMaxExtraTilePosition();
        while (wrappedCount > maxExtraPosition + EXTRA_TILE_MIN_DISTANCE)
        {
            maxExtraPosition += getNextExtraTileOffset();
            extraTiles.put(maxExtraPosition, getRandomisedTile());
        }
    }
    protected void addRandomCompetitionTiles(int wrappedCount)
    {
        if(isProviderEnabled) {
            int maxExtraPosition = getMaxExtraTilePosition();
            while (wrappedCount > maxExtraPosition + EXTRA_TILE_MIN_DISTANCE) {
                maxExtraPosition += getNextExtraTileOffset();
                extraTiles.put(maxExtraPosition, getCompetitionTile());
                extraTiles.put(1, TileType.LiveToggle);
            }
        }
    }

    protected int getNextExtraTileOffset()
    {
        return EXTRA_TILE_MIN_DISTANCE + random.nextInt(EXTRA_TILE_RANGE);
    }
}