package com.tradehero.th.fragments.discussion;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.text.ClickableTagProcessor;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.models.discussion.PlayerUserAction;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.models.discussion.UserDiscussionActionFactory;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.widget.MarkdownTextView;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.functions.Func1;

public class AbstractDiscussionItemViewHolder
        extends AbstractDiscussionCompactItemViewHolder
{
    @NonNull protected final Picasso picasso;
    @Bind(R.id.text_container) @Nullable protected View textContainer;
    @Bind(R.id.discussion_content) protected MarkdownTextView textContent;
    @Bind(R.id.discussion_stub_content) @Nullable protected MarkdownTextView stubContent;
    @Bind(R.id.discussion_user_picture) @Nullable ImageView discussionUserPicture;
    @Bind(R.id.user_profile_name) @Nullable TextView userProfileName;
    @Inject @ForUserPhoto Transformation discussionUserPictureTransformation;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionItemViewHolder(@NonNull Picasso picasso)
    {
        super();
        this.picasso = picasso;
    }
    //</editor-fold>

    @Override public void display(@NonNull AbstractDiscussionCompactItemViewHolder.DTO parentViewDto)
    {
        super.display(parentViewDto);
        DTO dto = (DTO) parentViewDto;
        if (textContainer != null)
        {
            textContainer.setVisibility(dto.textContainerVisibility);
        }
        if (textContent != null)
        {
            textContent.setText(dto.getTextContent());
        }
        if (stubContent != null)
        {
            stubContent.setText(dto.stubText);
        }
        if (discussionUserPicture != null)
        {
            picasso.load(getUserAvatarURL())
                    .placeholder(R.drawable.superman_facebook)
                    .error(R.drawable.superman_facebook)
                    .into(discussionUserPicture);
        }
        if (userProfileName != null)
        {
            userProfileName.setText(dto.userProfileName);
        }
    }

    @NonNull @Override protected Observable<UserDiscussionAction> getMergedUserActionObservable()
    {
        Observable<ClickableTagProcessor.UserAction> markdownActionSubject = textContent.getUserActionObservable();
        if (stubContent != null)
        {
            markdownActionSubject = markdownActionSubject.mergeWith(stubContent.getUserActionObservable());
        }
        return super.getMergedUserActionObservable().mergeWith(markdownActionSubject
                .flatMap(
                        new Func1<ClickableTagProcessor.UserAction, Observable<UserDiscussionAction>>()
                        {
                            @Override public Observable<UserDiscussionAction> call(ClickableTagProcessor.UserAction userAction)
                            {
                                if (viewDTO != null)
                                {
                                    return UserDiscussionActionFactory.createObservable(viewDTO.discussionDTO, userAction);
                                }
                                return Observable.empty();
                            }
                        }));
    }

    @NonNull protected String getUserAvatarURL()
    {
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Nullable @OnClick({R.id.discussion_user_picture, R.id.user_profile_name})
    protected void handleUserClicked(View view)
    {
        if (viewDTO != null)
        {
            userActionSubject.onNext(new PlayerUserAction(viewDTO.discussionDTO, ((AbstractDiscussionDTO) viewDTO.discussionDTO).getSenderKey()));
        }
    }

    public static class Requisite
            extends AbstractDiscussionCompactItemViewHolder.Requisite
    {
        public Requisite(
                @NonNull Resources resources,
                @NonNull PrettyTime prettyTime,
                @NonNull AbstractDiscussionDTO discussionDTO,
                boolean canTranslate,
                boolean isAutoTranslate)
        {
            super(resources, prettyTime, discussionDTO, canTranslate, isAutoTranslate);
        }
    }

    public static class DTO extends AbstractDiscussionCompactItemViewHolder.DTO
    {
        @Nullable public final String stubText;
        @ViewVisibilityValue public final int textContainerVisibility;
        @Nullable private String textContent;
        @NonNull public final String userProfileName;

        public DTO(@NonNull Requisite requisite)
        {
            super(requisite);

            this.stubText = ((AbstractDiscussionDTO) requisite.discussionDTO).text;
            this.textContainerVisibility = discussionDTO.isInProcess() ? View.GONE : View.VISIBLE;
            this.textContent = createTextContent();
            userProfileName = createUserDisplayName();
        }

        @NonNull protected String createUserDisplayName()
        {
            return String.format("%d", ((AbstractDiscussionDTO) discussionDTO).userId);
        }

        @Override public void setCurrentTranslationStatus(@NonNull TranslationStatus currentTranslationStatus)
        {
            super.setCurrentTranslationStatus(currentTranslationStatus);
            this.textContent = createTextContent();
        }

        @Nullable protected String createTextContent()
        {
            switch (getCurrentTranslationStatus())
            {
                case ORIGINAL:
                case TRANSLATING:
                case FAILED:
                    return ((AbstractDiscussionDTO) discussionDTO).text;

                case TRANSLATED:
                    if (translatedDiscussionDTO != null)
                    {
                        return ((AbstractDiscussionDTO) translatedDiscussionDTO).text;
                    }
                    return null;
            }
            throw new IllegalStateException("Unhandled state TranslationStatus." + getCurrentTranslationStatus());
        }

        @Nullable public String getTextContent()
        {
            return textContent;
        }
    }
}
