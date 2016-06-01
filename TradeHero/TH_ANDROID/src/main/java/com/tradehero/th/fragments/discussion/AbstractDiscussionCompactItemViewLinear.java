package com.ayondo.academy.fragments.discussion;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import com.ayondo.academy.models.share.SocialShareTranslationHelper;
import com.ayondo.academy.network.share.dto.SocialDialogResult;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.functions.Func1;

abstract public class AbstractDiscussionCompactItemViewLinear
        extends LinearLayout
        implements DTOView<AbstractDiscussionCompactItemViewLinear.DTO>
{
    @Inject protected SocialShareTranslationHelper socialShareHelper;

    @NonNull protected final AbstractDiscussionCompactItemViewHolder viewHolder;
    protected DTO viewDTO;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        viewHolder = createViewHolder();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            viewHolder.onFinishInflate(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            viewHolder.onAttachedToWindow(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        viewHolder.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    @NonNull protected AbstractDiscussionCompactItemViewHolder createViewHolder()
    {
        return new AbstractDiscussionCompactItemViewHolder();
    }

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return viewHolder.getUserActionObservable()
                .flatMap(new Func1<UserDiscussionAction, Observable<UserDiscussionAction>>()
                {
                    @Override public Observable<UserDiscussionAction> call(
                            UserDiscussionAction userAction)
                    {
                        return handleUserAction(userAction);
                    }
                });
    }

    @NonNull protected Observable<UserDiscussionAction> handleUserAction(
            UserDiscussionAction userAction)
    {
        if (viewDTO != null)
        {
            if (userAction instanceof DiscussionActionButtonsView.MoreUserAction)
            {
                return socialShareHelper.show(viewDTO.viewHolderDTO.discussionDTO, false)
                        .flatMap(new Func1<SocialDialogResult, Observable<? extends UserDiscussionAction>>()
                        {
                            @Override public Observable<? extends UserDiscussionAction> call(SocialDialogResult result)
                            {
                                return Observable.empty();
                            }
                        });
            }
        }
        return Observable.just(userAction);
    }

    @Override public void display(@NonNull DTO dto)
    {
        this.viewDTO = dto;
        viewHolder.display(dto.viewHolderDTO);
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
        @NonNull public final AbstractDiscussionCompactItemViewHolder.DTO viewHolderDTO;

        public DTO(@NonNull Requisite requisite)
        {
            this.viewHolderDTO = createViewHolderDTO(requisite);
        }

        @NonNull protected AbstractDiscussionCompactItemViewHolder.DTO createViewHolderDTO(
                @NonNull Requisite requisite)
        {
            return new AbstractDiscussionCompactItemViewHolder.DTO(
                    new AbstractDiscussionCompactItemViewHolder.Requisite(
                            requisite.resources,
                            requisite.prettyTime,
                            requisite.discussionDTO,
                            requisite.canTranslate,
                            requisite.isAutoTranslate));
        }
    }
}
