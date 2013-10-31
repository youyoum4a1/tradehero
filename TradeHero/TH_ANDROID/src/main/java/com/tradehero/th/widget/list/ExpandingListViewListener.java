package com.tradehero.th.widget.list;

import android.view.View;
import android.widget.AdapterView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/31/13 Time: 4:15 PM Copyright (c) TradeHero */
public class ExpandingListViewListener implements AdapterView.OnItemClickListener
{
    @Inject
    public ExpandingListViewListener()
    {
        super();
    }

    private void expandView(View view)
    {
        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.VISIBLE);
    }

    private void collapseView(View view)
    {
        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.GONE);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        ExpandableListItem viewObject = (ExpandableListItem) parent.getItemAtPosition(parent.getPositionForView(view));
        if (!viewObject.isExpanded())
        {
            viewObject.setExpanded(true);
            expandView(view);
        }
        else
        {
            viewObject.setExpanded(false);
            collapseView(view);
        }
    }
}
