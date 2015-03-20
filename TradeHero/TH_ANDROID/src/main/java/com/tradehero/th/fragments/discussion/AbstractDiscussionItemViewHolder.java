package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.graphics.ForUserPhoto;
import javax.inject.Inject;

public class AbstractDiscussionItemViewHolder<DiscussionDTOType extends AbstractDiscussionDTO>
    extends AbstractDiscussionCompactItemViewHolder<DiscussionDTOType>
{
    @InjectView(R.id.private_text_container) @Optional protected View textContainer;
    @InjectView(R.id.discussion_content) protected TextView textContent;
    @InjectView(R.id.discussion_user_picture) @Optional ImageView discussionUserPicture;
    @InjectView(R.id.user_profile_name) @Optional TextView userProfileName;

    @Inject Picasso picasso;

    //<editor-fold desc="Constructors">

    public AbstractDiscussionItemViewHolder(@NonNull Context context)
    {
        super(context);
    }

    //</editor-fold>

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayStubText();
    }

    @Override public void displayTranslatableTexts()
    {
        super.displayTranslatableTexts();
        displayText();
    }

    protected void displayText()
    {
        if (textContent != null)
        {
            textContent.setText(getText());
        }
    }

    protected String getText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
            case FAILED:
                if (discussionDTO != null)
                {
                    return discussionDTO.text;
                }
                return null;

            case TRANSLATED:
                if (translatedDiscussionDTO != null)
                {
                    return translatedDiscussionDTO.text;
                }
                return null;

        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }

    protected void displayStubText()
    {
        if (stubContent != null)
        {
            stubContent.setText(getStubText());
        }
    }

    protected String getStubText()
    {
        if (discussionDTO != null)
        {
            return discussionDTO.text;
        }
        return null;
    }

    @Override protected void displayInProcess()
    {
        super.displayInProcess();
        if (textContainer != null)
        {
            textContainer.setVisibility(isInProcess() ? View.GONE : View.VISIBLE);
        }
    }

    protected void displayUser()
    {
        displayUsername();
        displayProfilePicture();
    }

    protected void displayUsername()
    {
        if (userProfileName != null)
        {
            userProfileName.setText(getUserDisplayName());
        }
    }

    protected String getUserDisplayName()
    {
        if (discussionDTO == null)
        {
            return null;
        }
        return String.format("%d", discussionDTO.userId);
    }

    protected void displayProfilePicture()
    {
        if (discussionUserPicture != null)
        {
            cancelProfilePictureRequest();
            createUserPicassoRequest()
                    .into(discussionUserPicture);
        }
    }

    @NonNull protected RequestCreator createUserPicassoRequest()
    {
        return picasso.load(R.drawable.superman_facebook);
    }

    protected void cancelProfilePictureRequest()
    {
        if (discussionUserPicture != null)
        {
            picasso.cancelRequest(discussionUserPicture);
        }
    }
    //</editor-fold>

    @SuppressWarnings("UnusedDeclaration")
    @Optional @OnClick({R.id.discussion_user_picture, R.id.user_profile_name})
    protected void handleUserClicked(View view)
    {
        if (discussionDTO != null)
        {
            userActionBehavior.onNext(new PlayerUserAction(discussionDTO.getSenderKey()));
        }
    }

    public static class PlayerUserAction implements DiscussionActionButtonsView.UserAction
    {
        @NonNull public final UserBaseKey userClicked;

        public PlayerUserAction(@NonNull UserBaseKey userClicked)
        {
            this.userClicked = userClicked;
        }
    }
}
