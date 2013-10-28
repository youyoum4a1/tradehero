package com.tradehero.th.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.R;

/**
 * Listens for item clicks and expands or collapses the selected view depending on
 * its current state.
 * Created by julien on 24/10/13
 */
public class ExpandingListView extends ListView
{
    //<editor-fold desc="Constructors">
    public ExpandingListView(Context context)
    {
        super(context);
        init();
    }

    public ExpandingListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ExpandingListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    private void init()
    {
        super.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                ExpandableListItem viewObject = (ExpandableListItem) getItemAtPosition(getPositionForView(view));
                if (!viewObject.isExpanded())
                {
                    expandView(view);
                }
                else
                {
                    collapseView(view);
                }
            }
        });
    }

    @Override public void setOnItemClickListener(OnItemClickListener listener)
    {
        throw new IllegalArgumentException("You are trying to override the default listener");
    }

    private void expandView(View view)
    {
        ExpandableListItem viewObject = (ExpandableListItem) getItemAtPosition(getPositionForView(view));

        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.VISIBLE);
        viewObject.setExpanded(true);
    }

    private void collapseView(View view)
    {
        ExpandableListItem viewObject = (ExpandableListItem) getItemAtPosition(getPositionForView(view));

        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.GONE);
        viewObject.setExpanded(false);
    }
}
