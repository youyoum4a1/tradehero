package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class OtherUserPortfolioHeaderView extends RelativeLayout implements PortfolioHeaderView
{
    @Bind(R.id.portfolio_header_avatar) ImageView userImageView;
    @Bind(R.id.header_portfolio_username) TextView usernameTextView;
    @Bind(R.id.follow_button) Button followButton;
    @Bind(R.id.last_updated_date) @Nullable protected TextView lastUpdatedDate;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userCache;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private UserProfileDTO shownUserProfileDTO;
    @NonNull protected BehaviorSubject<UserAction> userActionBehaviour;
    private boolean isFollowing;

    //<editor-fold desc="Constructors">
    public OtherUserPortfolioHeaderView(Context context)
    {
        super(context);
        this.userActionBehaviour = BehaviorSubject.create();
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.userActionBehaviour = BehaviorSubject.create();
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.userActionBehaviour = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        this.userActionBehaviour.onCompleted();
        this.userActionBehaviour = BehaviorSubject.create();
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.portfolio_header_avatar, R.id.header_portfolio_username})
    protected void userClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        if (shownUserProfileDTO != null)
        {
            userActionBehaviour.onNext(new TimelineUserAction(shownUserProfileDTO));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.follow_button)
    protected void followClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        if (shownUserProfileDTO != null)
        {
            if (isFollowing)
            {
                userActionBehaviour.onNext(new UnFollowUserAction(shownUserProfileDTO));
            }
            else
            {
                userActionBehaviour.onNext(new FollowUserAction(shownUserProfileDTO));
            }
        }
    }

    @NonNull @Override public Observable<UserAction> getUserActionObservable()
    {
        return userActionBehaviour.asObservable();
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO)
    {
        display(userProfileDTO);
    }

    private void display(UserProfileDTO user)
    {
        this.shownUserProfileDTO = user;
        configureUserViews();
        configureFollowItemsVisibility();
    }

    @Override public void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        if (lastUpdatedDate != null)
        {
            if (portfolioCompactDTO != null && portfolioCompactDTO.markingAsOfUtc != null)
            {
                DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
                lastUpdatedDate.setText(getContext().getString(
                        R.string.watchlist_marking_date,
                        sdf.format(portfolioCompactDTO.markingAsOfUtc)));
                lastUpdatedDate.setVisibility(VISIBLE);
            }
            else
            {
                lastUpdatedDate.setVisibility(GONE);
            }
        }
    }

    private void configureUserViews()
    {
        if (shownUserProfileDTO != null)
        {
            if (usernameTextView != null)
            {
                usernameTextView.setText(shownUserProfileDTO.displayName);
            }

            if (this.userImageView != null)
            {
                displayDefaultUserImage();
                picasso.load(this.shownUserProfileDTO.picture)
                        .transform(peopleIconTransformation)
                        .placeholder(this.userImageView.getDrawable())
                        .into(this.userImageView);
            }
        }
    }

    private void displayDefaultUserImage()
    {
        picasso.load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(this.userImageView);
    }

    public void configureFollowItemsVisibility()
    {
        UserProfileDTO currentUser = this.userCache.getCachedValue(currentUserId.toUserBaseKey());
        isFollowing = (currentUser != null && currentUser.isFollowingUser(this.shownUserProfileDTO.id));
        if (this.shownUserProfileDTO == null || isCurrentUser(this.shownUserProfileDTO.id))
        {
            this.followButton.setVisibility(GONE);
        }
        else
        {
            this.followButton.setVisibility(VISIBLE);
            SimpleFollowUserAssistant.updateFollowButton(this.followButton, isFollowing);
        }
    }

    public boolean isCurrentUser(int userId)
    {
        return currentUserId.get().equals(userId);
    }
}
