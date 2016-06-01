package com.ayondo.academy.fragments.discussion;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import android.support.annotation.Nullable;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.translation.TranslationToken;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import com.ayondo.academy.models.share.SocialShareTranslationHelper;
import com.ayondo.academy.network.share.dto.SocialDialogResult;
import com.ayondo.academy.network.share.dto.TranslateResult;
import com.ayondo.academy.rx.ReplaceWithFunc1;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class AbstractDiscussionCompactItemViewHolder
        implements DTOView<AbstractDiscussionCompactItemViewHolder.DTO>
{
    public enum TranslationStatus
    {
        ORIGINAL(R.string.discussion_translate_button),
        TRANSLATING(R.string.discussion_translating_button),
        TRANSLATED(R.string.discussion_show_original_button),
        FAILED(R.string.discussion_translation_failed_button),;

        @StringRes public final int actionTextResId;

        //<editor-fold desc="Constructors">
        TranslationStatus(@StringRes int actionTextResId)
        {
            this.actionTextResId = actionTextResId;
        }
        //</editor-fold>
    }

    @Bind(R.id.discussion_action_buttons) @Nullable public DiscussionActionButtonsView discussionActionButtonsView;
    @Bind(R.id.discussion_time) @Nullable protected TextView time;

    @Bind(R.id.private_text_stub_container) @Nullable protected View stubTextContainer;

    @Bind(R.id.discussion_translate_notice_wrapper) @Nullable protected View translateNoticeWrapper;
    @Bind(R.id.discussion_translate_notice) @Nullable protected TextView translateNotice;
    @Bind(R.id.discussion_translate_notice_image) @Nullable protected ImageView translateNoticeImage;

    @Inject protected SocialShareTranslationHelper socialShareHelper;

    protected boolean downVote;
    @Nullable protected DTO viewDTO;
    @NonNull protected final PublishSubject<UserDiscussionAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewHolder()
    {
        this.userActionSubject = PublishSubject.create();
    }

    //</editor-fold>

    public void onFinishInflate(@NonNull View view)
    {
        HierarchyInjector.inject(view.getContext(), this);
        ButterKnife.bind(this, view);
    }

    public void onAttachedToWindow(@NonNull View view)
    {
        ButterKnife.bind(this, view);
    }

    @SuppressLint("MissingSuperCall")
    public void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
    }

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return getMergedUserActionObservable()
                .flatMap(new Func1<UserDiscussionAction,
                        Observable<UserDiscussionAction>>()
                {
                    @Override public Observable<UserDiscussionAction> call(
                            UserDiscussionAction userAction)
                    {
                        return handleCertainUserAction(userAction);
                    }
                });
    }

    @NonNull protected Observable<UserDiscussionAction> getMergedUserActionObservable()
    {
        Observable<UserDiscussionAction> actionButtonsObservable;
        if (discussionActionButtonsView != null)
        {
            actionButtonsObservable = discussionActionButtonsView.getUserActionObservable();
        }
        else
        {
            actionButtonsObservable = Observable.never();
        }
        return userActionSubject.mergeWith(actionButtonsObservable);
    }

    @NonNull public Observable<UserDiscussionAction> handleCertainUserAction(
            @NonNull final UserDiscussionAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.ShareUserAction)
        {
            return socialShareHelper.show(userAction.discussionDTO, true)
                    .map(new ReplaceWithFunc1<>(userAction));
        }
        else if (userAction instanceof TranslateUserAction)
        {
            final DTO dto = ((TranslateUserAction) userAction).viewDTO;
            if (dto.getCurrentTranslationStatus() == TranslationStatus.ORIGINAL ||
                    dto.getCurrentTranslationStatus() == TranslationStatus.FAILED)
            {
                dto.setCurrentTranslationStatus(TranslationStatus.TRANSLATING);
                if (viewDTO != null)
                {
                    display(viewDTO);
                }
            }
            return socialShareHelper.translate(userAction.discussionDTO)
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<SocialDialogResult, Observable<SocialDialogResult>>()
                    {
                        @Override public Observable<SocialDialogResult> call(SocialDialogResult result)
                        {
                            //noinspection unchecked
                            dto.translatedDiscussionDTO = ((TranslateResult) result).translated;
                            if (dto.getCurrentTranslationStatus() == TranslationStatus.TRANSLATING)
                            {
                                dto.setCurrentTranslationStatus(TranslationStatus.TRANSLATED);
                            }
                            if (viewDTO != null)
                            {
                                display(viewDTO);
                            }
                            return Observable.empty();
                        }
                    })
                    .doOnError(new Action1<Throwable>()
                    {
                        @Override public void call(Throwable throwable)
                        {
                            if (dto.getCurrentTranslationStatus() == TranslationStatus.TRANSLATING)
                            {
                                dto.setCurrentTranslationStatus(TranslationStatus.ORIGINAL);
                            }
                        }
                    })
                    .map(new ReplaceWithFunc1<>(userAction));
        }
        return Observable.just(userAction);
    }

    @Override public void display(@NonNull DTO dto)
    {
        this.viewDTO = dto;

        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.linkWith(dto.discussionDTO);
        }
        if (translateNoticeWrapper != null)
        {
            translateNoticeWrapper.setVisibility(dto.translateNoticeVisibility);
        }
        if (stubTextContainer != null)
        {
            stubTextContainer.setVisibility(dto.stubTextContainerVisibility);
        }
        if (time != null)
        {
            time.setText(dto.timeToDisplay);
        }
        if (translateNotice != null)
        {
            translateNotice.setText(dto.getTranslateNoticeText());
        }
        if (translateNoticeImage != null && dto.translationToken != null)
        {
            translateNoticeImage.setImageResource(dto.translationToken.logoResId());
        }
        if (dto.isAutoTranslate)
        {
            switch (dto.getCurrentTranslationStatus())
            {
                case ORIGINAL:
                case FAILED:
                    handleTranslationRequested();
                    break;
            }
        }
    }

    public void setBackgroundResource(int resId)
    {
        //Do nothing
    }

    public void setDownVote(boolean downVote)
    {
        this.downVote = downVote;
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setDownVote(downVote);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.discussion_translate_notice_wrapper})
    @Nullable
    protected void toggleTranslate()
    {
        if (viewDTO != null)
        {
            switch (viewDTO.getCurrentTranslationStatus())
            {
                case ORIGINAL:
                case FAILED:
                    handleTranslationRequested();
                    break;

                case TRANSLATING:
                case TRANSLATED:
                    viewDTO.setCurrentTranslationStatus(TranslationStatus.ORIGINAL);
                    display(viewDTO);
                    break;
            }
        }
    }

    protected void handleTranslationRequested()
    {
        if (viewDTO != null)
        {
            viewDTO.setCurrentTranslationStatus(TranslationStatus.TRANSLATING);
            userActionSubject.onNext(new TranslateUserAction(viewDTO));
        }
    }

    public static class Requisite
    {
        @NonNull public final Resources resources;
        @NonNull public final PrettyTime prettyTime;
        @NonNull public final AbstractDiscussionCompactDTO discussionDTO;
        public final boolean canTranslate;
        public final boolean isAutoTranslate;

        public Requisite(
                @NonNull Resources resources,
                @NonNull PrettyTime prettyTime,
                @NonNull AbstractDiscussionCompactDTO discussionDTO,
                boolean canTranslate,
                boolean isAutoTranslate)
        {
            this.resources = resources;
            this.prettyTime = prettyTime;
            this.discussionDTO = discussionDTO;
            this.canTranslate = canTranslate;
            this.isAutoTranslate = isAutoTranslate;
        }
    }

    public static class DTO
    {
        @NonNull private Resources resources;
        @NonNull public final AbstractDiscussionCompactDTO discussionDTO;
        public final boolean isAutoTranslate;
        @ViewVisibilityValue public final int translateNoticeVisibility;
        @Nullable public AbstractDiscussionCompactDTO translatedDiscussionDTO;
        @NonNull private TranslationStatus currentTranslationStatus;
        @Nullable public TranslationToken translationToken;

        @ViewVisibilityValue public final int stubTextContainerVisibility;
        @NonNull private Spanned translateNoticeText;
        @NonNull public final String timeToDisplay;

        public DTO(@NonNull Requisite requisite)
        {
            this.resources = requisite.resources;
            this.discussionDTO = requisite.discussionDTO;
            this.isAutoTranslate = requisite.isAutoTranslate && requisite.canTranslate;
            this.translateNoticeVisibility = requisite.canTranslate ? View.VISIBLE : View.GONE;
            this.currentTranslationStatus = TranslationStatus.ORIGINAL;
            this.translatedDiscussionDTO = null;
            this.translationToken = null;

            this.stubTextContainerVisibility = discussionDTO.isInProcess() ? View.VISIBLE : View.GONE;

            translateNoticeText = createTranslationNoticeText();

            timeToDisplay = createTimeToDisplay(requisite.prettyTime);
        }

        @NonNull public TranslationStatus getCurrentTranslationStatus()
        {
            return currentTranslationStatus;
        }

        public void setCurrentTranslationStatus(
                @NonNull TranslationStatus currentTranslationStatus)
        {
            this.currentTranslationStatus = currentTranslationStatus;
            this.translateNoticeText = createTranslationNoticeText();
        }

        @NonNull private Spanned createTranslationNoticeText()
        {
            return Html.fromHtml(resources.getString(
                    R.string.discussion_translate_button_with_powered,
                    resources.getString(currentTranslationStatus.actionTextResId)));
        }

        @NonNull public Spanned getTranslateNoticeText()
        {
            return translateNoticeText;
        }

        @NonNull protected String createTimeToDisplay(@NonNull PrettyTime prettyTime)
        {
            if (discussionDTO.createdAtUtc != null)
            {
                return prettyTime.formatUnrounded(discussionDTO.createdAtUtc);
            }
            return "";
        }
    }

    public static class TranslateUserAction extends UserDiscussionAction
    {
        @NonNull public final AbstractDiscussionCompactItemViewHolder.DTO viewDTO;

        public TranslateUserAction(@NonNull AbstractDiscussionCompactItemViewHolder.DTO viewDTO)
        {
            super(viewDTO.discussionDTO);
            this.viewDTO = viewDTO;
        }
    }
}
