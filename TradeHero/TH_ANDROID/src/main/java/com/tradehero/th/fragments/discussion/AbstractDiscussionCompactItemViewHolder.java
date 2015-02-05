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
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.network.share.dto.SocialDialogResult;
import com.tradehero.th.network.share.dto.TranslateResult;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;

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
    @NonNull protected final Context context;

    protected boolean downVote;
    @Nullable protected DiscussionDTOType discussionDTO;
    @Nullable protected DiscussionDTOType translatedDiscussionDTO;
    protected @NonNull TranslationStatus currentTranslationStatus = TranslationStatus.ORIGINAL;
    protected TranslationResult latestTranslationResult;
    @NonNull protected SubscriptionList subscriptions;
    @NonNull protected BehaviorSubject<DiscussionActionButtonsView.UserAction> userActionBehavior;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewHolder(@NonNull Context context)
    {
        this.context = context;
        this.subscriptions = new SubscriptionList();
        this.userActionBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    public void onFinishInflate(@NonNull View view)
    {
        HierarchyInjector.inject(view.getContext(), this);
        ButterKnife.inject(this, view);
    }

    public void onAttachedToWindow(@NonNull View view)
    {
        ButterKnife.inject(this, view);
        if (discussionActionButtonsView != null)
        {
            subscriptions.add(discussionActionButtonsView.getUserActionObservable()
                    .flatMap(new Func1<DiscussionActionButtonsView.UserAction,
                            Observable<DiscussionActionButtonsView.UserAction>>()
                    {
                        @Override public Observable<DiscussionActionButtonsView.UserAction> call(
                                DiscussionActionButtonsView.UserAction userAction)
                        {
                            return handleDiscussionButtonUserAction(userAction);
                        }
                    })
                    .subscribe(userActionBehavior));
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onDetachedFromWindow()
    {
        userActionBehavior.onCompleted();
        this.userActionBehavior = BehaviorSubject.create();
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
        ButterKnife.reset(this);
    }

    @NonNull public Observable<DiscussionActionButtonsView.UserAction> getUserActionObservable()
    {
        return userActionBehavior.asObservable();
    }

    @NonNull public Observable<DiscussionActionButtonsView.UserAction> handleDiscussionButtonUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.ShareUserAction
                && discussionDTO != null)
        {
            return socialShareHelper.show(discussionDTO, true)
                    .flatMap(new Func1<SocialDialogResult, Observable<DiscussionActionButtonsView.UserAction>>()
                    {
                        @Override public Observable<DiscussionActionButtonsView.UserAction> call(SocialDialogResult result)
                        {
                            return handleSocialResult(result)
                                    .map(obj -> userAction);
                        }
                    });
        }
        return Observable.just(userAction);
    }

    @NonNull public Observable<SocialDialogResult> handleSocialResult(SocialDialogResult result)
    {
        if (result instanceof TranslateResult)
        {
            //noinspection unchecked
            linkWithTranslated((DiscussionDTOType) ((TranslateResult) result).translated);
            return Observable.empty();
        }
        return Observable.just(result);
    }

    public void setBackgroundResource(int resId)
    {
        //Do nothing
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

        subscriptions.add(socialShareHelper.isAutoTranslate()
                .filter(new Func1<Boolean, Boolean>()
                {
                    @Override public Boolean call(Boolean autoTranslate)
                    {
                        return autoTranslate;
                    }
                })
                .flatMap(new Func1<Boolean, Observable<? extends Boolean>>()
                {
                    @Override public Observable<? extends Boolean> call(Boolean autoTranslate)
                    {
                        return socialShareHelper.canTranslate(discussionDTO);
                    }
                })
                .filter(new Func1<Boolean, Boolean>()
                {
                    @Override public Boolean call(Boolean canTranslate)
                    {
                        return canTranslate;
                    }
                })
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean aBoolean)
                            {
                                handleTranslationRequested();
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {

                            }
                        }));
    }

    protected void handleTranslationRequested()
    {
        currentTranslationStatus = TranslationStatus.TRANSLATING;
        displayTranslateNotice();
        if (discussionDTO != null)
        {
            subscriptions.add(socialShareHelper.translate(discussionDTO)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::handleSocialResult,
                            Actions.empty()
                    ));
        }
        userActionBehavior.onNext(new TranslateUserAction());
    }

    public void setDownVote(boolean downVote)
    {
        this.downVote = downVote;
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setDownVote(downVote);
        }
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
        if (translateNoticeWrapper != null && discussionDTO != null)
        {
            subscriptions.add(socialShareHelper.canTranslate(discussionDTO)
                    .subscribe(
                            new Action1<Boolean>()
                            {
                                @Override public void call(Boolean canTranslate)
                                {
                                    translateNoticeWrapper.setVisibility(canTranslate ? View.VISIBLE : View.GONE);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable throwable)
                                {
                                    // Nothing to do
                                }
                            }));
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

    public static class TranslateUserAction implements DiscussionActionButtonsView.UserAction
    {
    }
}
