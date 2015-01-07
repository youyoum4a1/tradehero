package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.position.GetPositionsCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCacheRx;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PortfolioListItemView extends RelativeLayout
        implements DTOView<DisplayablePortfolioDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject GetPositionsCacheRx getPositionsCache;
    @Inject UserWatchlistPositionCacheRx userWatchlistPositionCache;
    @Inject DisplayablePortfolioUtil displayablePortfolioUtil;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userImageTransformation;
    @Inject DashboardNavigator navigator;

    @InjectView(R.id.follower_profile_picture) @Optional protected ImageView userIcon;
    @InjectView(R.id.portfolio_title) protected TextView title;
    @InjectView(R.id.portfolio_description) protected TextView description;
    @InjectView(R.id.roi_value) @Optional protected TextView roiValue;

    private DisplayablePortfolioDTO displayablePortfolioDTO;
    @Nullable private Subscription currentUserProfileCacheSubscription;
    private UserProfileDTO currentUserProfileDTO;
    @Nullable private Subscription getPositionsSubscription;
    private GetPositionsDTO getPositionsDTO;
    @Nullable private Subscription userWatchlistSubscription;
    private WatchlistPositionDTOList watchedSecurityPositions;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PortfolioListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
        if (userIcon != null && picasso != null)
        {
            displayDefaultUserIcon();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        unsubscribe(currentUserProfileCacheSubscription);
        currentUserProfileCacheSubscription = null;
        unsubscribe(getPositionsSubscription);
        getPositionsSubscription = null;
        unsubscribe(userWatchlistSubscription);
        userWatchlistSubscription = null;

        if (this.userIcon != null)
        {
            this.userIcon.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.follower_profile_picture) @Optional
    protected void handleUserIconClicked()
    {
        if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null)
        {
            Bundle bundle = new Bundle();
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

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public void display(DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        linkWith(displayablePortfolioDTO, true);
    }

    public void linkWith(DisplayablePortfolioDTO displayablePortfolioDTO, boolean andDisplay)
    {
        this.displayablePortfolioDTO = displayablePortfolioDTO;

        fetchCurrentUserProfile();
        fetchAdditional();

        if (andDisplay)
        {
            displayUserIcon();
            displayTitle();
            displayDescription();
            displayRoiValue();
        }
    }

    protected void fetchCurrentUserProfile()
    {
        if (currentUserProfileCacheSubscription == null)
        {
            currentUserProfileCacheSubscription = userProfileCache.get(currentUserId.toUserBaseKey())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PortfolioListItemViewCurrentUserProfileCacheObserver());
        }
    }

    protected void fetchAdditional()
    {
        this.fetchGetPositions();
        this.fetchWatchedSecurities();
    }

    protected void fetchGetPositions()
    {
        DisplayablePortfolioDTO displayablePortfolioDTOCopy = this.displayablePortfolioDTO;
        if (displayablePortfolioDTOCopy != null &&
                displayablePortfolioDTOCopy.ownedPortfolioId != null &&
                displayablePortfolioDTOCopy.portfolioDTO != null &&
                !displayablePortfolioDTOCopy.portfolioDTO.isWatchlist &&
                displayablePortfolioDTOCopy.userBaseDTO != null &&
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()
                        .equals(currentUserId.toUserBaseKey())
                && getPositionsSubscription == null)
        {
            getPositionsSubscription = getPositionsCache.get(displayablePortfolioDTOCopy.ownedPortfolioId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PortfolioListItemViewGetPositionsObserver());
        }
    }

    protected void fetchWatchedSecurities()
    {
        DisplayablePortfolioDTO displayablePortfolioDTOCopy = this.displayablePortfolioDTO;
        if (displayablePortfolioDTOCopy != null &&
                displayablePortfolioDTOCopy.ownedPortfolioId != null &&
                displayablePortfolioDTOCopy.portfolioDTO != null &&
                displayablePortfolioDTOCopy.portfolioDTO.isWatchlist &&
                displayablePortfolioDTOCopy.userBaseDTO != null &&
                displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()
                        .equals(currentUserId.toUserBaseKey()) &&
                userWatchlistSubscription == null)
        {
            Timber.d("fetchWatchedSecurities launching");
            UserBaseKey key = displayablePortfolioDTOCopy.userBaseDTO.getBaseKey();
            userWatchlistSubscription = userWatchlistPositionCache.get(key)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PortfolioListItemViewWatchedSecurityIdListObserver());
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
            title.setText(displayablePortfolioUtil.getLongTitle(getContext(),
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

    public void displayRoiValue()
    {
        if (roiValue != null)
        {
            if (displayablePortfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO != null &&
                    displayablePortfolioDTO.portfolioDTO.roiSinceInception != null)
            {
                THSignedPercentage.builder(displayablePortfolioDTO.portfolioDTO.roiSinceInception * 100)
                        .withSign()
                        .signTypeArrow()
                        .build()
                        .into(roiValue);
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
    //</editor-fold>

    protected class PortfolioListItemViewCurrentUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            currentUserProfileDTO = pair.second;
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected class PortfolioListItemViewGetPositionsObserver
            implements Observer<Pair<GetPositionsDTOKey, GetPositionsDTO>>
    {
        @Override public void onNext(Pair<GetPositionsDTOKey, GetPositionsDTO> pair)
        {
            getPositionsDTO = pair.second;
            DisplayablePortfolioDTO displayablePortfolioDTOCopy =
                    PortfolioListItemView.this.displayablePortfolioDTO;
            if (displayablePortfolioDTOCopy != null && pair.first.equals(
                    displayablePortfolioDTOCopy.ownedPortfolioId))
            {
                PortfolioListItemView.this.linkWith(pair.second, true);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "When getting positionsDTO");
            // We do not inform the user as this is not critical
        }
    }

    protected class PortfolioListItemViewWatchedSecurityIdListObserver
            implements Observer<Pair<UserBaseKey, WatchlistPositionDTOList>>
    {
        @Override public void onNext(Pair<UserBaseKey, WatchlistPositionDTOList> pair)
        {
            watchedSecurityPositions = pair.second;
            DisplayablePortfolioDTO displayablePortfolioDTOCopy =
                    PortfolioListItemView.this.displayablePortfolioDTO;
            if (displayablePortfolioDTOCopy != null &&
                    displayablePortfolioDTOCopy.userBaseDTO != null &&
                    pair.first.equals(displayablePortfolioDTOCopy.userBaseDTO.getBaseKey()))
            {
                PortfolioListItemView.this.linkWith(pair.second, true);
            }
            else
            {
                // Unrelated positions.
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            // We do not inform the user as this is not critical
        }
    }
}
