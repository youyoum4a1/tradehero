package com.tradehero.th.fragments.leaderboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.social.FollowRequest;
import com.tradehero.th.models.user.follow.ChoiceFollowUserAssistantWithDialog;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.route.THRouter;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class LeaderboardMarkUserListFragmentUtil
        implements Action1<LeaderboardMarkUserItemView.UserAction>
{
    @NonNull private final DashboardNavigator navigator;
    @NonNull private final THRouter thRouter;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final LeaderboardDefCacheRx leaderboardDefCache;
    @NonNull private final Analytics analytics;
    @NonNull private final ProviderUtil providerUtil;

    private BaseLeaderboardPagedListRxFragment fragment;
    private SubscriptionList onStopSubscriptions;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardMarkUserListFragmentUtil(
            @NonNull DashboardNavigator navigator,
            @NonNull THRouter thRouter,
            @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardDefCacheRx leaderboardDefCache,
            @NonNull Analytics analytics,
            @NonNull ProviderUtil providerUtil)
    {
        this.navigator = navigator;
        this.thRouter = thRouter;
        this.currentUserId = currentUserId;
        this.leaderboardDefCache = leaderboardDefCache;
        this.analytics = analytics;
        this.providerUtil = providerUtil;
    }
    //</editor-fold>

    public void linkWith(@NonNull BaseLeaderboardPagedListRxFragment fragment)
    {
        this.fragment = fragment;
    }

    public void onStart()
    {
        onStopSubscriptions = new SubscriptionList();
    }

    public void onStop()
    {
        onStopSubscriptions.unsubscribe();
    }

    @Override public void call(LeaderboardMarkUserItemView.UserAction userAction)
    {
        switch (userAction.actionType)
        {
            case PROFILE:
                openTimeline(userAction.dto);
                break;

            case FOLLOW:
                handleFollowRequested(userAction.dto.leaderboardUserDTO);
                break;

            case POSITIONS:
                handlePositionsRequested(userAction.dto);
                break;

            case RULES:
                handleRulesRequested((CompetitionLeaderboardMarkUserItemView.DTO) userAction.dto);
                break;
        }
    }

    protected void openTimeline(@NonNull LeaderboardMarkUserItemView.DTO dto)
    {
        Bundle bundle = new Bundle();
        UserBaseKey userToSee = dto.leaderboardUserDTO.getBaseKey();
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

    protected void handleFollowRequested(@NonNull final UserBaseDTO userBaseDTO)
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                fragment,
                new ChoiceFollowUserAssistantWithDialog(
                        fragment.getActivity(),
                        userBaseDTO
                        // ,getApplicablePortfolioId()
                ).launchChoiceRx())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<FollowRequest, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<FollowRequest, UserProfileDTO> pair)
                            {
                                fragment.setCurrentUserProfileDTO(pair.second);
                                int followType = pair.second.getFollowType(userBaseDTO);
                                if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
                                {
                                    analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));
                                }
                                else if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
                                {
                                    analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success, AnalyticsConstants.Leaderboard));
                                }
                                fragment.updateListViewRow(userBaseDTO.getBaseKey());
                            }
                        },
                        new ToastOnErrorAction()
                ));
    }

    protected void handlePositionsRequested(@NonNull LeaderboardMarkUserItemView.DTO dto)
    {
        GetPositionsDTOKey getPositionsDTOKey = dto.leaderboardUserDTO.getGetPositionsDTOKey();
        if (getPositionsDTOKey == null)
        {
            Timber.e(new NullPointerException(), "Unable to get positions %s", dto);
            THToast.show(R.string.leaderboard_friends_position_failed);
            return;
        }

        // get leaderboard definition from cache, supposedly it exists coz this view appears after leaderboard definition list
        LeaderboardDefDTO leaderboardDef = null;
        Integer leaderboardId = dto.leaderboardUserDTO.getLeaderboardId();
        if (leaderboardId != null)
        {
            leaderboardDef = leaderboardDefCache
                    .getCachedValue(new LeaderboardDefKey(dto.leaderboardUserDTO.getLeaderboardId()));
        }

        // leaderboard mark user id, to get marking user information
        Bundle bundle = new Bundle();
        TabbedPositionListFragment.putGetPositionsDTOKey(bundle, getPositionsDTOKey);
        TabbedPositionListFragment.putShownUser(bundle, dto.leaderboardUserDTO.getBaseKey());
        if (leaderboardDef != null)
        {
            TabbedPositionListFragment.putLeaderboardTimeRestricted(bundle, leaderboardDef.isTimeRestrictedLeaderboard());
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf =
                new SimpleDateFormat(fragment.getResources().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(dto.leaderboardUserDTO.periodStartUtc);
        TabbedPositionListFragment.putLeaderboardPeriodStartString(bundle, formattedStartPeriodUtc);

        OwnedPortfolioId applicablePortfolioId = fragment.getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            TabbedPositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        if (dto instanceof CompetitionLeaderboardMarkUserItemView.DTO)
        {
            TabbedPositionListFragment.putProviderId(bundle, ((CompetitionLeaderboardMarkUserItemView.DTO) dto).providerDTO.getProviderId());
        }

        navigator.pushFragment(TabbedPositionListFragment.class, bundle);
    }

    protected void handleRulesRequested(@NonNull CompetitionLeaderboardMarkUserItemView.DTO dto)
    {
        Bundle args = new Bundle();
        CompetitionWebViewFragment.putUrl(args, providerUtil.getRulesPage(dto.providerDTO.getProviderId()));
        navigator.pushFragment(CompetitionWebViewFragment.class, args);
    }
}
