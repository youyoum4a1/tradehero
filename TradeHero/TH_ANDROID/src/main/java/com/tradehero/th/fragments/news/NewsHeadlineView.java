package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FontUtil;
import java.net.MalformedURLException;
import java.net.URL;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by julien on 11/10/13
 *
 * modified by Wang Liang.
 */
public class NewsHeadlineView extends LinearLayout implements DTOView<NewsItemDTO>, THDialog.OnDialogItemClickListener
{
    @InjectView(R.id.news_title_date) TextView dateTextView;
    @InjectView(R.id.news_title_description) TextView descView;
    @InjectView(R.id.news_action_button_comment) View actionCommentView;
    @InjectView(R.id.news_title_title) TextView titleTextView;
    @InjectView(R.id.news_item_layout_wrapper) View titleViewWrapper;
    @InjectView(R.id.news_item_placeholder) View placeHolderView;
    @InjectView(R.id.news_action_tv_more) View moreView;
    @InjectView(R.id.news_action_tv_more) TextView more;

    @Inject FontUtil fontUtil;

    private NewsItemDTO newsHeadline;

    //<editor-fold desc="Constructors">
    public NewsHeadlineView(Context context)
    {
        super(context);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        initView();
    }

    private void initView()
    {
        fontUtil.setTypeFace(more, FontUtil.FontType.AWESOME);
    }

    @Override
    public void onClick(int whichButton)
    {
        switch (whichButton)
        {
            case 0:
                break;
            case 1:
                break;
        }
    }

    /**
     * show dialog including sharing and translation.
     */
    @OnClick(R.id.news_action_tv_more) void showShareDialog()
    {
        //THDialog.showUpDialog(getContext(),null, new String[]{"Translation","Share"},null,this,null);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.sharing_translation_dialog_layout, null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback) contentView;
        ((NewsDialogLayout) contentView).setNewsData(newsHeadline, true);
        THDialog.showUpDialog(getContext(), contentView, callback);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(NewsItemDTO dto)
    {
        this.newsHeadline = dto;
        displayNews();
    }

    private String parseHost(String url)
    {
        try
        {
            String host = new URL(url).getHost();
            return host;
        }
        catch (MalformedURLException e)
        {
            return null;
        }
    }

    private void displayNews()
    {
        if (newsHeadline == null)
        {
            return;
        }

        if (titleTextView != null)
        {
            titleTextView.setText(newsHeadline.title);
        }

        if (dateTextView != null && newsHeadline.createdAtUtc != null)
        {
            PrettyTime prettyTime = new PrettyTime();
            StringBuilder sb = new StringBuilder();
            String text = prettyTime.format(newsHeadline.createdAtUtc);
            sb.append(text);
            if (newsHeadline.url != null)
            {
                String source = parseHost(newsHeadline.url);
                if (source != null)
                {
                    sb.append(" via ").append(source);
                }
            }
            dateTextView.setText(sb.toString());
        }

        if (descView != null)
        {
            descView.setText(newsHeadline.description);
            //            if (TextUtils.isEmpty(newsHeadline.description)) {
            //                descView.setVisibility(View.GONE);
            //                Timber.d("newsHeadline description %s empty",newsHeadline.description);
            //            }else {
            //                descView.setVisibility(View.VISIBLE);
            //                Timber.d("newsHeadline description %s not empty",newsHeadline.description);
            //            }
        }

        //        int h = titleViewWrapper.getMeasuredHeight();
        //        if(h <= 0){
        //            titleViewWrapper.measure(MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT,View.MeasureSpec.AT_MOST
        //                   ), MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,View.MeasureSpec.AT_MOST));
        //            h = titleViewWrapper.getMeasuredHeight();
        //        }
        //        ViewGroup.LayoutParams lp = placeHolderView.getLayoutParams();
        //        lp.height = h;
        //        placeHolderView.setLayoutParams(lp);
    }
}
