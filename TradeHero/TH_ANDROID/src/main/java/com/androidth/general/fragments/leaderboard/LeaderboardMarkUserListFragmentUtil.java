package com.androidth.general.fragments.leaderboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.def.LeaderboardDefDTO;
import com.androidth.general.api.leaderboard.key.LeaderboardDefKey;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.UserProfileDTOUtil;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.competition.CompetitionWebViewFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.position.TabbedPositionListFragment;
import com.androidth.general.fragments.timeline.MeTimelineFragment;
import com.androidth.general.fragments.timeline.PushableTimelineFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.user.follow.FollowUserAssistant;
import com.androidth.general.persistence.leaderboard.LeaderboardDefCacheRx;
import com.androidth.general.rx.ReplaceWithFunc1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LeaderboardMarkUserListFragmentUtil
        implements Action1<LeaderboardItemUserAction>
{
    @NonNull private final DashboardNavigator navigator;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final LeaderboardDefCacheRx leaderboardDefCache;
    //TODO Change Analytics
    //@NonNull private final Analytics analytics;
    @NonNull private final ProviderUtil providerUtil;

    private BaseLeaderboardPagedRecyclerRxFragment fragment;
    private LeaderboardType leaderboardType;
    private SubscriptionList onStopSubscriptions;

    //<editor-fold desc="Constructors">
    //TODO Change Analytics
    //4th argument was analytics
    @Inject public LeaderboardMarkUserListFragmentUtil(
            @NonNull DashboardNavigator navigator,
            @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardDefCacheRx leaderboardDefCache,
            @NonNull ProviderUtil providerUtil)
    {
        this.navigator = navigator;
        this.currentUserId = currentUserId;
        this.leaderboardDefCache = leaderboardDefCache;
        //TODO Change Analytics
        //this.analytics = analytics;
        this.providerUtil = providerUtil;
    }
    //</editor-fold>

    public void linkWith(
            @NonNull BaseLeaderboardPagedRecyclerRxFragment fragment,
            @NonNull LeaderboardType leaderboardType)
    {
        this.fragment = fragment;
        this.leaderboardType = leaderboardType;
        HierarchyInjector.inject(fragment.getActivity(), this);
    }

    public void onStart()
    {
        onStopSubscriptions = new SubscriptionList();
    }

    public void onStop()
    {
        onStopSubscriptions.unsubscribe();
    }

    public void onDestroy()
    {
        this.fragment = null;
        this.leaderboardType = null;
    }

    @Override public void call(LeaderboardItemUserAction userAction)
    {
        if (userAction.dto instanceof LeaderboardMarkedUserItemDisplayDto)
        {
            switch (userAction.actionType)
            {
                case PROFILE:
                    if(((CompetitionLeaderboardItemDisplayDTO)userAction.dto).providerDTO!=null){
                        ProviderDTO providerDTO = ((CompetitionLeaderboardItemDisplayDTO)userAction.dto).providerDTO;
                        openTimeline((LeaderboardMarkedUserItemDisplayDto) userAction.dto, providerDTO);
                    }else{
                        openTimeline((LeaderboardMarkedUserItemDisplayDto) userAction.dto, null);
                    }

                    break;

                case FOLLOW:
                    UserBaseDTO toFollow = ((LeaderboardMarkedUserItemDisplayDto) userAction.dto).leaderboardUserDTO;
                    if (toFollow != null)
                    {
                        handleFollowRequested(toFollow);
                        LeaderboardMarkedUserItemDisplayDto markedUserItemDisplayDto = (LeaderboardMarkedUserItemDisplayDto) userAction.dto;
                        markedUserItemDisplayDto.setIsFollowing(true);
                        fragment.updateRow(markedUserItemDisplayDto);
                    }
                    else
                    {
                        Timber.e(new NullPointerException(), "ToFollow was null");
                    }
                    break;

                case UNFOLLOW:
                    handleUnfollowRequested(userAction.dto);
                    break;

                case POSITIONS:
                    handlePositionsRequested((LeaderboardMarkedUserItemDisplayDto) userAction.dto);
                    break;

                case RULES:
                    handleRulesRequested((CompetitionLeaderboardItemDisplayDTO) userAction.dto);
                    break;
            }
        }
    }

    private void handleUnfollowRequested(LeaderboardItemDisplayDTO toUnfollow)
    {
        LeaderboardMarkedUserItemDisplayDto markedUser = (LeaderboardMarkedUserItemDisplayDto) toUnfollow;
        UserBaseDTO user = markedUser.leaderboardUserDTO;
        FollowUserAssistant assistant;
        if (user != null)
        {
            assistant = new FollowUserAssistant(fragment.getActivity(), user.getBaseKey());
            onStopSubscriptions.add(
                    assistant.showUnFollowConfirmation(user.displayName)
                            .map(new ReplaceWithFunc1<OnDialogClickEvent, LeaderboardMarkedUserItemDisplayDto>(
                                    markedUser))
                            .doOnNext(new Action1<LeaderboardMarkedUserItemDisplayDto>()
                            {
                                @Override public void call(LeaderboardMarkedUserItemDisplayDto leaderboardMarkedUserItemDisplayDto)
                                {
                                    leaderboardMarkedUserItemDisplayDto.setIsFollowing(false);
                                    fragment.updateRow(leaderboardMarkedUserItemDisplayDto);
                                }
                            })
                            .map(new ReplaceWithFunc1<LeaderboardMarkedUserItemDisplayDto, FollowUserAssistant>(assistant))
                            .doOnNext(new Action1<FollowUserAssistant>()
                            {
                                @Override public void call(FollowUserAssistant followUserAssistant)
                                {
                                    followUserAssistant.unFollowFromCache();
                                }
                            })
                            .observeOn(Schedulers.io())
                            .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                            {
                                @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                                {
                                    return followUserAssistant.unFollowFromServer();
                                }
                            })
                            .subscribe(new Action1<UserProfileDTO>()
                            {
                                @Override public void call(UserProfileDTO userProfileDTO)
                                {
                                    fragment.setCurrentUserProfileDTO(userProfileDTO);
                                }
                            }, new TimberOnErrorAction1("Failed to unfollow hero"))
            );
        }
        else
        {
            Timber.e("Error on unfollow from leaderboard, user is null %s", markedUser);
        }
    }

    protected void openTimeline(@NonNull LeaderboardMarkedUserItemDisplayDto dto, ProviderDTO providerDTO)
    {
        Bundle bundle = new Bundle();
        if(providerDTO!=null){
            bundle.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, providerDTO.hexColor);
            bundle.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, providerDTO.navigationLogoUrl);
        }

        if (dto.leaderboardUserDTO == null)
        {
            navigator.pushFragment(MeTimelineFragment.class, bundle);
            return;
        }
        UserBaseKey userToSee = dto.leaderboardUserDTO.getBaseKey();
        if (currentUserId.toUserBaseKey().equals(userToSee))
        {
            navigator.pushFragment(MeTimelineFragment.class, bundle);
        }
        else
        {
            PushableTimelineFragment.putUserBaseKey(bundle, userToSee);
            navigator.pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    protected void handleFollowRequested(@NonNull final UserBaseDTO userBaseDTO)
    {
        FollowUserAssistant followUserAssistant = new FollowUserAssistant(fragment.getActivity(), userBaseDTO.getBaseKey());
        followUserAssistant.followingInCache();
        onStopSubscriptions.add(
                AppObservable.bindSupportFragment(
                        fragment,
                        followUserAssistant.followingInServer())
                        .subscribe(new Action1<UserProfileDTO>()
                                   {
                                       @Override public void call(UserProfileDTO userProfileDTO)
                                       {
                                           fragment.setCurrentUserProfileDTO(userProfileDTO);
                                           int followType = userProfileDTO.getFollowType(userBaseDTO);
                                           if (followType == UserProfileDTOUtil.IS_FREE_FOLLOWER)
                                           {
                                               //TODO Change Analytics
                                               //analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));
                                           }
                                           //Previous codebase comments 3.3.1
                                           //else if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
                                           //{
                                           //    analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.PremiumFollow_Success,
                                           //            AnalyticsConstants.Leaderboard));
                                           //}
                                       }
                                   },
                                new Action1<Throwable>()
                                {
                                    @Override public void call(Throwable throwable)
                                    {
                                        Timber.e(throwable, "Failed to follow hero");
                                    }
                                })
        );
    }

    protected void handlePositionsRequested(@NonNull LeaderboardMarkedUserItemDisplayDto dto)
    {
        LeaderboardUserDTO userDTO = dto.leaderboardUserDTO;
        if (userDTO == null)
        {
            Timber.e(new NullPointerException(), "LeaderboardUserDTO was null %s", dto);
            THToast.show(R.string.leaderboard_friends_position_failed);
            return;
        }
        GetPositionsDTOKey getPositionsDTOKey = userDTO.getGetPositionsDTOKey();
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
        TabbedPositionListFragment.putIsFX(bundle, leaderboardType.assetClass);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf =
                new SimpleDateFormat(fragment.getResources().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(dto.leaderboardUserDTO.periodStartUtc);
        TabbedPositionListFragment.putLeaderboardPeriodStartString(bundle, formattedStartPeriodUtc);

        OwnedPortfolioId applicablePortfolioId = fragment.getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            TabbedPositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        if (dto instanceof CompetitionLeaderboardItemDisplayDTO)
        {
            TabbedPositionListFragment.putProviderId(bundle,
                    ((CompetitionLeaderboardItemDisplayDTO) dto).providerDTO.getProviderId());
            bundle.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL,
                    ((CompetitionLeaderboardItemDisplayDTO) dto).providerDTO.navigationLogoUrl);
            bundle.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR,
                    ((CompetitionLeaderboardItemDisplayDTO) dto).providerDTO.hexColor);
        }

        navigator.pushFragment(TabbedPositionListFragment.class, bundle);
    }

    protected void handleRulesRequested(@NonNull CompetitionLeaderboardItemDisplayDTO dto)
    {
        Bundle args = new Bundle();
        CompetitionWebViewFragment.putUrl(args, providerUtil.getRulesPage(dto.providerDTO.getProviderId()));
        navigator.pushFragment(CompetitionWebViewFragment.class, args);
    }
}
