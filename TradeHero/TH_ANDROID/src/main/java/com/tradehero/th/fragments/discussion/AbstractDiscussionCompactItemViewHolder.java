package com.tradehero.th.fragments.discussion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class AbstractDiscussionCompactItemViewHolder<DiscussionDTOType extends AbstractDiscussionCompactDTO>
{
    public static enum TranslationStatus
    {
        ORIGINAL(R.string.discussion_translate_button),
        TRANSLATING(R.string.discussion_translating_button),
        TRANSLATED(R.string.discussion_show_original_button),
        FAILED(R.string.discussion_translation_failed_button),
        ;

        @StringRes public final int actionTextResId;

        //<editor-fold desc="Constructors">
        TranslationStatus(@StringRes int actionTextResId)
        {
            this.actionTextResId = actionTextResId;
        }
        //</editor-fold>
    }

    @InjectView(R.id.discussion_action_buttons) @Optional public DiscussionActionButtonsView discussionActionButtonsView;
    @InjectView(R.id.discussion_time) protected TextView time;

    @InjectView(R.id.private_text_stub_container) @Optional protected View stubTextContainer;
    @InjectView(R.id.discussion_stub_content) @Optional protected TextView stubContent;

    @InjectView(R.id.discussion_translate_notice_wrapper) @Optional protected View translateNoticeWrapper;
    @InjectView(R.id.discussion_translate_notice) @Optional protected TextView translateNotice;
    @InjectView(R.id.discussion_translate_notice_image) @Optional protected ImageView translateNoticeImage;

    @Inject @NonNull protected PrettyTime prettyTime;
    @Inject @NonNull protected SocialShareTranslationHelper socialShareHelper;
    protected final Context context;

    protected boolean downVote;
    @Nullable protected DiscussionDTOType discussionDTO;
    @Nullable protected DiscussionDTOType translatedDiscussionDTO;
    protected @NonNull TranslationStatus currentTranslationStatus = TranslationStatus.ORIGINAL;
    protected TranslationResult latestTranslationResult;
    protected OnMenuClickedListener menuClickedListener;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewHolder(Context context)
    {
        this.context = context;
    }
    //</editor-fold>

    public void onFinishInflate(@NonNull View view)
    {
        HierarchyInjector.inject(view.getContext(), this);
        ButterKnife.inject(this, view);
        socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
    }

    public void onAttachedToWindow(@NonNull View view)
    {
        ButterKnife.inject(this, view);
        socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setButtonClickedListener(createDiscussionActionButtonsViewClickedListener());
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onDetachedFromWindow()
    {
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setButtonClickedListener(null);
        }
        socialShareHelper.onDetach();
        ButterKnife.reset(this);
    }

    public void setBackgroundResource(int resId)
    {
        //Do nothing
    }

    public void setMenuClickedListener(OnMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    public void linkWith(DiscussionDTOType discussionDTO)
    {
        this.discussionDTO = discussionDTO;
        this.translatedDiscussionDTO = null;
        this.currentTranslationStatus = TranslationStatus.ORIGINAL;

        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.linkWith(discussionDTO);
        }

        display();

        if (isAutoTranslate() && socialShareHelper.canTranslate(discussionDTO))
        {
            handleTranslationRequested();
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

    public boolean isAutoTranslate()
    {
        return socialShareHelper.isAutoTranslate();
    }

    public void linkWithTranslated(DiscussionDTOType translatedDiscussionDTO)
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
        displayTime();
        displayTranslatableTexts();
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

    @NonNull public Spanned getTranslateNoticeText()
    {
        return Html.fromHtml(context.getString(
                R.string.discussion_translate_button_with_powered,
                getTranslateNoticeActionText()));
    }

    @NonNull public String getTranslateNoticeActionText()
    {
        return context.getString(currentTranslationStatus.actionTextResId);
    }
    //</editor-fold>

    protected void notifyCommentButtonClicked()
    {
        OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onCommentButtonClicked();
        }
    }

    protected void notifyShareRequested()
    {
        if (discussionDTO != null)
        {
            socialShareHelper.share(discussionDTO);
            OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
            if (menuClickedListenerCopy != null)
            {
                menuClickedListenerCopy.onShareButtonClicked();
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.discussion_translate_notice_wrapper})
    @Optional
    protected void toggleTranslate()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case FAILED:
                handleTranslationRequested();
                break;

            case TRANSLATING:
            case TRANSLATED:
                currentTranslationStatus = TranslationStatus.ORIGINAL;
                displayTranslatableTexts();
                break;
        }
    }

    protected void handleTranslationRequested()
    {
        currentTranslationStatus = TranslationStatus.TRANSLATING;
        displayTranslateNotice();
        socialShareHelper.translate(discussionDTO);
        notifyTranslationRequested();
    }

    protected void notifyTranslationRequested()
    {
        OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy != null)
        {
            menuClickedListenerCopy.onTranslationRequested();
        }
    }

    protected void notifyMoreButtonClicked()
    {
        OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
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

    protected SocialShareTranslationHelper.OnMenuClickedListener createSocialShareMenuClickedListener()
    {
        return new AbstractDiscussionCompactItemViewHolderSocialShareHelperMenuClickedListener()
        {
            @Override public void onTranslationClicked(AbstractDiscussionCompactDTO toTranslate)
            {
                // Nothing to do
            }

            @Override public void onTranslateFailed(AbstractDiscussionCompactDTO toTranslate,
                    Throwable error)
            {
                currentTranslationStatus = TranslationStatus.FAILED;
            }

            @Override public void onCancelClicked()
            {
                // Nothing to do
            }

            @Override public void onShareRequestedClicked(@NonNull SocialShareFormDTO socialShareFormDTO)
            {
                // Nothing to do
                THToast.show(R.string.content_sharing_started);
            }

            @Override public void onConnectRequired(@NonNull SocialShareFormDTO shareFormDTO, @NonNull List<SocialNetworkEnum> toConnect)
            {
                // Nothing to do
            }

            @Override public void onShared(@NonNull SocialShareFormDTO shareFormDTO,
                    @NonNull SocialShareResultDTO socialShareResultDTO)
            {
                // Nothing to do
                THToast.show(R.string.content_shared);
            }

            @Override public void onShareFailed(@NonNull SocialShareFormDTO shareFormDTO,
                    @NonNull Throwable throwable)
            {
                // Nothing to do
            }
        };
    }

    abstract protected class AbstractDiscussionCompactItemViewHolderSocialShareHelperMenuClickedListener
            implements SocialShareTranslationHelper.OnMenuClickedListener
    {
        @Override public void onTranslatedOneAttribute(AbstractDiscussionCompactDTO toTranslate,
                TranslationResult translationResult)
        {
            setLatestTranslationResult(translationResult);
        }

        @SuppressWarnings("unchecked")
        @Override public void onTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate,
                AbstractDiscussionCompactDTO translated)
        {
            linkWithTranslated((DiscussionDTOType) translated);
        }
    }

    public static interface OnMenuClickedListener extends DiscussionActionButtonsView.OnButtonClickedListener
    {
        @Deprecated // TODO remove as all implementations are empty
        void onTranslationRequested();
    }
}
