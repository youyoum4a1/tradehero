package com.ayondo.academy.widget.list;

import android.view.View;
import android.widget.AdapterView;
import com.ayondo.academy.adapters.ExpandableItem;
import javax.inject.Inject;

public class SingleExpandingListViewListener extends BaseExpandingListViewListener
{
    private int oldSelectedPostion;

    @Inject public SingleExpandingListViewListener()
    {
        super();
        oldSelectedPostion = -1;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if ((oldSelectedPostion != -1) && (position != oldSelectedPostion))
        {
            Object item = parent.getItemAtPosition(oldSelectedPostion);
            if (item instanceof ExpandableItem)
            {
                View child = parent.getChildAt(oldSelectedPostion);
                collapseView(child);
                ((ExpandableItem) item).setExpanded(false);
            }
        }

        oldSelectedPostion = position;

        super.onItemClick(parent, view, position, id);
    }
}
