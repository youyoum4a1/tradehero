package com.tradehero.th.widget.list;

import android.view.View;
import android.widget.AdapterView;
import com.tradehero.th.adapters.ExpandableItem;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/24/14 Time: 12:57 PM Copyright (c) TradeHero
 */
public class SingleExpandingListViewListener extends BaseExpandingListViewListener
{
    @Inject public SingleExpandingListViewListener()
    {
        super();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // TODO is there anyway better than looping through the item?
        // idea is to remember a selected position, and only expand that item,
        // problem is adapter does not know about that item's position.
        int firstVisibleItemPosition = parent.getFirstVisiblePosition();
        int lastVisibleItemPosition = parent.getLastVisiblePosition();

        // collapse all visible item within the view
        for (int i = 0; i < parent.getCount(); ++i)
        {
            // let super class take care of clicked item
            if (position == i) continue;

            Object o = parent.getItemAtPosition(i);
            if (o != null  && o instanceof ExpandableItem)
            {
                ((ExpandableItem) o).setExpanded(false);
                if (i >= firstVisibleItemPosition && i <= lastVisibleItemPosition)
                {
                    View child = parent.getChildAt(i);
                    collapseView(child);
                }
            }
        }

        super.onItemClick(parent, view, position, id);
    }
}
