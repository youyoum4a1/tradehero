package com.tradehero.th.fragments.news;

import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsItemViewHolder extends AbstractDiscussionCompactItemViewHolder
{
    public static boolean IS_AUTO_TRANSLATE = true;

    public static enum TranslationStatus
    {
        ORIGINAL(R.string.discussion_translate_button),
        TRANSLATING(R.string.discussion_translating_button),
        TRANSLATED(R.string.discussion_show_original_button);

        public int actionTextResId;

        TranslationStatus(int actionTextResId)
        {
            this.actionTextResId = actionTextResId;
        }
    }

    @InjectView(R.id.news_title_description) TextView newsDescription;
    @InjectView(R.id.news_title_title) TextView newsTitle;
    @InjectView(R.id.news_source) TextView newsSource;

    @InjectView(R.id.discussion_translate_notice_wrapper) View translateNoticeWrapper;
    @InjectView(R.id.discussion_translate_notice) TextView translateNotice;
    @InjectView(R.id.discussion_translate_notice_image) ImageView translateNoticeImage;

    @InjectView(R.id.discussion_action_button_more) View buttonMore;

    public TranslationStatus currentTranslationStatus = TranslationStatus.ORIGINAL;
    public AbstractDiscussionCompactDTO translatedAbstractDiscussionCompactDTO; // TODO move it
    public TranslationResult latestTranslationResult;
    private OnMenuClickedListener menuClickedListener;

    //<editor-fold desc="Constructors">
    public NewsItemViewHolder()
    {
        super();
    }
    //</editor-fold>

    public void setMenuClickedListener(OnMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    /**
     * TODO this event should be handled by DiscussionActionButtonsView,
     */
    @OnClick(R.id.discussion_action_button_comment_count)
    protected void notifyCommentButtonClicked()
    {
        OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onCommentButtonClicked();
        }
    }

    @OnClick({R.id.discussion_translate_notice_wrapper})
    protected void toggleTranslate()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
                currentTranslationStatus = NewsItemViewHolder.TranslationStatus.TRANSLATING;
                displayTranslatableTexts();
                notifyTranslationRequested();
                break;

            case TRANSLATING:
            case TRANSLATED:
                currentTranslationStatus = NewsItemViewHolder.TranslationStatus.ORIGINAL;
                displayTranslatableTexts();
                break;
        }
    }

    protected void notifyTranslationRequested()
    {
        OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onTranslationRequested();
        }
    }

    @OnClick(R.id.discussion_action_button_more)
    protected void notifyMoreButtonClicked()
    {
        OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onMoreButtonClicked();
        }
    }

    @Override public void linkWith(AbstractDiscussionCompactDTO discussionDTO, boolean andDisplay)
    {
        super.linkWith(discussionDTO, andDisplay);
        this.translatedAbstractDiscussionCompactDTO = null;
        this.currentTranslationStatus = TranslationStatus.ORIGINAL;

        if (andDisplay)
        {
            if (discussionDTO != null)
            {
                displaySource();
                displayTranslatableTexts();
                displayMoreButton();
            }
            else
            {
                resetViews();
            }
        }

        if (IS_AUTO_TRANSLATE)
        {
            notifyTranslationRequested();
        }
    }

    //<editor-fold desc="Display Methods">
    private void resetViews()
    {
        resetTitle();
        resetDescription();
        resetSource();
    }

    private void resetTitle()
    {
        newsTitle.setText(null);
    }

    private void resetDescription()
    {
        newsDescription.setText(null);
    }

    private void resetSource()
    {
        newsSource.setText(null);
    }

    private void displaySource()
    {
        if (abstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
        {
            newsSource.setText(parseHost(((NewsItemCompactDTO) abstractDiscussionCompactDTO).url));
        }
        else if (abstractDiscussionCompactDTO instanceof NewsItemDTO)
        {
            newsSource.setText(parseHost(((NewsItemDTO) abstractDiscussionCompactDTO).url));
        }
        else
        {
            newsSource.setText(R.string.na);
        }
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

    public void displayTranslatableTexts()
    {
        displayTitle();
        displayDescription();
        displayTranslateNotice();
    }

    private void displayTitle()
    {
        newsTitle.setText(getTitleText());
    }

    private String getTitleText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
                if (abstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
                {
                    return ((NewsItemCompactDTO) abstractDiscussionCompactDTO).title;
                }
                else if (abstractDiscussionCompactDTO instanceof NewsItemDTO)
                {
                    return ((NewsItemDTO) abstractDiscussionCompactDTO).title;
                }
                return null;

            case TRANSLATED:
                if (translatedAbstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
                {
                    return ((NewsItemCompactDTO) translatedAbstractDiscussionCompactDTO).title;
                }
                else if (translatedAbstractDiscussionCompactDTO instanceof NewsItemDTO)
                {
                    return ((NewsItemDTO) translatedAbstractDiscussionCompactDTO).title;
                }
                return null;
        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }

    private void displayDescription()
    {
        newsDescription.setText(getDescriptionText());
    }

    private String getDescriptionText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
                if (abstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
                {
                    return ((NewsItemCompactDTO) abstractDiscussionCompactDTO).description;
                }
                else if (abstractDiscussionCompactDTO instanceof NewsItemDTO)
                {
                    return ((NewsItemDTO) abstractDiscussionCompactDTO).description;
                }
                return null;

            case TRANSLATED:
                if (translatedAbstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
                {
                    return ((NewsItemCompactDTO) translatedAbstractDiscussionCompactDTO).description;
                }
                else if (translatedAbstractDiscussionCompactDTO instanceof NewsItemDTO)
                {
                    return ((NewsItemDTO) translatedAbstractDiscussionCompactDTO).description;
                }
                return null;
        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }

    public void displayTranslateNotice()
    {
        translateNoticeWrapper.setVisibility(socialShareHelper.canTranslate(abstractDiscussionCompactDTO) ? View.VISIBLE : View.GONE);
        translateNotice.setText(getTranslateNoticeText());
        if (latestTranslationResult != null)
        {
            translateNoticeImage.setImageResource(latestTranslationResult.logoResId());
        }
    }

    private Spanned getTranslateNoticeText()
    {
        return Html.fromHtml(context.getString(
                R.string.discussion_translate_button_with_powered,
                getTranslateNoticeActionText()));
    }

    private String getTranslateNoticeActionText()
    {
        return context.getString(currentTranslationStatus.actionTextResId);
    }

    private void displayMoreButton()
    {
        buttonMore.setVisibility(View.GONE);
    }
    //</editor-fold>

    public static interface OnMenuClickedListener
    {
        void onCommentButtonClicked();
        void onTranslationRequested();
        void onMoreButtonClicked();
    }
}
