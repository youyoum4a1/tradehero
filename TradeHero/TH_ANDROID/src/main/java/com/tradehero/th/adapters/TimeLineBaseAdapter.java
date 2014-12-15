package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import dagger.Lazy;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by huhaiping on 14-9-19.
 */
public class TimeLineBaseAdapter extends BaseAdapter
{

    public TimeLineOperater timeLineOperater;
    @Inject public Lazy<PrettyTime> prettyTime;
    public Context context;
    public LayoutInflater inflater;


    @Override public int getCount()
    {
        return 0;
    }

    @Override public Object getItem(int i)
    {
        return null;
    }

    @Override public long getItemId(int i)
    {
        return 0;
    }

    @Override public View getView(int i, View view, ViewGroup viewGroup)
    {
        return null;
    }

    public static interface TimeLineOperater
    {
        void OnTimeLineItemClicked(int position);

        void OnTimeLinePraiseClicked(int position);

        void OnTimeLinePraiseDownClicked(int position);

        void OnTimeLineCommentsClicked(int position);

        void OnTimeLineShareClicked(int position);

        void OnTimeLineBuyClicked(int position);
    }
}
