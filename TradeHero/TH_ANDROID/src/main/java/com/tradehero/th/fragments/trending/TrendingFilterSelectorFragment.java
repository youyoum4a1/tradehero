package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 10:32 AM To change this template use File | Settings | File Templates. */
abstract public class TrendingFilterSelectorFragment extends SherlockFragment
{
    public static final String TAG = TrendingFilterSelectorFragment.class.getName();

    protected ImageButton mPrevious;
    protected ImageButton mNext;
    private TextView mTitle;
    private ImageView mTitleIcon;
    private TextView mDescription;
    private Spinner mExchangeSelection;
    private OnResumedListener onResumedListener;

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
            mTitleIcon = (ImageView) view.findViewById(R.id.trending_filter_title_icon);
            mDescription = (TextView) view.findViewById(R.id.description);
            mExchangeSelection = (Spinner) view.findViewById(R.id.exchange_selection);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        display();
        notifyOnResumedListener();
    }

    @Override public void onDestroyView()
    {
        onResumedListener = null;

        if (mPrevious != null)
        {
            mPrevious.setOnClickListener(null);
        }
        if (mNext != null)
        {
            mNext.setOnClickListener(null);
        }
        super.onDestroyView();
    }

    abstract int getTitleResId();
    abstract int getTitleLeftDrawableResId();
    abstract int getDescriptionResId();

    public void display()
    {
        displayPreviousButton();
        displayNextButton();
        displayTitle();
        displayTitleIcon();
        displayDescription();
    }

    public void displayPreviousButton()
    {
        if (mPrevious != null)
        {
            mPrevious.setVisibility(View.VISIBLE);
        }
    }

    public void displayNextButton()
    {
        if (mNext != null)
        {
            mNext.setVisibility(View.VISIBLE);
        }
    }

    public void displayTitle()
    {
        if (mTitle != null)
        {
            mTitle.setText(getTitleResId());
        }
    }

    public void displayTitleIcon()
    {
        if (mTitleIcon != null)
        {
            mTitleIcon.setImageResource(getTitleLeftDrawableResId());
        }
    }

    public void displayDescription()
    {
        if (mDescription != null)
        {
            mDescription.setText(getDescriptionResId());
        }
    }

    private void handlePreviousClicked()
    {
        THToast.show("Nothing for now");
    }

    private void handleNextClicked()
    {
        THToast.show("Nothing for now");
    }

    public void setOnResumedListener(OnResumedListener onResumedListener)
    {
        this.onResumedListener = onResumedListener;
    }

    private void notifyOnResumedListener()
    {
        OnResumedListener onResumedListenerCopy = onResumedListener;
        if (onResumedListenerCopy != null)
        {
            onResumedListenerCopy.onResumed(this);
        }
    }

    public interface OnResumedListener
    {
        void onResumed(Fragment fragment);
    }
}
