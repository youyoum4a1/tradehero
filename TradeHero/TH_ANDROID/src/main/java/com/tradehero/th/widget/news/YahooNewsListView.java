package com.tradehero.th.widget.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.YahooNewsAdapter;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.yahoo.NewsList;

/** Created with IntelliJ IDEA. User: xavier Date: 10/31/13 Time: 11:32 AM To change this template use File | Settings | File Templates. */
public class YahooNewsListView extends ListView implements DTOView<NewsList>
{
    public static final String TAG = YahooNewsListView.class.getSimpleName();
    public static final int DEFAULT_YAHOO_NEWS_LAYOUT_RES_ID = R.layout.yahoo_news_item;

    private YahooNewsAdapter adapter;
    private int yahooNewsItemLayoutResId = DEFAULT_YAHOO_NEWS_LAYOUT_RES_ID;
    private NewsList newsList;

    //<editor-fold desc="Constructors">
    public YahooNewsListView(Context context)
    {
        super(context);
    }

    public YahooNewsListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public YahooNewsListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    public void onDestroyView()
    {
        setAdapter(null);
        adapter = null;
    }

    //<editor-fold desc="Accessors">
    public int getYahooNewsItemLayoutResId()
    {
        return yahooNewsItemLayoutResId;
    }

    public void setYahooNewsItemLayoutResId(int yahooNewsItemLayoutResId)
    {
        this.yahooNewsItemLayoutResId = yahooNewsItemLayoutResId;
    }
    //</editor-fold>

    public void setAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        setYahooNewsItemLayoutResId(layoutResourceId);
        setAdapter(context, inflater);
    }

    public void setAdapter(Context context, LayoutInflater inflater)
    {
        adapter = new YahooNewsAdapter(context, inflater, this.yahooNewsItemLayoutResId);
        setAdapter(adapter);
    }

    @Override public void display(NewsList newsList)
    {
        this.newsList = newsList;

        adapter.setItems(newsList);
        post(new Runnable()
        {
            @Override public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
