package com.tradehero.th.adapters;

import android.content.Context;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

abstract public class DTOSetAdapter<T> extends BaseAdapter
{
    protected final Context context;
    protected LinkedHashSet<T> set;
    private ArrayList<T> items;

    //<editor-fold desc="Constructors">
    public DTOSetAdapter(Context context)
    {
        super();
        this.context = context;
        set = new LinkedHashSet<>();
        items = new ArrayList<>();
    }

    public DTOSetAdapter(Context context, Collection<T> objects)
    {
        super();
        this.context = context;
        if (objects == null)
        {
            set = new LinkedHashSet<>();
            items = new ArrayList<>();
        }
        else
        {
            set = new LinkedHashSet<>(objects);
            items = new ArrayList<>(set);
        }

    }
    //</editor-fold>

    /**
     *
     * @param newOnes
     * @return the count of effectively added elements
     */
    public int appendTail(Collection<T> newOnes)
    {
        int beforeCount = set.size();
        int afterCount = beforeCount;
        if (newOnes != null)
        {
            set.addAll(newOnes);
            items = new ArrayList<>(set);
            afterCount = set.size();
        }
        return afterCount - beforeCount;
    }

    /**
     * @param newOnes
     * @return the count of effectively added elements
     */
    public int appendHead(List<T> newOnes)
    {
        int beforeCount = set.size();
        int afterCount = beforeCount;
        if (newOnes != null)
        {
            LinkedHashSet<T> replacement = new LinkedHashSet<>(newOnes);
            replacement.addAll(set);
            set = replacement;
            items = new ArrayList<>(set);
            afterCount = set.size();
        }
        return afterCount - beforeCount;
    }

    @Override public int getCount()
    {
        return items.size();
    }

    @Override public T getItem(int position)
    {
        return items.get(position);
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).hashCode();
    }
}
