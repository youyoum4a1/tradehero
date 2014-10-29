package com.tradehero.th.rx.view.list;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;

class CompositeOnItemClickListener implements AbsListView.OnItemClickListener
{
    private final List<AbsListView.OnItemClickListener> listeners = new ArrayList<>();

    public boolean addOnClickListener(final AbsListView.OnItemClickListener listener)
    {
        return listeners.add(listener);
    }

    public boolean removeOnClickListener(final AbsListView.OnItemClickListener listener)
    {
        return listeners.remove(listener);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        for (final AbsListView.OnItemClickListener listener : listeners)
        {
            listener.onItemClick(parent, view, position, id);
        }
    }
}
