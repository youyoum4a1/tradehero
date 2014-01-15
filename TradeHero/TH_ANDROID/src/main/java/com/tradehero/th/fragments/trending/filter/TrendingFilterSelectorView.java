package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.tradehero.th.R;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterSelectorView extends RelativeLayout
{
    public static final String TAG = TrendingFilterSelectorView.class.getSimpleName();

    public ImageButton mPrevious;
    public ImageButton mNext;
    public TextView mTitle;
    public ImageView mTitleIcon;
    public TextView mDescription;
    public Spinner mExchangeSelection;

    //<editor-fold desc="Constructors">
    public TrendingFilterSelectorView(Context context)
    {
        super(context);
    }

    public TrendingFilterSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TrendingFilterSelectorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        mPrevious = (ImageButton) findViewById(R.id.previous_filter);
        mNext = (ImageButton) findViewById(R.id.next_filter);
        mTitle = (TextView) findViewById(R.id.title);
        mTitleIcon = (ImageView) findViewById(R.id.trending_filter_title_icon);
        mDescription = (TextView) findViewById(R.id.description);
        mExchangeSelection = (Spinner) findViewById(R.id.exchange_selection);
    }

    public void onDestroy()
    {
        if (mPrevious != null)
        {
            mPrevious.setOnClickListener(null);
        }
        mPrevious = null;

        if (mNext != null)
        {
            mNext.setOnClickListener(null);
        }
        mNext = null;

        if (mExchangeSelection != null)
        {
            mExchangeSelection.setOnItemSelectedListener(null);
            mExchangeSelection.setAdapter(null);
        }
        mExchangeSelection = null;
    }

    public void apply(TrendingFilterTypeDTO typeDTO)
    {
        if (typeDTO != null)
        {
            if (mPrevious != null)
            {
                mPrevious.setEnabled(typeDTO.hasPreviousButton);
            }

            if (mNext != null)
            {
                mNext.setEnabled(typeDTO.hasNextButton);
            }

            if (mTitle != null)
            {
                mTitle.setText(typeDTO.titleResId);
            }

            if (mTitleIcon != null)
            {
                mTitleIcon.setImageResource(typeDTO.titleIconResId);
            }

            if (mDescription != null)
            {
                mDescription.setText(typeDTO.descriptionResId);
            }
        }
    }
}
