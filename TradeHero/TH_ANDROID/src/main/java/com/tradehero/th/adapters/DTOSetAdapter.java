package com.ayondo.academy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

abstract public class DTOSetAdapter<T> extends BaseAdapter
{
    @NonNull protected final Context context;
    @Nullable protected Comparator<T> comparator;
    @NonNull protected Set<T> set;
    @NonNull private ArrayList<T> items;

    //<editor-fold desc="Constructors">
    public DTOSetAdapter(@NonNull Context context)
    {
        super();
        this.context = context;
        set = createSet(null);
        items = new ArrayList<>();
    }

    public DTOSetAdapter(@NonNull Context context, @Nullable Comparator<T> comparator)
    {
        super();
        this.context = context;
        this.comparator = comparator;
        set = createSet(null);
        items = new ArrayList<>();
    }

    public DTOSetAdapter(@NonNull Context context, @Nullable Collection<? extends T> objects)
    {
        super();
        this.context = context;
        set = createSet(objects);
        items = new ArrayList<>(set);

    }

    public DTOSetAdapter(@NonNull Context context, @Nullable Comparator<T> comparator, @Nullable Collection<? extends T> objects)
    {
        super();
        this.comparator = comparator;
        this.context = context;
        set = createSet(objects);
        items = new ArrayList<>(set);

    }
    //</editor-fold>

    public void clear()
    {
        set.clear();
        items.clear();
    }

    @NonNull protected Set<T> createSet(@Nullable Collection<? extends T> objects)
    {
        Set<T> created;
        if (comparator == null)
        {
            created = new LinkedHashSet<>();
        }
        else
        {
            created = new TreeSet<>(comparator);
        }

        if (objects != null)
        {
            created.addAll(objects);
        }
        return created;
    }

    public void remove(@NonNull T element)
    {
        set.remove(element);
        items = new ArrayList<>(set);
    }

    /**
     *
     * @param newOnes
     * @return the count of effectively added elements
     */
    public int appendTail(@Nullable Collection<? extends T> newOnes)
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
    public int appendHead(@Nullable List<? extends T> newOnes)
    {
        int beforeCount = set.size();
        int afterCount = beforeCount;
        if (newOnes != null)
        {
            Set<T> replacement = createSet(newOnes);
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

    public int getPositionOf(@NonNull T item)
    {
        return items.indexOf(item);
    }
}
