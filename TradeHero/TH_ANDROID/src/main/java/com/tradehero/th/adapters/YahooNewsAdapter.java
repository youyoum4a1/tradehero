package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.widget.trade.YahooNewsView;

/**
 * Created by julien on 11/10/13
 */
public class YahooNewsAdapter  extends DTOAdapter<News, YahooNewsView>
{
    private final static String TAG = YahooNewsAdapter.class.getSimpleName();

    public YahooNewsAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected View getView(int position, final YahooNewsView convertView)
    {
        return convertView;
    }
}
