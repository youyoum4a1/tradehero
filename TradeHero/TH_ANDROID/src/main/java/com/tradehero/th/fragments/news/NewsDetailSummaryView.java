package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsDetailSummaryView extends FrameLayout
    implements DTOView<NewsItemDTO>
{
    @InjectView(R.id.news_detail_title_placeholder) ImageView mNewsDetailTitlePlaceholder;
    @InjectView(R.id.news_detail_title_layout_wrapper) LinearLayout mNewsDetailTitleLayoutWrapper;
    @InjectView(R.id.news_detail_title) TextView mNewsDetailTitle;
    @InjectView(R.id.news_detail_date) TextView mNewsDetailDate;

    @Inject PrettyTime prettyTime;

    //<editor-fold desc="Constructors">
    public NewsDetailSummaryView(Context context)
    {
        super(context);
    }

    public NewsDetailSummaryView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsDetailSummaryView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(NewsItemDTO dto)
    {
        if (dto != null)
        {
            if (mNewsDetailTitle != null)
            {
                mNewsDetailTitle.setText(dto.title);
            }

            if (mNewsDetailDate != null)
            {
                mNewsDetailDate.setText(prettyTime.format(dto.createdAtUtc));
            }
        }
    }

    public void setBackground(int resId)
    {
        mNewsDetailTitlePlaceholder.setBackgroundResource(resId);
    }
}
