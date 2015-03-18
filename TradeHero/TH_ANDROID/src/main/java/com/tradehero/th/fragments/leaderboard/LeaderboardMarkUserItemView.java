package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Pair;
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
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.internal.util.SubscriptionList;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.tradehero.th.utils.Constants.MAX_OWN_LEADER_RANKING;

public class LeaderboardMarkUserItemView
        extends RelativeLayout
        implements DTOView<LeaderboardMarkUserItemView.DTO>
{
    @Inject Lazy<Picasso> picasso;
    @Inject Analytics analytics;

    protected OwnedPortfolioId applicablePortfolioId;
    // data
    @Nullable protected DTO viewDTO;
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
    @InjectView(R.id.mark_expand_down) @Optional @Nullable ImageView expandMark;

    @InjectView(R.id.user_statistic_view) @Optional @Nullable UserStatisticView userStatisticView;

    @InjectView(R.id.lbmu_inner_view_container) @Optional @Nullable ViewGroup innerViewContainer;

    @NonNull protected SubscriptionList subscriptions;
    @NonNull protected PublishSubject<UserAction> userActionSubject;

    protected UserProfileDTO currentUserProfileDTO;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserItemView(Context context)
    {
        super(context);
        init();
    }

    public LeaderboardMarkUserItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public LeaderboardMarkUserItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        subscriptions = new SubscriptionList();
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
        if (lbmuFoF != null)
        {
            HierarchyInjector.inject(lbmuFoF);
        }
        lbmuProfilePicture.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        expandingLayout.setOnExpandListener(new ExpandingLayout.OnExpandListener()
        {
            @Override public void onExpand(boolean expand)
            {
                setExpanded(expand);
            }
        });
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.get().cancelRequest(lbmuProfilePicture);
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(null);
        }

        subscriptions.unsubscribe();

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void linkWith(OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @NonNull public Observable<UserAction> getFollowRequestedObservable()
    {
        return userActionSubject.asObservable();
    }

    @Override public void display(@NonNull DTO viewDTO)
    {
        this.viewDTO = viewDTO;

        if (lbmuDisplayName != null)
        {
            lbmuDisplayName.setText(viewDTO.lbmuDisplayName);
        }
        if (lbmuRoi != null)
        {
            lbmuRoi.setText(viewDTO.lbmuRoi);
        }
        if (lbmuPosition != null)
        {
            lbmuPosition.setText(getPosition(viewDTO));
            lbmuPosition.setTextColor(viewDTO.lbmuPositionColor);
        }
        if (lbmuRoiAnnualized != null)
        {
            lbmuRoiAnnualized.setText(viewDTO.lbmuRoiAnnualized);
        }
        if (lbmuFoF != null)
        {
            lbmuFoF.setText(viewDTO.lbmuFoF);
            lbmuFoF.setVisibility(viewDTO.lbmuFoFVisibility);
        }
        if (lbmuProfilePicture != null)
        {
            if (viewDTO.leaderboardUserDTO.picture != null)
            {
                picasso.get()
                        .load(viewDTO.leaderboardUserDTO.picture)
                        .into(lbmuProfilePicture);
            }
            else
            {
                picasso.get().load(R.drawable.superman_facebook)
                        .into(lbmuProfilePicture);
            }
        }

        if (lbmuFollowUser != null)
        {
            lbmuFollowUser.setVisibility(viewDTO.lbmuFollowUserVisibility);
        }
        if (lbmuFollowingUser != null)
        {
            lbmuFollowingUser.setVisibility(viewDTO.lbmuFollowingUserVisibility);
        }
    }

    @NonNull private String getPosition(DTO viewDTO)
    {
        LeaderboardUserDTO leaderboardItem = viewDTO.leaderboardUserDTO;
        Integer currentRank = leaderboardItem.ordinalPosition + 1;
        if (leaderboardItem.getPosition() != null)
        {
            return "" + (leaderboardItem.getPosition() + 1);
        }
        else //noinspection PointlessBooleanExpression,ConstantConditions
            if (MAX_OWN_LEADER_RANKING < 0 || currentRank <= MAX_OWN_LEADER_RANKING)
            {
                return "" + currentRank;
            }
            else
            {
                return viewDTO.maxOwnLeaderRanking;
            }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.leaderboard_user_item_open_profile, R.id.leaderboard_user_item_profile_picture})
    protected void handleProfileClicked(View view)
    {
        if (viewDTO != null)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Profile));
            userActionSubject.onNext(new UserAction(viewDTO, UserActionType.PROFILE));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.leaderboard_user_item_open_positions_list)
    protected void handlePositionButtonClicked(View view)
    {
        if (viewDTO != null)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Positions));
            userActionSubject.onNext(new UserAction(viewDTO, UserActionType.POSITIONS));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.leaderboard_user_item_follow)
    protected void handleFollowButtonClicked(View view)
    {
        if (viewDTO != null)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Follow));
            userActionSubject.onNext(new UserAction(viewDTO, UserActionType.FOLLOW));
        }
    }

    @Deprecated // Perhaps
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

    protected void displayUserIsNotRanked(@Nullable UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        lbmuRoi.setText(R.string.leaderboard_not_ranked);
        lbmuPosition.setText("-");

        if (currentUserProfileDTO == null)
        {
            return;
        }

        if (lbmuDisplayName != null)
        {
            lbmuDisplayName.setText(currentUserProfileDTO.displayName);
        }

        if (lbmuProfilePicture != null)
        {
            if (currentUserProfileDTO.picture != null)
            {
                picasso.get()
                        .load(currentUserProfileDTO.picture)
                        .into(lbmuProfilePicture);
            }
            else
            {
                picasso.get().load(R.drawable.superman_facebook)
                        .into(lbmuProfilePicture);
            }
        }
    }

    public void setExpanded(boolean expand)
    {
        if (userStatisticView != null && viewDTO != null && expand)
        {
            userStatisticView.display(viewDTO.userStatisticsDto);
        }
    }

    public static class DTO implements com.tradehero.common.persistence.DTO, ExpandableItem
    {
        @NonNull final CurrentUserId currentUserId;
        @NonNull final LeaderboardUserDTO leaderboardUserDTO;
        @NonNull final UserStatisticView.DTO userStatisticsDto;
        final String lbmuDisplayName;
        @NonNull final Spanned lbmuRoi;
        final int lbmuPositionColor;
        @NonNull final Spanned lbmuRoiAnnualized;
        final String lbmuFoF;
        @ViewVisibilityValue final int lbmuFoFVisibility;
        @ViewVisibilityValue final int lbmuFollowUserVisibility;
        @ViewVisibilityValue final int lbmuFollowingUserVisibility;
        private String maxOwnLeaderRanking;
        private boolean expanded;

        public DTO(@NonNull Resources resources,
                @NonNull CurrentUserId currentUserId,
                @NonNull LeaderboardUserDTO leaderboardItem,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            this.currentUserId = currentUserId;
            this.leaderboardUserDTO = leaderboardItem;
            this.userStatisticsDto = new UserStatisticView.DTO(resources, leaderboardUserDTO, currentUserProfileDTO.mostSkilledLbmu);
            this.lbmuDisplayName = leaderboardItem.displayName;
            this.lbmuRoi = THSignedPercentage
                    .builder(leaderboardItem.roiInPeriod * 100)
                    .signTypePlusMinusAlways()
                    .relevantDigitCount(3)
                    .withDefaultColor()
                    .build().createSpanned();

            maxOwnLeaderRanking = resources.getString(R.string.leaderboard_max_ranked_position, MAX_OWN_LEADER_RANKING);

            this.lbmuPositionColor = currentUserId.get() == leaderboardItem.id ?
                    resources.getColor(R.color.light_green_normal) :
                    resources.getColor(R.color.text_primary);
            String roiAnnualizedFormat = resources.getString(R.string.leaderboard_roi_annualized);
            this.lbmuRoiAnnualized = THSignedPercentage
                    .builder(leaderboardItem.roiAnnualizedInPeriod * 100)
                    .withSign()
                    .signTypeArrow()
                    .relevantDigitCount(3)
                    .boldValue()
                    .format(roiAnnualizedFormat)
                    .build().createSpanned();
            this.lbmuFoF = leaderboardItem.friendOfMarkupString;
            this.lbmuFoFVisibility = leaderboardItem.isIncludeFoF() != null
                    && leaderboardItem.isIncludeFoF()
                    && !StringUtils.isNullOrEmptyOrSpaces(leaderboardItem.friendOfMarkupString)
                    ? VISIBLE
                    : GONE;

            if (currentUserId.get() == leaderboardItem.id)
            {
                // you can't follow yourself
                lbmuFollowUserVisibility = GONE;
            }
            else
            {
                lbmuFollowUserVisibility = !currentUserProfileDTO.isFollowingUser(leaderboardItem.getBaseKey())
                        ? VISIBLE
                        : GONE;
            }
            lbmuFollowingUserVisibility = currentUserProfileDTO.isFollowingUser(leaderboardItem.getBaseKey())
                    ? VISIBLE
                    : GONE;
        }

        @Override public boolean isExpanded()
        {
            return expanded;
        }

        @Override public void setExpanded(boolean expanded)
        {
            this.expanded = expanded;
        }
    }

    public static class DTOList extends BaseArrayList<DTO> implements
            com.tradehero.common.persistence.DTO,
            ContainerDTO<DTO, DTOList>
    {
        @NonNull public final LeaderboardDTO leaderboardDTO;

        protected DTOList(@NonNull LeaderboardDTO leaderboardDTO)
        {
            this.leaderboardDTO = leaderboardDTO;
        }

        public DTOList(@NonNull Resources resources,
                @NonNull CurrentUserId currentUserId,
                @NonNull LeaderboardDTO leaderboardDTO,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            long before = System.nanoTime();
            this.leaderboardDTO = leaderboardDTO;
            for (LeaderboardUserDTO leaderboardItem : leaderboardDTO.getList())
            {
                add(new DTO(resources, currentUserId, leaderboardItem, currentUserProfileDTO));
            }
            Timber.d("Leaderboard " + ((System.nanoTime() - before) / 1000));
        }

        @Override public DTOList getList()
        {
            return this;
        }
    }

    public static class Requisite
    {
        @Nullable final LeaderboardUserDTO currentLeaderboardUserDTO;
        @NonNull final UserProfileDTO currentUserProfileDTO;

        public Requisite(
                @NonNull Pair<LeaderboardKey, LeaderboardDTO> currentLeaderboardPair,
                @NonNull Pair<UserBaseKey, UserProfileDTO> currentUserProfilePair)
        {
            this(currentLeaderboardPair.second, currentUserProfilePair.second);
        }

        public Requisite(
                @NonNull LeaderboardDTO currentLeaderboardDTO,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            this(currentLeaderboardDTO.users == null || currentLeaderboardDTO.users.size() == 0
                            ? null
                            : currentLeaderboardDTO.users.get(0),
                    currentUserProfileDTO);
        }

        public Requisite(
                @Nullable LeaderboardUserDTO currentLeaderboardUserDTO,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            this.currentLeaderboardUserDTO = currentLeaderboardUserDTO;
            this.currentUserProfileDTO = currentUserProfileDTO;
        }
    }

    public enum UserActionType
    {
        PROFILE, POSITIONS, FOLLOW, RULES
    }

    public static class UserAction
    {
        @NonNull public final DTO dto;
        @NonNull public final UserActionType actionType;

        public UserAction(
                @NonNull DTO dto,
                @NonNull UserActionType actionType)
        {
            this.dto = dto;
            this.actionType = actionType;
        }
    }
}
