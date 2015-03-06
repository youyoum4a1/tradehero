package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCacheRx;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import rx.Subscription;
import timber.log.Timber;

import static com.tradehero.th.utils.Constants.MAX_OWN_LEADER_RANKING;

public class LeaderboardMarkUserItemView
        extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>,
        ExpandingLayout.OnExpandListener
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<LeaderboardDefCacheRx> leaderboardDefCache;
    @Inject Lazy<Picasso> picasso;
    @Inject Analytics analytics;
    @Inject THRouter thRouter;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject DashboardNavigator navigator;

    protected UserProfileDTO currentUserProfileDTO;
    protected OnFollowRequestedListener followRequestedListener;
    protected OwnedPortfolioId applicablePortfolioId;
    // data
    protected LeaderboardUserDTO leaderboardItem;

    // top view
    @InjectView(R.id.leaderboard_user_item_display_name) protected TextView lbmuDisplayName;
    @InjectView(R.id.lbmu_roi) protected TextView lbmuRoi;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView lbmuProfilePicture;
    @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;

    // expanding view
    @InjectView(R.id.expanding_layout) ExpandingLayout expandingLayout;
    @InjectView(R.id.lbmu_roi_annualized) TextView lbmuRoiAnnualized;
    @InjectView(R.id.leaderboard_user_item_fof) @Optional @Nullable MarkdownTextView lbmuFoF;
    @InjectView(R.id.leaderboard_user_item_follow) View lbmuFollowUser;
    @InjectView(R.id.leaderboard_user_item_following) View lbmuFollowingUser;
    @InjectView(R.id.leaderboard_user_item_country_logo) @Optional @Nullable ImageView countryLogo;

    @InjectView(R.id.user_statistic_view) @Optional @Nullable UserStatisticView userStatisticView;

    @InjectView(R.id.lbmu_inner_view_container) @Optional @Nullable ViewGroup innerViewContainer;

    @Nullable private Subscription leaderboardOwnUserRankingSubscription;

    public LeaderboardMarkUserItemView(Context context)
    {
        super(context);
    }

    public LeaderboardMarkUserItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public LeaderboardMarkUserItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        HierarchyInjector.inject(this);
        initViews();
    }

    private void initViews()
    {
        ButterKnife.inject(this);
        // top part
        if (lbmuFoF != null)
        {
            HierarchyInjector.inject(lbmuFoF);
        }

        lbmuProfilePicture.setLayerType(LAYER_TYPE_SOFTWARE, null);
        expandingLayout.setOnExpandListener(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        initViews();
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(null);
        }
        loadDefaultUserImage();

        if (lbmuProfilePicture != null)
        {
            lbmuProfilePicture.setImageDrawable(null);
        }
        detachOwnRankingLeaderboardCache();
        leaderboardOwnUserRankingSubscription = null;

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void linkWith(UserProfileDTO currentUserProfileDTO, boolean andDisplay)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        if (andDisplay)
        {
            displayFollow();
            displayIsFollowing();
        }
    }

    public void linkWith(OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public Boolean isCurrentUserFollowing()
    {
        if (currentUserProfileDTO == null || leaderboardItem == null)
        {
            return null;
        }
        return currentUserProfileDTO.isFollowingUser(leaderboardItem.getBaseKey());
    }

    public void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }

    @Override public void display(LeaderboardUserDTO leaderboardUserDTO)
    {
        linkWith(leaderboardUserDTO, true);
    }

    private void detachOwnRankingLeaderboardCache()
    {
        Subscription copy = leaderboardOwnUserRankingSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        leaderboardOwnUserRankingSubscription = null;
    }

    protected void linkWith(LeaderboardUserDTO leaderboardUserDTO, boolean andDisplay)
    {
        this.leaderboardItem = leaderboardUserDTO;

        if (andDisplay)
        {
            display();
        }
    }

    private void display()
    {
        if (leaderboardItem == null)
        {
            return;
        }

        displayTopSection();
        displayExpandableSection();
        displayFollow();
        displayIsFollowing();
    }

    private void displayTopSection()
    {
        displayRankingPosition();
        if (leaderboardItem.getPosition() != null)
        {
            lbmuPosition.setText("" + (leaderboardItem.getPosition() + 1));
        }

        if (lbmuFoF != null)
        {
            lbmuFoF.setVisibility(
                    leaderboardItem.isIncludeFoF() != null && leaderboardItem.isIncludeFoF() &&
                            !StringUtils.isNullOrEmptyOrSpaces(
                                    leaderboardItem.friendOfMarkupString) ? VISIBLE : GONE);
            lbmuFoF.setText(leaderboardItem.friendOfMarkupString);
        }

        linkWith(leaderboardItem);
    }

    public void linkWith(@NonNull UserBaseDTO userBaseDTO)
    {
        displayRankingColor(userBaseDTO);

        lbmuDisplayName.setText(userBaseDTO.displayName);

        loadDefaultUserImage();
        if (userBaseDTO.picture != null)
        {
            picasso.get()
                    .load(userBaseDTO.picture)
                    .transform(peopleIconTransformation)
                    .placeholder(lbmuProfilePicture.getDrawable())
                    .into(lbmuProfilePicture);
        }

        displayCountryLogo(userBaseDTO);
    }

    private void displayRankingColor(@NonNull UserBaseDTO userBaseDTO)
    {
        if (currentUserId.get() == userBaseDTO.id)
        {
            lbmuPosition.setTextColor(
                    getContext().getResources().getColor(R.color.button_green));
        }
        else
        {
            lbmuPosition.setTextColor(
                    getContext().getResources().getColor(R.color.leaderboard_ranking_position));
        }
    }

    private void loadDefaultUserImage()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(lbmuProfilePicture);
    }

    private void displayCountryLogo(UserBaseDTO userBaseDTO)
    {
        if (countryLogo != null)
        {
            try
            {
                int imageResId = Country.getCountryLogo(R.drawable.default_image, userBaseDTO.countryCode);
                countryLogo.setImageResource(imageResId);
            } catch (OutOfMemoryError e)
            {
                Timber.e(e, null);
            }
        }
    }

    protected void displayExpandableSection()
    {
        // display Roi
        THSignedPercentage
                .builder(leaderboardItem.roiInPeriod * 100)
                .withSign()
                .signTypeArrow()
                .relevantDigitCount(3)
                .withDefaultColor()
                .build()
                .into(lbmuRoi);

        // display Roi annualized
        String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
        THSignedPercentage
                .builder(leaderboardItem.roiAnnualizedInPeriod * 100)
                .withSign()
                .signTypeArrow()
                .relevantDigitCount(3)
                .boldValue()
                .format(roiAnnualizedFormat)
                .build()
                .into(lbmuRoiAnnualized);
    }

    protected String getLbmuPlCurrencyDisplay()
    {
        if (leaderboardItem != null)
        {
            return leaderboardItem.getNiceCurrency();
        }
        return SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    private void displayFollow()
    {
        if (lbmuFollowUser != null)
        {
            // you can't follow yourself
            if (currentUserId.get() == leaderboardItem.id)
            {
                lbmuFollowUser.setVisibility(GONE);
                return;
            }

            Boolean isFollowing = isCurrentUserFollowing();
            boolean showButton = isFollowing == null || !isFollowing;
            lbmuFollowUser.setVisibility(showButton ? VISIBLE : GONE);
        }
    }

    private void displayIsFollowing()
    {
        if (lbmuFollowingUser != null)
        {
            Boolean isFollowing = isCurrentUserFollowing();
            boolean showImage = isFollowing != null && isFollowing;
            lbmuFollowingUser.setVisibility(showImage ? VISIBLE : GONE);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.leaderboard_user_item_open_profile)
    protected void handleProfileClicked(View view)
    {
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Profile));
        handleOpenProfileButtonClicked();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.leaderboard_user_item_open_positions_list)
    protected void handlePositionButtonClicked(View view)
    {
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Positions));
        handleOpenPositionListClicked();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.leaderboard_user_item_follow)
    protected void handleFollowButtonClicked(View view)
    {
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Follow));
        follow(leaderboardItem);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.leaderboard_user_item_profile_picture)
    protected void handleUserIconClicked(View view)
    {
        handleOpenProfileButtonClicked();
    }

    protected void follow(@NonNull UserBaseDTO userBaseDTO)
    {
        notifyFollowRequested(userBaseDTO);
    }

    private void handleOpenPositionListClicked()
    {
        GetPositionsDTOKey getPositionsDTOKey = leaderboardItem.getGetPositionsDTOKey();
        if (getPositionsDTOKey == null)
        {
            Timber.e(new NullPointerException(), "Unable to get positions %s", leaderboardItem);
            THToast.show(R.string.leaderboard_friends_position_failed);
            return;
        }

        // get leaderboard definition from cache, supposedly it exists coz this view appears after leaderboard definition list
        LeaderboardDefDTO leaderboardDef = null;
        Integer leaderboardId = leaderboardItem.getLeaderboardId();
        if (leaderboardId != null)
        {
            leaderboardDef = leaderboardDefCache.get()
                    .getValue(new LeaderboardDefKey(leaderboardItem.getLeaderboardId()));
        }

        if (leaderboardItem.lbmuId != -1)
        {
            pushLeaderboardPositionListFragment(getPositionsDTOKey, leaderboardDef);
        }
        else
        {
            pushPositionListFragment(getPositionsDTOKey);
        }
    }

    protected void pushLeaderboardPositionListFragment(GetPositionsDTOKey getPositionsDTOKey, LeaderboardDefDTO leaderboardDefDTO)
    {
        // leaderboard mark user id, to get marking user information
        Bundle bundle = new Bundle();
        LeaderboardPositionListFragment.putGetPositionsDTOKey(bundle, getPositionsDTOKey);
        LeaderboardPositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());
        if (leaderboardDefDTO != null)
        {
            LeaderboardPositionListFragment.putLeaderboardTimeRestricted(bundle, leaderboardDefDTO.isTimeRestrictedLeaderboard());
        }
        SimpleDateFormat sdf =
                new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(leaderboardItem.periodStartUtc);
        LeaderboardPositionListFragment.putLeaderboardPeriodStartString(bundle, formattedStartPeriodUtc);

        if (applicablePortfolioId != null)
        {
            LeaderboardPositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        navigator.pushFragment(LeaderboardPositionListFragment.class, bundle);
    }

    protected void pushPositionListFragment(GetPositionsDTOKey getPositionsDTOKey)
    {
        Bundle bundle = new Bundle();
        PositionListFragment.putGetPositionsDTOKey(bundle, getPositionsDTOKey);
        PositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());

        if (applicablePortfolioId != null)
        {
            PositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        navigator.pushFragment(PositionListFragment.class, bundle);
    }

    protected void handleOpenProfileButtonClicked()
    {
        if (leaderboardItem == null)
        {
            // TODO nicer
            return;
        }
        int userId = leaderboardItem.id;

        openTimeline(userId);
    }

    protected void openTimeline(int userId)
    {
        Bundle bundle = new Bundle();
        UserBaseKey userToSee = new UserBaseKey(userId);
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

    protected void notifyFollowRequested(@NonNull UserBaseDTO userBaseDTO)
    {
        OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseDTO);
        }
    }

    public void displayRankingPosition()
    {
        Integer currentRank = getCurrentRank();
        if (currentRank == null)
        {
            // TODO decide
            return;
        }
        else if (MAX_OWN_LEADER_RANKING < 0 || currentRank <= MAX_OWN_LEADER_RANKING)
        {
            lbmuPosition.setText("" + currentRank);
        }
        else
        {
            lbmuPosition.setText(getContext().getString(R.string.leaderboard_max_ranked_position, MAX_OWN_LEADER_RANKING));
        }

        //Add touch feedback
        if (innerViewContainer != null)
        {
            innerViewContainer.setBackgroundResource(R.drawable.basic_white_selector);
        }
    }

    @Nullable protected Integer getCurrentRank()
    {
        return leaderboardItem == null ? null : (leaderboardItem.ordinalPosition + 1);
    }

    protected void displayUserIsLoading()
    {
        // Strangely, those may be null
        // https://crashlytics.com/tradehero/android/apps/com.tradehero.th/issues/546d39eb65f8dfea1521d9f6
        if (lbmuPosition != null)
        {
            lbmuPosition.setText("-");
        }
        if (lbmuRoi != null)
        {
            lbmuRoi.setText(R.string.loading_required_information);
        }
    }

    protected void displayUserIsNotRanked()
    {
        // disable touch feedback so we don't confuse the user
        if (innerViewContainer != null)
        {
            innerViewContainer.setBackgroundResource(R.color.white);
        }

        if(lbmuRoi!=null)
        {
            lbmuRoi.setText(R.string.leaderboard_not_ranked);
            lbmuPosition.setText("-");
        }
    }

    @Override public void onExpand(boolean expand)
    {
        if (userStatisticView != null && leaderboardItem != null)
        {
            if (expand)
            {
                userStatisticView.display(leaderboardItem);
            }
            else
            {
                userStatisticView.display(null);
                Timber.d("clearExpandAnimation");
            }
        }
    }

    public static interface OnFollowRequestedListener
    {
        void onFollowRequested(@NonNull UserBaseDTO userBaseKey);
    }
}
