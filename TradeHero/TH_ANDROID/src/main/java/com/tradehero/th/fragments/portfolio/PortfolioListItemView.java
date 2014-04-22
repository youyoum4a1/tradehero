package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THSignedNumber;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class PortfolioListItemView extends RelativeLayout implements DTOView<DisplayablePortfolioDTO>,View.OnClickListener
{
    @InjectView(R.id.follower_profile_picture) @Optional protected ImageView userIcon;
    @InjectView(R.id.portfolio_title) protected TextView title;
    @InjectView(R.id.portfolio_description) protected TextView description;
    @InjectView(R.id.following_image) @Optional protected ImageView followingStamp;
    @InjectView(R.id.roi_value) @Optional protected TextView roiValue;

    private DisplayablePortfolioDTO displayablePortfolioDTO;
    private GetPositionsDTO getPositionsDTO;
    private SecurityIdList watchedSecurityIds;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userImageTransformation;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject GetPositionsCache getPositionsCache;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject DisplayablePortfolioUtil displayablePortfolioUtil;

    private UserProfileRetrievedMilestone currentUserProfileRetrievedMilestone;
    private Milestone.OnCompleteListener currentUserProfileRetrievedMilestoneListener;

    private DTOCache.Listener<OwnedPortfolioId, GetPositionsDTO> getPositionsListener;
    private DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> getPositionsFetchTask;

    private DTOCache.Listener<UserBaseKey, SecurityIdList> userWatchlistListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> userWatchlistFetchTask;

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
    }

    @Override protected void onAttachedToWindow()
    {
        //THLog.d(TAG, "onAttachedToWindow ");
        super.onAttachedToWindow();

        this.currentUserProfileRetrievedMilestoneListener = new PortfolioListItemViewUserProfileRetrievedListener();
        this.getPositionsListener = new PortfolioListItemViewGetPositionsListener();
        this.userWatchlistListener = new PortfolioListItemViewWatchedSecurityIdListListener();
        if (this.userIcon != null)
        {
            this.userIcon.setOnClickListener(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        //THLog.d(TAG, "onDetachedFromWindow ");
        this.currentUserProfileRetrievedMilestoneListener = null;
        detachMilestone();

        this.getPositionsListener = null;
        detachGetPositionsTask();

        this.userWatchlistListener = null;
        detachUserWatchlistTask();
        if (this.userIcon != null)
        {
            this.userIcon.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.follower_profile_picture){
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null) {
                handleUserIconClicked();
            }
        }
    }

    private void handleUserIconClicked(){
        //THToast.show(String.format("user icon click %s",displayablePortfolioDTO.userBaseDTO.displayName));
        TimelineFragment.viewProfile((DashboardActivity) getContext(), displayablePortfolioDTO.userBaseDTO.id);
    }

    protected void detachMilestone()
    {
        Milestone milestoneCopy = this.currentUserProfileRetrievedMilestone;
        if (milestoneCopy != null)
        {
            milestoneCopy.setOnCompleteListener(null);
        }
        this.currentUserProfileRetrievedMilestone = null;
    }

    protected void detachGetPositionsTask()
    {
        DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> positionTaskCopy = this.getPositionsFetchTask;
        if (positionTaskCopy != null)
        {
            positionTaskCopy.setListener(null);
        }
        this.getPositionsFetchTask = null;
    }

    protected void detachUserWatchlistTask()
    {
        DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> securityTaskCopy = this.userWatchlistFetchTask;
        if (securityTaskCopy != null)
        {
            securityTaskCopy.setListener(null);
        }
        this.userWatchlistFetchTask = null;
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

        fetchNecessaryInfo();

        if (andDisplay)
        {
            displayUserIcon();
            displayTitle();
            displayDescription();
            displayFollowingStamp();
            displayRoiValue();
        }
    }

    protected void fetchNecessaryInfo()
    {
        UserProfileRetrievedMilestone milestone = new UserProfileRetrievedMilestone(currentUserId.toUserBaseKey());
        milestone.setOnCompleteListener(this.currentUserProfileRetrievedMilestoneListener);
        this.currentUserProfileRetrievedMilestone = milestone;
        milestone.launch();
    }

    protected void fetchAdditional()
    {
        this.fetchGetPositions();
        this.fetchWatchedSecurities();
    }

    protected void fetchGetPositions()
    {
        detachGetPositionsTask();
        DisplayablePortfolioDTO displayablePortfolioDTOCopy = this.displayablePortfolioDTO;
        if (displayablePortfolioDTOCopy != null &&
                displayablePortfolioDTOCopy.ownedPortfolioId != null &&
                displayablePortfolioDTOCopy.portfolioDTO != null &&
                !displayablePortfolioDTOCopy.portfolioDTO.isWatchlist&&
                displayablePortfolioDTOCopy.userBaseDTO != null &&
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey().equals(currentUserId.toUserBaseKey()))
        {
            DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> task = this.getPositionsCache.getOrFetch(displayablePortfolioDTOCopy.ownedPortfolioId, getPositionsListener);
            this.getPositionsFetchTask = task;
            task.execute();
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
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey().equals(currentUserId.toUserBaseKey()))
        {
            Timber.d("fetchWatchedSecurities launching");
            DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> task = this.userWatchlistPositionCache.getOrFetch(displayablePortfolioDTOCopy.userBaseDTO.getBaseKey(), userWatchlistListener);
            this.userWatchlistFetchTask = task;
            task.execute();
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

    protected void linkWith(SecurityIdList securityIdList, boolean andDisplay)
    {
        this.watchedSecurityIds = securityIdList;
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
                THSignedNumber roi = new THSignedNumber(
                        THSignedNumber.TYPE_PERCENTAGE,
                        displayablePortfolioDTO.portfolioDTO.roiSinceInception * 100,
                        true,
                        null,
                        THSignedNumber.TYPE_SIGN_ARROW);
                roiValue.setText(roi.toString(1));
                roiValue.setTextColor(getResources().getColor(roi.getColor()));
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
        UserProfileDTO currentUserProfile = userProfileCache.get(currentUserId.toUserBaseKey());
        return currentUserProfile != null && displayablePortfolioDTO != null &&
                currentUserProfile.isFollowingUser(displayablePortfolioDTO.userBaseDTO);
    }
    //</editor-fold>

    private class PortfolioListItemViewUserProfileRetrievedListener implements Milestone.OnCompleteListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            displayFollowingStamp();
            fetchAdditional();
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    private class PortfolioListItemViewGetPositionsListener implements DTOCache.Listener<OwnedPortfolioId, GetPositionsDTO>
    {
        public PortfolioListItemViewGetPositionsListener()
        {
        }

        @Override public void onDTOReceived(OwnedPortfolioId key, GetPositionsDTO value, boolean fromCache)
        {
            DisplayablePortfolioDTO displayablePortfolioDTOCopy = PortfolioListItemView.this.displayablePortfolioDTO;
            if (key != null && displayablePortfolioDTOCopy != null && key.equals(displayablePortfolioDTOCopy.ownedPortfolioId))
            {
                PortfolioListItemView.this.linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
        {
            // We do not inform the user as this is not critical
        }
    }

    private class PortfolioListItemViewWatchedSecurityIdListListener implements DTOCache.Listener<UserBaseKey, SecurityIdList>
    {
        public PortfolioListItemViewWatchedSecurityIdListListener()
        {
        }

        @Override public void onDTOReceived(UserBaseKey key, SecurityIdList value, boolean fromCache)
        {
            DisplayablePortfolioDTO displayablePortfolioDTOCopy = PortfolioListItemView.this.displayablePortfolioDTO;
            if (key != null && displayablePortfolioDTOCopy != null &&
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

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            // We do not inform the user as this is not critical
        }
    }



}
