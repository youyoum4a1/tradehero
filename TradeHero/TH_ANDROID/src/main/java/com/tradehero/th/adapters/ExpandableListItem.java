package com.ayondo.academy.adapters;

public class ExpandableListItem<T> implements ExpandableItem, OnSizeChangedListener
{
    protected boolean expanded;
    protected final T model;

    //<editor-fold desc="Constructors">
    public ExpandableListItem(T model)
    {
        this.model = model;
    }

    public ExpandableListItem(boolean expanded, T model)
    {
        this.expanded = expanded;
        this.model = model;
    }
    //</editor-fold>

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
