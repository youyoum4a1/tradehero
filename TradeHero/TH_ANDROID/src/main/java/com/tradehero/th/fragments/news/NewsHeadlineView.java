package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;
import com.tradehero.th.utils.DaggerUtils;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by julien on 11/10/13
 *
 * modified by Wang Liang.
 */
public class  NewsHeadlineView extends AbstractDiscussionItemView<NewsItemDTOKey>
        implements THDialog.OnDialogItemClickListener
{
    @InjectView(R.id.news_title_description) TextView newsDescription;
    @InjectView(R.id.news_title_title) TextView newsTitle;

    private NewsItemDTO newsItemDTO;

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

        ButterKnife.inject((NewsHeadlineView) this, this);
        DaggerUtils.inject(this);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Related to share dialog">
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
        ((NewsDialogLayout) contentView).setNewsData(newsItemDTO, true);
        THDialog.showUpDialog(getContext(), contentView, callback);
    }
    //</editor-fold>

    @Override public void display(NewsItemDTOKey discussionKey)
    {
        super.display(discussionKey);
    }

    @Override protected void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);

        if (abstractDiscussionDTO instanceof NewsItemDTO)
        {
            linkWith((NewsItemDTO) abstractDiscussionDTO, andDisplay);
        }
    }

    protected void linkWith(NewsItemDTO newsItemDTO, boolean andDisplay)
    {
        this.newsItemDTO = newsItemDTO;

        if (andDisplay)
        {
            if (newsItemDTO != null)
            {
                displayTitle();
                displayDescription();
                displaySource();
            }
        }
    }

    private void displaySource()
    {
        parseHost(newsItemDTO.url);
    }

    private void displayDescription()
    {
        if (newsDescription != null)
        {
            newsDescription.setText(newsItemDTO.description);
        }
    }

    private void displayTitle()
    {
        if (newsTitle != null)
        {
            newsTitle.setText(newsItemDTO.title);
        }
    }

    private String parseHost(String url)
    {
        try
        {
            return new URL(url).getHost();
        }
        catch (MalformedURLException e)
        {
            return null;
        }
    }
}
