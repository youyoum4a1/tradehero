package com.tradehero.th.widget.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.yahoo.News;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by julien on 11/10/13
 */
public class YahooNewsView extends LinearLayout implements DTOView<News>
{
    private static final String TAG = YahooNewsView.class.getSimpleName();

    private TextView dateTextView;
    private TextView titleTextView;
    private News news;

    //<editor-fold desc="Constructors">
    public YahooNewsView(Context context)
    {
        this(context, null);
    }

    public YahooNewsView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public YahooNewsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        fetchViews();
    }

        private void fetchViews()
        {
            titleTextView = (TextView)findViewById(R.id.title_yahooNews);
            dateTextView = (TextView)findViewById(R.id.date_yahooNews);
        }

    @Override
    public void display(News dto)
    {
        this.news = dto;
        displayNews();
    }

        private void displayNews()
        {
            if (news == null) return;

            if (titleTextView != null)
                titleTextView.setText(news.getTitle());

            if (dateTextView != null && news.getDate() != null)
            {
                PrettyTime prettyTime = new PrettyTime();
                dateTextView.setText(prettyTime.format(news.getDate()));
            }

        }
}
