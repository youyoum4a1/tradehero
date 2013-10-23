package com.tradehero.th.fragments.leaderboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: tho Date: 10/23/13 Time: 3:23 PM Copyright (c) TradeHero */
public class LeaderboardSortTypeSelectorDialog extends AlertDialog
{
    private OnSortTypeChangedListener listener;

    //<editor-fold desc="Constructors">
    protected LeaderboardSortTypeSelectorDialog(Context context, OnSortTypeChangedListener listener)
    {
        super(context);
        this.listener = listener;
    }

    protected LeaderboardSortTypeSelectorDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
    }

    protected LeaderboardSortTypeSelectorDialog(Context context, int theme)
    {
        super(context, theme);
    }


    //</editor-fold>

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.leaderboard_sort_type_selector);

        setButton(BUTTON_NEGATIVE, getContext().getString(R.string.done), new OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                if (listener!=null)
                {
                    // get the selected sort type for the callback
                    //listener.onSortTypeChanged();
                }
            }
        });

        setButton(BUTTON_POSITIVE, getContext().getString(R.string.cancel), new OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                // do nothing for now
            }
        });
    }

    public static interface OnSortTypeChangedListener
    {
        void onSortTypeChanged(LeaderboardSortType newSortType);
    }
}
