package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.news.NewsHeadline;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by julien on 11/10/13
 */
public class NewsHeadlineView extends LinearLayout implements DTOView<NewsHeadline>
{
    private static final String TAG = NewsHeadlineView.class.getSimpleName();

    private TextView dateTextView;
    private TextView titleTextView;
    private NewsHeadline newsHeadline;

    //<editor-fold desc="Constructors">
    public NewsHeadlineView(Context context)
    {
        this(context, null);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs, int defStyle)
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
        titleTextView = (TextView) findViewById(R.id.news_title_title);
        dateTextView = (TextView) findViewById(R.id.news_title_date);
    }

    @Override public void display(NewsHeadline dto)
    {
        this.newsHeadline = dto;
        displayNews();
    }

    private void displayNews()
    {
        if (newsHeadline == null)
        {
            return;
        }

        if (titleTextView != null)
        {
            titleTextView.setText(newsHeadline.getTitle());
        }

        if (dateTextView != null && newsHeadline.getDate() != null)
        {
            PrettyTime prettyTime = new PrettyTime();
            dateTextView.setText(prettyTime.format(newsHeadline.getDate()));
        }
    }
}
