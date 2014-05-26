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
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.persistence.news.NewsItemCompactCacheNew;
import com.tradehero.th.persistence.translation.TranslationCache;
import com.tradehero.th.persistence.translation.TranslationKey;
import java.net.MalformedURLException;
import java.net.URL;
import javax.inject.Inject;

public class NewsHeadlineView extends AbstractDiscussionItemView<NewsItemDTOKey>
        implements THDialog.OnDialogItemClickListener
{
    @InjectView(R.id.news_title_description) TextView newsDescription;
    @InjectView(R.id.news_title_title) TextView newsTitle;
    @InjectView(R.id.news_source) TextView newsSource;

    @Inject NewsItemCompactCacheNew newsItemCompactCache;
    @Inject TranslationCache translationCache;

    private NewsItemCompactDTO newsItemDTO;
    private DTOCache.GetOrFetchTask<TranslationKey, TranslationResult> translationTask;

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

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        detachTranslationTask();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void detachTranslationTask()
    {
        if (translationTask != null)
        {
            translationTask.setListener(null);
        }
        translationTask = null;
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
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.sharing_translation_dialog_layout, null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback) contentView;
        ((NewsDialogLayout) contentView).setNewsData(newsItemDTO, WeChatMessageType.News);
        ((NewsDialogLayout) contentView).setMenuClickedListener(createNewsDialogMenuClickedListener());
        // TODO find a place to unset this listener
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
            args.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY,
                    discussionKey.getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }

    @Override public void display(NewsItemDTOKey discussionKey)
    {
        super.display(discussionKey);
        linkWith(newsItemCompactCache.get(discussionKey), true);

    }

    //@Override
    protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO, boolean andDisplay)
    {
        //super.linkWith(abstractDiscussionDTO, andDisplay);

        if (abstractDiscussionDTO instanceof NewsItemCompactDTO)
        {
            linkWith((NewsItemCompactDTO) abstractDiscussionDTO, andDisplay);
        }
    }

    protected void linkWith(NewsItemCompactDTO newsItemDTO, boolean andDisplay)
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
        } catch (MalformedURLException e)
        {
            return null;
        }
    }

    @Override protected SecurityId getSecurityId()
    {
        throw new IllegalStateException("It has no securityId");
    }

    protected void pushSettingsForConnect(SocialShareFormDTO socialShareFormDTO)
    {
        Bundle args = new Bundle();
        SettingsFragment.putSocialNetworkToConnect(args, socialShareFormDTO);
        ((DashboardActivity) getContext()).getDashboardNavigator().pushFragment(SettingsFragment.class, args);
    }

    protected NewsDialogLayout.OnMenuClickedListener createNewsDialogMenuClickedListener()
    {
        return new NewsHeadlineViewDialogMenuClickedListener();
    }

    private class NewsHeadlineViewDialogMenuClickedListener implements NewsDialogLayout.OnMenuClickedListener
    {
        @Override public void onTranslationRequestedClicked()
        {
            // TODO better
            THToast.show("Temp click for translation");
        }

        @Override public void onShareConnectRequested(SocialShareFormDTO socialShareFormDTO)
        {
            pushSettingsForConnect(socialShareFormDTO);
        }

        @Override public void onShareRequestedClicked()
        {
            // TODO
        }
    }
}
