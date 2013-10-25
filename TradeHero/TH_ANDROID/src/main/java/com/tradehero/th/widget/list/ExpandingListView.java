package com.tradehero.th.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.R;

/**
 * Created by julien on 24/10/13
 */
public class ExpandingListView extends ListView
{

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

    private void init() {
        setOnItemClickListener(mItemClickListener);
    }

    /**
     * Listens for item clicks and expands or collapses the selected view depending on
     * its current state.
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            ExpandableListItem viewObject = (ExpandableListItem)getItemAtPosition(getPositionForView
                    (view));
            if (!viewObject.isExpanded()) {
                expandView(view);
            } else {
                collapseView(view);
            }
        }

    };

    private void expandView(View view)
    {
        ExpandableListItem viewObject = (ExpandableListItem)getItemAtPosition(getPositionForView
                (view));

        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.VISIBLE);
        viewObject.setExpanded(true);
    }

    private void collapseView(View view)
    {
        ExpandableListItem viewObject = (ExpandableListItem)getItemAtPosition(getPositionForView
                (view));

        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.GONE);
        viewObject.setExpanded(false);
    }
}
