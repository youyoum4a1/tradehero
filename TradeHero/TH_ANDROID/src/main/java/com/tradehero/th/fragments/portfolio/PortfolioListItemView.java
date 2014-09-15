package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th2.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class PortfolioListItemView extends RelativeLayout
        implements DTOView<DisplayablePortfolioDTO>
{
    @InjectView(R.id.follower_profile_picture) @Optional protected ImageView userIcon;
    @InjectView(R.id.portfolio_title) protected TextView title;
    @InjectView(R.id.portfolio_description) protected TextView description;
    @InjectView(R.id.following_image) @Optional protected ImageView followingStamp;
    @InjectView(R.id.roi_value) @Optional protected TextView roiValue;

    private DisplayablePortfolioDTO displayablePortfolioDTO;
    private UserProfileDTO currentUserProfileDTO;
    private GetPositionsDTO getPositionsDTO;
    private WatchlistPositionDTOList watchedSecurityPositions;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userImageTransformation;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject GetPositionsCache getPositionsCache;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject DisplayablePortfolioUtil displayablePortfolioUtil;
    @Inject THRouter thRouter;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> currentUserProfileCacheListener;
    private DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO> getPositionsListener;
    private DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistListener;

    //<editor-fold desc="Constructors">
    public PortfolioListItemView(Context context)
    {
        super(context);
    }

    public PortfolioListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PortfolioListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
        if (userIcon != null && picasso != null)
        {
            displayDefaultUserIcon();
        }
        currentUserProfileCacheListener = new PortfolioListItemViewCurrentUserProfileCacheListener();
        getPositionsListener = new PortfolioListItemViewGetPositionsListener();
        userWatchlistListener = new PortfolioListItemViewWatchedSecurityIdListListener();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (currentUserProfileCacheListener == null)
        {
            currentUserProfileCacheListener = new PortfolioListItemViewCurrentUserProfileCacheListener();
        }
        if (getPositionsListener == null)
        {
            this.getPositionsListener = new PortfolioListItemViewGetPositionsListener();
        }
        if (userWatchlistListener == null)
        {
            this.userWatchlistListener = new PortfolioListItemViewWatchedSecurityIdListListener();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        userProfileCache.unregister(currentUserProfileCacheListener);
        this.currentUserProfileCacheListener = null;

        detachGetPositionsCache();
        this.getPositionsListener = null;

        detachUserWatchlistTask();
        this.userWatchlistListener = null;

        if (this.userIcon != null)
        {
            this.userIcon.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.follower_profile_picture) @Optional
    protected void handleUserIconClicked()
    {
        if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null)
        {
            Bundle bundle = new Bundle();
            DashboardNavigator navigator =
                    ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            UserBaseKey userToSee = new UserBaseKey(displayablePortfolioDTO.userBaseDTO.id);
            thRouter.save(bundle, userToSee);
            if (currentUserId.toUserBaseKey().equals(userToSee))
            {
                navigator.pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
                navigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    protected void detachGetPositionsCache()
    {
        getPositionsCache.unregister(getPositionsListener);
    }

    protected void detachUserWatchlistTask()
    {
        userWatchlistPositionCache.unregister(userWatchlistListener);
    }

    public DisplayablePortfolioDTO getDisplayablePortfolioDTO()
    {
        return displayablePortfolioDTO;
    }

    public void display(DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        linkWith(displayablePortfolioDTO, true);
    }

    public void linkWith(DisplayablePortfolioDTO displayablePortfolioDTO, boolean andDisplay)
    {
        this.displayablePortfolioDTO = displayablePortfolioDTO;

        fetchCurrentUserProfile();

        if (andDisplay)
        {
            displayUserIcon();
            displayTitle();
            displayDescription();
            displayFollowingStamp();
            displayRoiValue();
        }
    }

    protected void fetchCurrentUserProfile()
    {
        userProfileCache.unregister(currentUserProfileCacheListener);
        userProfileCache.register(currentUserId.toUserBaseKey(), currentUserProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void fetchAdditional()
    {
        this.fetchGetPositions();
        this.fetchWatchedSecurities();
    }

    protected void fetchGetPositions()
    {
        detachGetPositionsCache();
        DisplayablePortfolioDTO displayablePortfolioDTOCopy = this.displayablePortfolioDTO;
        if (displayablePortfolioDTOCopy != null &&
                displayablePortfolioDTOCopy.ownedPortfolioId != null &&
                displayablePortfolioDTOCopy.portfolioDTO != null &&
                !displayablePortfolioDTOCopy.portfolioDTO.isWatchlist &&
                displayablePortfolioDTOCopy.userBaseDTO != null &&
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()
                        .equals(currentUserId.toUserBaseKey()))
        {
            getPositionsCache.register(displayablePortfolioDTOCopy.ownedPortfolioId, getPositionsListener);
            getPositionsCache.getOrFetchAsync(displayablePortfolioDTOCopy.ownedPortfolioId);
        }
    }

    protected void fetchWatchedSecurities()
    {
        detachUserWatchlistTask();
        DisplayablePortfolioDTO displayablePortfolioDTOCopy = this.displayablePortfolioDTO;
        if (displayablePortfolioDTOCopy != null &&
                displayablePortfolioDTOCopy.ownedPortfolioId != null &&
                displayablePortfolioDTOCopy.portfolioDTO != null &&
                displayablePortfolioDTOCopy.portfolioDTO.isWatchlist &&
                displayablePortfolioDTOCopy.userBaseDTO != null &&
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()
                        .equals(currentUserId.toUserBaseKey()))
        {
            Timber.d("fetchWatchedSecurities launching");
            UserBaseKey key = displayablePortfolioDTOCopy.userBaseDTO.getBaseKey();
            userWatchlistPositionCache.register(key, userWatchlistListener);
            userWatchlistPositionCache.getOrFetchAsync(key);
        }
        else
        {
            Timber.d("fetchWatchedSecurities nothing to launch");
        }
    }

    protected void linkWith(GetPositionsDTO getPositionsDTO, boolean andDisplay)
    {
        this.getPositionsDTO = getPositionsDTO;
        if (andDisplay)
        {
            displayDescription();
        }
    }

    protected void linkWith(WatchlistPositionDTOList watchlistPositionDTOs, boolean andDisplay)
    {
        this.watchedSecurityPositions = watchlistPositionDTOs;
        if (andDisplay)
        {
            displayDescription();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayUserIcon();
        displayTitle();
        displayDescription();
        displayFollowingStamp();
        displayRoiValue();
    }

    public void displayUserIcon()
    {
        if (userIcon != null)
        {
            displayDefaultUserIcon();
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null)
            {
                picasso.load(displayablePortfolioDTO.userBaseDTO.picture)
                        .transform(userImageTransformation)
                        .placeholder(userIcon.getDrawable())
                        .into(userIcon);
            }
        }
    }

    public void displayDefaultUserIcon()
    {
        if (userIcon != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(userImageTransformation)
                    .into(userIcon);
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(displayablePortfolioUtil.getLongTitleType(getContext(),
                    displayablePortfolioDTO));
        }
    }

    public void displayDescription()
    {
        TextView descriptionCopy = this.description;
        if (descriptionCopy != null)
        {
            descriptionCopy.setText(getDescription());
        }
    }

    public String getDescription()
    {
        return displayablePortfolioUtil.getLongSubTitle(getContext(), displayablePortfolioDTO);
    }

    public void displayFollowingStamp()
    {
        if (followingStamp != null)
        {
            if (isThisUserFollowed())
            {
                followingStamp.setVisibility(VISIBLE);
            }
            else
            {
                followingStamp.setVisibility(GONE);
            }
        }
    }

    public void displayRoiValue()
    {
        if (roiValue != null)
        {
            if (displayablePortfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO.roiSinceInception != null)
            {
                THSignedNumber roi = THSignedPercentage.builder(displayablePortfolioDTO.portfolioDTO.roiSinceInception * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                roiValue.setText(roi.toString());
                roiValue.setTextColor(getResources().getColor(roi.getColorResId()));
                roiValue.setVisibility(VISIBLE);
            }
            else if (displayablePortfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO.isWatchlist)
            {
                roiValue.setVisibility(GONE);
            }
            else
            {
                roiValue.setVisibility(VISIBLE);
                roiValue.setText(R.string.na);
            }
        }
    }

    public boolean isThisUserFollowed()
    {
        return currentUserProfileDTO != null && displayablePortfolioDTO != null &&
                currentUserProfileDTO.isFollowingUser(displayablePortfolioDTO.userBaseDTO);
    }
    //</editor-fold>

    protected class PortfolioListItemViewCurrentUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            currentUserProfileDTO = value;
            displayFollowingStamp();
            fetchAdditional();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected class PortfolioListItemViewGetPositionsListener
            implements DTOCacheNew.Listener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onDTOReceived(@NotNull GetPositionsDTOKey key, @NotNull GetPositionsDTO value)
        {
            getPositionsDTO = value;
            DisplayablePortfolioDTO displayablePortfolioDTOCopy =
                    PortfolioListItemView.this.displayablePortfolioDTO;
            if (displayablePortfolioDTOCopy != null && key.equals(
                    displayablePortfolioDTOCopy.ownedPortfolioId))
            {
                PortfolioListItemView.this.linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(@NotNull GetPositionsDTOKey key, @NotNull Throwable error)
        {
            // We do not inform the user as this is not critical
        }
    }

    protected class PortfolioListItemViewWatchedSecurityIdListListener
            implements DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull WatchlistPositionDTOList value)
        {
            watchedSecurityPositions = value;
            DisplayablePortfolioDTO displayablePortfolioDTOCopy =
                    PortfolioListItemView.this.displayablePortfolioDTO;
            if (displayablePortfolioDTOCopy != null &&
                    displayablePortfolioDTOCopy.userBaseDTO != null &&
                    key.equals(displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()))
            {
                PortfolioListItemView.this.linkWith(value, true);
            }
            else
            {
                // Unrelated positions.
            }
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            // We do not inform the user as this is not critical
        }
    }
}
