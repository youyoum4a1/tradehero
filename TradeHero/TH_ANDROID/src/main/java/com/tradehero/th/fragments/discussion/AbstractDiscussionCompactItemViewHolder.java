package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.utils.DaggerUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ocpsoft.prettytime.PrettyTime;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class AbstractDiscussionCompactItemViewHolder<DiscussionDTOType extends AbstractDiscussionCompactDTO>
{
    public static enum TranslationStatus
    {
        ORIGINAL(R.string.discussion_translate_button),
        TRANSLATING(R.string.discussion_translating_button),
        TRANSLATED(R.string.discussion_show_original_button),
        FAILED(R.string.discussion_translation_failed_button);

        public final int actionTextResId;

        TranslationStatus(int actionTextResId)
        {
            this.actionTextResId = actionTextResId;
        }
    }

    @InjectView(R.id.discussion_action_buttons) @Optional public DiscussionActionButtonsView discussionActionButtonsView;
    @InjectView(R.id.discussion_time) protected TextView time;

    @InjectView(R.id.private_text_stub_container) @Optional protected View stubTextContainer;
    @InjectView(R.id.discussion_stub_content) @Optional protected TextView stubContent;

    @InjectView(R.id.discussion_translate_notice_wrapper) @Optional protected View translateNoticeWrapper;
    @InjectView(R.id.discussion_translate_notice) @Optional protected TextView translateNotice;
    @InjectView(R.id.discussion_translate_notice_image) @Optional protected ImageView translateNoticeImage;

    @Inject @NotNull protected PrettyTime prettyTime;
    @Inject @NotNull protected Context context;
    @Inject @NotNull protected SocialShareHelper socialShareHelper;

    protected boolean downVote;
    protected DiscussionDTOType discussionDTO;
    protected DiscussionDTOType translatedDiscussionDTO;
    protected @NotNull TranslationStatus currentTranslationStatus = TranslationStatus.ORIGINAL;
    protected DiscussionActionButtonsView.OnButtonClickedListener menuClickedListener;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewHolder()
    {
        super();
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    public void onFinishInflate(@NotNull View view)
    {
        ButterKnife.inject(this, view);
    }

    public void onAttachedToWindow(@NotNull View view)
    {
        ButterKnife.inject(this, view);
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setButtonClickedListener(createDiscussionActionButtonsViewClickedListener());
        }
    }

    public void onDetachedFromWindow()
    {
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setButtonClickedListener(null);
        }
        socialShareHelper.onDetach();
        ButterKnife.reset(this);
    }

    public void setBackroundResource(int resId)
    {
        //Do nothing
    }

    public void setMenuClickedListener(DiscussionActionButtonsView.OnButtonClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    public void linkWith(DiscussionDTOType discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;
        this.translatedDiscussionDTO = null;
        this.currentTranslationStatus = TranslationStatus.ORIGINAL;

        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.linkWith(discussionDTO, andDisplay);
        }

        if (andDisplay)
        {
            display();
        }

    }

    public void setDownVote(boolean downVote)
    {
        this.downVote = downVote;
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setDownVote(downVote);
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayInProcess();
        displayTime();
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

    protected void displayTime()
    {
        if (time != null)
        {
            time.setText(getTimeToDisplay());
        }
    }

    @Nullable
    protected String getTimeToDisplay()
    {
        if (discussionDTO != null && discussionDTO.createdAtUtc != null)
        {
            return prettyTime.formatUnrounded(discussionDTO.createdAtUtc);
        }
        return null;
    }

    //</editor-fold>

    protected void notifyCommentButtonClicked()
    {
        DiscussionActionButtonsView.OnButtonClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onCommentButtonClicked();
        }
    }

    protected void notifyShareRequested()
    {
        socialShareHelper.share(discussionDTO);
        DiscussionActionButtonsView.OnButtonClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onShareButtonClicked();
        }
    }

    protected void notifyMoreButtonClicked()
    {
        DiscussionActionButtonsView.OnButtonClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onMoreButtonClicked();
        }
    }

    protected DiscussionActionButtonsView.OnButtonClickedListener createDiscussionActionButtonsViewClickedListener()
    {
        return new AbstractDiscussionCompactItemViewHolderActionButtonsClickedListener();
    }

    protected class AbstractDiscussionCompactItemViewHolderActionButtonsClickedListener implements DiscussionActionButtonsView.OnButtonClickedListener
    {
        @Override public void onCommentButtonClicked()
        {
            notifyCommentButtonClicked();
        }

        @Override public void onShareButtonClicked()
        {
            notifyShareRequested();
        }

        @Override public void onMoreButtonClicked()
        {
            notifyMoreButtonClicked();
        }
    }
}
