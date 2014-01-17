package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class PortfolioListItemView extends RelativeLayout implements DTOView<DisplayablePortfolioDTO>
{
    public static final String TAG = PortfolioListItemView.class.getSimpleName();
    private static int countUp = 0;

    private ImageView userIcon;
    private TextView title;
    private TextView description;
    private ImageView followingStamp;

    private DisplayablePortfolioDTO displayablePortfolioDTO;
    private GetPositionsDTO getPositionsDTO;
    private SecurityIdList watchedSecurityIds;
    @Inject Picasso picasso;
    @Inject CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject UserProfileCache userProfileCache;
    @Inject GetPositionsCache getPositionsCache;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;

    private UserProfileRetrievedMilestone currentUserProfileRetrievedMilestone;
    private Milestone.OnCompleteListener currentUserProfileRetrievedMilestoneListener;

    private DTOCache.Listener<OwnedPortfolioId, GetPositionsDTO> getPositionsListener;
    private DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> getPositionsFetchTask;

    private DTOCache.Listener<UserBaseKey, SecurityIdList> userWatchlistListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> userWatchlistFetchTask;

    private final int count = countUp++;

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
        initViews();
        DaggerUtils.inject(this);
        if (userIcon != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(new RoundedShapeTransformation())
                    .into(userIcon);
        }
    }

    private void initViews()
    {
        userIcon = (ImageView) findViewById(R.id.follower_profile_picture);
        title = (TextView) findViewById(R.id.portfolio_title);
        description = (TextView) findViewById(R.id.portfolio_description);
        followingStamp = (ImageView) findViewById(R.id.following_image);
    }

    @Override protected void onAttachedToWindow()
    {
        THLog.d(TAG, "onAttachedToWindow " + count);
        super.onAttachedToWindow();
        this.currentUserProfileRetrievedMilestoneListener = new PortfolioListItemViewUserProfileRetrievedListener();

        UserProfileRetrievedMilestone milestone = new UserProfileRetrievedMilestone(this.currentUserBaseKeyHolder.getCurrentUserBaseKey());
        milestone.setOnCompleteListener(this.currentUserProfileRetrievedMilestoneListener);
        this.currentUserProfileRetrievedMilestone = milestone;

        this.getPositionsListener = new PortfolioListItemViewGetPositionsListener();
        this.userWatchlistListener = new PortfolioListItemViewWatchedSecurityIdListListener();

        milestone.launch();
    }

    @Override protected void onDetachedFromWindow()
    {
        THLog.d(TAG, "onDetachedFromWindow " + count);
        this.currentUserProfileRetrievedMilestoneListener = null;
        detachMilestone();

        this.getPositionsListener = null;
        detachGetPositionsTask();

        this.userWatchlistListener = null;
        detachUserWatchlistTask();

        super.onDetachedFromWindow();
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
        THLog.d(TAG, "display");
        linkWith(displayablePortfolioDTO, true);
    }

    public void linkWith(DisplayablePortfolioDTO displayablePortfolioDTO, boolean andDisplay)
    {
        this.displayablePortfolioDTO = displayablePortfolioDTO;

        if (andDisplay)
        {
            displayUserIcon();
            displayTitle();
            displayDescription();
            displayFollowingStamp();
        }
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
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey().equals(currentUserBaseKeyHolder.getCurrentUserBaseKey()))
        {
            THLog.d(TAG, "fetchGetPositions launching");
            DTOCache.GetOrFetchTask<OwnedPortfolioId, GetPositionsDTO> task = this.getPositionsCache.getOrFetch(displayablePortfolioDTOCopy.ownedPortfolioId, getPositionsListener);
            this.getPositionsFetchTask = task;
            task.execute();
        }
        else
        {
            THLog.d(TAG, "fetchGetPositions nothing to launch");

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
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey().equals(currentUserBaseKeyHolder.getCurrentUserBaseKey()))
        {
            THLog.d(TAG, "fetchWatchedSecurities launching");
            DTOCache.GetOrFetchTask<UserBaseKey, SecurityIdList> task = this.userWatchlistPositionCache.getOrFetch(displayablePortfolioDTOCopy.userBaseDTO.getBaseKey(), userWatchlistListener);
            this.userWatchlistFetchTask = task;
            task.execute();
        }
        else
        {
            THLog.d(TAG, "fetchWatchedSecurities nothing to launch");

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
    }

    public void displayUserIcon()
    {
        if (userIcon != null)
        {
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null)
            {
                picasso.load(displayablePortfolioDTO.userBaseDTO.picture)
                             .transform(new RoundedShapeTransformation())
                             .into(userIcon);
            }
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(DisplayablePortfolioUtil.getLongTitle(getContext(), displayablePortfolioDTO));
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
        DisplayablePortfolioDTO displayablePortfolioDTOCopy = this.displayablePortfolioDTO;

        if (displayablePortfolioDTOCopy == null || displayablePortfolioDTOCopy.userBaseDTO == null ||
                displayablePortfolioDTOCopy.portfolioDTO == null)
        {
            return "";
        }

        // When this is another user
        if (!currentUserBaseKeyHolder.getCurrentUserBaseKey().equals(displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()))
        {
            return UserBaseDTOUtil.getFirstLastName(getContext(), displayablePortfolioDTOCopy.userBaseDTO);
        }

        // When this is current user
        GetPositionsDTO getPositionsDTOCopy = this.getPositionsDTO;
        SecurityIdList watchedSecurityIdsCopy = this.watchedSecurityIds;

        if (!displayablePortfolioDTOCopy.portfolioDTO.isWatchlist && getPositionsDTOCopy != null)
        {
            List<PositionDTO> openPositions = getPositionsDTOCopy.getOpenPositions();
            if (openPositions != null && openPositions.size() > 0)
            {
                return getResources().getString(R.string.portfolio_description_count_open_positions, openPositions.size());
            }

            List<PositionDTO> closedPositions = getPositionsDTOCopy.getClosedPositions();
            if (closedPositions != null && closedPositions.size() > 0)
            {
                return getResources().getString(R.string.portfolio_description_count_closed_positions, closedPositions.size());
            }
        }
        if (displayablePortfolioDTOCopy.portfolioDTO.isWatchlist && watchedSecurityIdsCopy != null)
        {
            return getResources().getString(R.string.portfolio_description_count_watchlist, watchedSecurityIdsCopy.size());
        }
        return "";
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

    public boolean isThisUserFollowed()
    {
        UserProfileDTO currentUserProfile = userProfileCache.get(currentUserBaseKeyHolder.getCurrentUserBaseKey());
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
            THLog.e(TAG, "Failed to fetch user profile", throwable);
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    private class PortfolioListItemViewGetPositionsListener implements DTOCache.Listener<OwnedPortfolioId, GetPositionsDTO>
    {
        public PortfolioListItemViewGetPositionsListener()
        {
        }

        @Override public void onDTOReceived(OwnedPortfolioId key, GetPositionsDTO value)
        {
            DisplayablePortfolioDTO displayablePortfolioDTOCopy = PortfolioListItemView.this.displayablePortfolioDTO;
            if (key != null && displayablePortfolioDTOCopy != null && key.equals(displayablePortfolioDTOCopy.ownedPortfolioId))
            {
                THLog.d(TAG, "onDTOReceived getPositions passing on");
                PortfolioListItemView.this.linkWith(value, true);
            }
            else
            {
                THLog.d(TAG, "onDTOReceived getPositions not passing on");
                // Unrelated positions.
            }
        }

        @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
        {
            THLog.e(TAG, "Failed to fetch get positions", error);
            // We do not inform the user as this is not critical
        }
    }

    private class PortfolioListItemViewWatchedSecurityIdListListener implements DTOCache.Listener<UserBaseKey, SecurityIdList>
    {
        public PortfolioListItemViewWatchedSecurityIdListListener()
        {
        }

        @Override public void onDTOReceived(UserBaseKey key, SecurityIdList value)
        {
            DisplayablePortfolioDTO displayablePortfolioDTOCopy = PortfolioListItemView.this.displayablePortfolioDTO;
            if (key != null && displayablePortfolioDTOCopy != null &&
                    displayablePortfolioDTOCopy.userBaseDTO != null &&
                    key.equals(displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()))
            {
                THLog.d(TAG, "onDTOReceived watchedSecurities passing on");
                PortfolioListItemView.this.linkWith(value, true);
            }
            else
            {
                THLog.d(TAG, "onDTOReceived watchedSecurities not passing on");
                // Unrelated positions.
            }
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THLog.e(TAG, "Failed to fetch watched securities", error);
            // We do not inform the user as this is not critical
        }
    }
}
