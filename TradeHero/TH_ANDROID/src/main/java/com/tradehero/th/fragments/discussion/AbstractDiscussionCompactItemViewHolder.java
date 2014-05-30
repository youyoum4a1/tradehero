package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.VotePair;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class AbstractDiscussionCompactItemViewHolder<DiscussionDTOType extends AbstractDiscussionCompactDTO>
{
    public static boolean IS_AUTO_TRANSLATE = false;

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

    @InjectView(R.id.vote_pair) @Optional protected VotePair votePair;
    @InjectView(R.id.discussion_time) protected TextView time;
    @InjectView(R.id.discussion_action_button_comment_count) @Optional protected CompoundButton commentCountView;
    @InjectView(R.id.discussion_action_button_more) protected View buttonMore;

    @InjectView(R.id.private_text_stub_container) @Optional protected  View stubTextContainer;
    @InjectView(R.id.discussion_stub_content) @Optional protected  TextView stubContent;

    @InjectView(R.id.discussion_translate_notice_wrapper) @Optional protected View translateNoticeWrapper;
    @InjectView(R.id.discussion_translate_notice) @Optional protected TextView translateNotice;
    @InjectView(R.id.discussion_translate_notice_image) @Optional protected ImageView translateNoticeImage;

    @Inject protected PrettyTime prettyTime;
    @Inject protected Context context;
    @Inject protected SocialShareTranslationHelper socialShareHelper;

    protected DiscussionDTOType discussionDTO;
    protected DiscussionDTOType translatedDiscussionDTO;
    protected TranslationStatus currentTranslationStatus = TranslationStatus.ORIGINAL;
    protected TranslationResult latestTranslationResult;
    protected OnMenuClickedListener menuClickedListener;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewHolder()
    {
        super();
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    public void setMenuClickedListener(OnMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    public void linkWith(DiscussionDTOType discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;
        this.translatedDiscussionDTO = null;
        this.currentTranslationStatus = TranslationStatus.ORIGINAL;

        if (andDisplay)
        {
            display();
        }

        if (isAutoTranslate())
        {
            notifyTranslationRequested();
        }
    }

    public boolean isAutoTranslate()
    {
        return IS_AUTO_TRANSLATE;
    }

    public void linkWithTranslated(DiscussionDTOType translatedDiscussionDTO, boolean andDisplay)
    {
        this.translatedDiscussionDTO = translatedDiscussionDTO;
        if (currentTranslationStatus == TranslationStatus.TRANSLATING)
        {
            currentTranslationStatus = TranslationStatus.TRANSLATED;
        }
        displayTranslatableTexts();
    }

    public void setLatestTranslationResult(TranslationResult latestTranslationResult)
    {
        this.latestTranslationResult = latestTranslationResult;
        displayTranslateNotice();
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayInProcess();
        displayVotePair();
        displayCommentCount();
        displayTime();
        displayTranslatableTexts();
        displayMoreButton();
    }

    protected void displayInProcess()
    {
        if (stubTextContainer != null)
        {
            stubTextContainer.setVisibility(isInProcess() ? View.VISIBLE : View.GONE);
        }
    }

    public boolean isInProcess()
    {
        return discussionDTO != null && discussionDTO.isInProcess();
    }

    protected void displayVotePair()
    {
        if (votePair != null)
        {
            votePair.display(discussionDTO);
        }
    }

    protected void displayTime()
    {
        if (time != null)
        {
            if (discussionDTO.createdAtUtc != null)
            {
                time.setText(prettyTime.formatUnrounded(discussionDTO.createdAtUtc));
            }
        }
    }

    protected void displayCommentCount()
    {
        if (commentCountView != null)
        {
            commentCountView.setText(String.valueOf(discussionDTO.commentCount));
            commentCountView.setChecked(discussionDTO.commentCount > 0);
        }
    }

    public void displayTranslatableTexts()
    {
        displayTranslateNotice();
    }

    public void displayTranslateNotice()
    {
        if (translateNoticeWrapper != null)
        {
            translateNoticeWrapper.setVisibility(socialShareHelper.canTranslate(discussionDTO) ? View.VISIBLE : View.GONE);
        }
        if (translateNotice != null)
        {
            translateNotice.setText(getTranslateNoticeText());
        }
        if (translateNoticeImage != null && latestTranslationResult != null)
        {
            translateNoticeImage.setImageResource(latestTranslationResult.logoResId());
        }
    }

    public Spanned getTranslateNoticeText()
    {
        return Html.fromHtml(context.getString(
                R.string.discussion_translate_button_with_powered,
                getTranslateNoticeActionText()));
    }

    public String getTranslateNoticeActionText()
    {
        return context.getString(currentTranslationStatus.actionTextResId);
    }

    protected void displayMoreButton()
    {
        if (buttonMore != null)
        {
            buttonMore.setVisibility(View.GONE);
        }
    }
    //</editor-fold>

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

    @OnClick({R.id.discussion_translate_notice_wrapper}) @Optional
    protected void toggleTranslate()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
                notifyTranslationRequested();
                break;

            case TRANSLATING:
            case TRANSLATED:
                currentTranslationStatus = TranslationStatus.ORIGINAL;
                displayTranslatableTexts();
                break;
        }
    }

    protected void notifyTranslationRequested()
    {
        currentTranslationStatus = TranslationStatus.TRANSLATING;
        displayTranslateNotice();
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

    public static interface OnMenuClickedListener
    {
        void onCommentButtonClicked();
        void onTranslationRequested();
        void onMoreButtonClicked();
    }
}
