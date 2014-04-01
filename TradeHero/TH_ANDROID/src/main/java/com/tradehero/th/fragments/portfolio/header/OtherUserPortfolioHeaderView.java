package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.localytics.android.LocalyticsSession;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import java.lang.ref.WeakReference;
import javax.inject.Inject;

/**
 * Created by julien on 21/10/13
 */
public class OtherUserPortfolioHeaderView extends RelativeLayout implements PortfolioHeaderView
{
    public static final String TAG = OtherUserPortfolioHeaderView.class.getSimpleName();

    @InjectView(R.id.portfolio_person_container) View userViewContainer;
    @InjectView(R.id.portfolio_header_avatar) ImageView userImageView;
    @InjectView(R.id.header_portfolio_username) TextView usernameTextView;
    @InjectView(R.id.header_portfolio_following_image) ImageView followingImageView;
    @InjectView(R.id.follow_button) TextView followButton;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userCache;
    @Inject Picasso picasso;
    @Inject LocalyticsSession localyticsSession;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private UserProfileDTO userProfileDTO;
    private WeakReference<OnFollowRequestedListener> followRequestedListenerWeak = new WeakReference<>(null);
    private WeakReference<OnTimelineRequestedListener> timelineRequestedListenerWeak = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public OtherUserPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (followButton != null)
        {
            followButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    localyticsSession.tagEvent(LocalyticsConstants.Positions_Follow);
                    if (userProfileDTO != null)
                    {
                        notifyFollowRequested(userProfileDTO.getBaseKey());
                    }
                }
            });
        }

        if (userViewContainer != null)
        {
            userViewContainer.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    if (userProfileDTO != null)
                    {
                        notifyTimelineRequested(userProfileDTO.getBaseKey());
                    }
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (followButton != null)
        {
            followButton.setOnClickListener(null);
        }
        if (userViewContainer != null)
        {
            userViewContainer.setOnClickListener(null);
        }

        super.onDetachedFromWindow();
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO)
    {
        display(userProfileDTO);
    }

    private void display(UserProfileDTO user)
    {
        this.userProfileDTO = user;
        configureUserViews();
        configureFollowItemsVisibility();
    }

    @Override public void linkWith(PortfolioDTO portfolioDTO)
    {
        // Nothing to do
    }

    private void configureUserViews()
    {
        if (userProfileDTO != null)
        {
            if (usernameTextView != null)
            {
                usernameTextView.setText(userProfileDTO.displayName);
            }

            if (this.userImageView != null)
            {
                displayDefaultUserImage();
                picasso.load(this.userProfileDTO.picture)
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

    private void configureFollowItemsVisibility()
    {
        UserProfileDTO currentUser = this.userCache.get(currentUserId.toUserBaseKey());
        if (this.userProfileDTO == null)
        {
            this.followingImageView.setVisibility(GONE);
            this.followButton.setVisibility(GONE);
        }
        else if (currentUser.isFollowingUser(this.userProfileDTO.id))
        {
            this.followingImageView.setVisibility(VISIBLE);
            this.followButton.setVisibility(GONE);
        }
        else
        {
            this.followingImageView.setVisibility(GONE);
            this.followButton.setVisibility(VISIBLE);
        }
    }

    /**
     * The listener should be strongly referenced elsewhere.
     * @param followRequestedListener
     */
    @Override public void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListenerWeak = new WeakReference<>(followRequestedListener);
    }

    protected void notifyFollowRequested(UserBaseKey userBaseKey)
    {
        OnFollowRequestedListener followRequestedListener = followRequestedListenerWeak.get();
        if (followRequestedListener != null)
        {
            followRequestedListener.onFollowRequested(userBaseKey);
        }
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param timelineRequestedListener
     */
    @Override public void setTimelineRequestedListener(OnTimelineRequestedListener timelineRequestedListener)
    {
        this.timelineRequestedListenerWeak = new WeakReference<>(timelineRequestedListener);
    }

    protected void notifyTimelineRequested(UserBaseKey userBaseKey)
    {
        OnTimelineRequestedListener timelineRequestedListener = timelineRequestedListenerWeak.get();
        if (timelineRequestedListener != null)
        {
            timelineRequestedListener.onTimelineRequested(userBaseKey);
        }
    }
}
