package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import java.lang.ref.WeakReference;
import javax.inject.Inject;

/**
 * Created by julien on 21/10/13
 */
public class OtherUserPortfolioHeaderView extends RelativeLayout implements PortfolioHeaderView
{
    public static final String TAG = OtherUserPortfolioHeaderView.class.getSimpleName();

    private View userViewContainer;
    private ImageView userImageView;
    private TextView usernameTextView;
    private ImageView followingImageView;
    private ImageButton followButton;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject Lazy<UserProfileCache> userCache;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    private UserProfileDTO userProfileDTO;
    private WeakReference<OnFollowRequestedListener> followRequestedListenerWeak = new WeakReference<>(null);
    private WeakReference<OnTimelineRequestedListener> timelineRequestedListenerWeak = new WeakReference<>(null);

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> getUserCacheListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> fetchUserProfileTask;

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
        initViews();
    }

    private void initViews()
    {
        userViewContainer = findViewById(R.id.portfolio_person_container);
        userImageView = (ImageView) findViewById(R.id.portfolio_header_avatar);
        usernameTextView = (TextView) findViewById(R.id.header_portfolio_username);
        followingImageView = (ImageView) findViewById(R.id.header_portfolio_following_image);
        followButton = (ImageButton) findViewById(R.id.header_portfolio_follow_button);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        getUserCacheListener = new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
            {
                display(value);
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THLog.e(TAG, "There was an error fetching the profile of " + key, error);
                THToast.show(R.string.error_fetch_user_profile);
            }
        };

        if (followButton != null)
        {
            followButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
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
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.setListener(null);
        }
        fetchUserProfileTask = null;
        getUserCacheListener = null;

        super.onDetachedFromWindow();
    }

    private void display(UserProfileDTO user)
    {
        this.userProfileDTO = user;
        configureUserViews();
        configureFollowItemsVisibility();
    }

    @Override public void bindOwnedPortfolioId(OwnedPortfolioId id)
    {
        fetchUserProfileTask = this.userCache.get().getOrFetch(id.getUserBaseKey(), false, getUserCacheListener);
        fetchUserProfileTask.execute();
    }

    private void configureUserViews()
    {
        if (this.usernameTextView != null)
        {
            this.usernameTextView.setText(this.userProfileDTO.displayName);
        }

        if (this.userImageView != null)
        {
            picasso.get().load(this.userProfileDTO.picture)
                    .transform(peopleIconTransformation)
                    .into(this.userImageView, new Callback()
                    {
                        @Override public void onSuccess()
                        {

                        }

                        @Override public void onError()
                        {
                            displayDefaultUserImage();
                        }
                    });
        }
    }

    private void displayDefaultUserImage()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(this.userImageView);
    }

    private void configureFollowItemsVisibility()
    {
        UserProfileDTO currentUser = this.userCache.get().get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
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
