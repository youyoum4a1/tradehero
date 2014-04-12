package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
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
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
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
    @InjectView(R.id.news_source) TextView newsSource;

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
        ButterKnife.inject(this);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Related to share dialog">
    // TODO

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
    @OnClick(R.id.discussion_action_button_more) void showShareDialog()
    {
        //THDialog.showUpDialog(getContext(),null, new String[]{"Translation","Share"},null,this,null);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.sharing_translation_dialog_layout, null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback) contentView;
        ((NewsDialogLayout) contentView).setNewsData(newsItemDTO, true);
        THDialog.showUpDialog(getContext(), contentView, callback);
    }
    //</editor-fold>

    /**
     * TODO this event should be handled by DiscussionActionButtonsView,
     */
    @OnClick(R.id.discussion_action_button_comment_count) void onActionButtonCommentCountClicked()
    {
        if (discussionKey != null)
        {
            Bundle args = new Bundle();
            args.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }

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
            else
            {
                resetViews();
            }
        }
    }

    private void resetViews()
    {
        resetTitle();
        resetDescription();
        resetSource();
    }

    private void displaySource()
    {
        newsSource.setText(parseHost(newsItemDTO.url));
    }

    private void resetSource()
    {
        newsSource.setText(null);
    }

    private void displayDescription()
    {
        newsDescription.setText(newsItemDTO.description);
    }

    private void resetDescription()
    {
        newsDescription.setText(null);
    }

    private void displayTitle()
    {
        newsTitle.setText(newsItemDTO.title);
    }

    private void resetTitle()
    {
        newsTitle.setText(null);
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
