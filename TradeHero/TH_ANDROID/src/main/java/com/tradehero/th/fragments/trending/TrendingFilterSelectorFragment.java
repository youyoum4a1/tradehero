package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 10:32 AM To change this template use File | Settings | File Templates. */
abstract public class TrendingFilterSelectorFragment extends SherlockFragment
{
    public static final String TAG = TrendingFilterSelectorFragment.class.getName();

    private ImageButton mPrevious;
    private ImageButton mNext;
    private TextView mTitle; // The title has a drawable to the left
    private TextView mDescription;
    private Spinner mExchangeSelection;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_trending_filter, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        if (view != null)
        {
            mPrevious = (ImageButton) view.findViewById(R.id.previous_filter);
            mNext = (ImageButton) view.findViewById(R.id.next_filter);
            mTitle = (TextView) view.findViewById(R.id.title);
            mDescription = (TextView) view.findViewById(R.id.description);
            mExchangeSelection = (Spinner) view.findViewById(R.id.exchange_selection);
        }
    }

    abstract int getTitleResId();
    abstract int getDescriptionResId();

    public void display()
    {
        displayTitle();
        displayDescription();
    }

    public void displayTitle()
    {
        if (mTitle != null)
        {
            mTitle.setText(getTitleResId());
        }
    }

    public void displayDescription()
    {
        if (mDescription != null)
        {
            mDescription.setText(getDescriptionResId());
        }
    }


}
