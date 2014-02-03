package com.tradehero.th.adapters;

/**
 * Created by julien on 24/10/13
 */
public class ExpandableListItem<T> implements
        ExpandableItem,
        OnSizeChangedListener
{
    protected boolean expanded;
    protected T model;

    public ExpandableListItem(T model)
    {
        this.model = model;
    }

    public T getModel()
    {
        return model;
    }

    @Override public void onSizeChanged(int newHeight)
    {
    }

    @Override public boolean isExpanded()
    {
        return expanded;
    }

    @Override public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }

    @Override public String toString()
    {
        return "ExpandableListItem{" +
                "expanded=" + expanded +
                ", model=" + model +
                '}';
    }
}
