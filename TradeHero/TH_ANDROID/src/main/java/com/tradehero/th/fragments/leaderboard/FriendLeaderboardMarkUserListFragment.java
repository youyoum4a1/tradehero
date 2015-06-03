package com.tradehero.th.fragments.leaderboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import com.android.internal.util.Predicate;
import com.facebook.FacebookOperationCanceledException;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedDTOAdapter;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsKey;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.social.UserFriendsLinkedinDTO;
import com.tradehero.th.api.social.UserFriendsTwitterDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.friend.SocialFriendHandlerFacebook;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.list.SingleExpandingListViewListener;
import java.util.Arrays;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class FriendLeaderboardMarkUserListFragment extends BaseLeaderboardPagedListRxFragment<
        LeaderboardFriendsKey,
        FriendLeaderboardUserDTO,
        FriendLeaderboardUserDTOList,
        ProcessableLeaderboardFriendsDTO>
{
    @Inject Analytics analytics;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardFriendsCacheRx leaderboardFriendsCache;
    @Inject LeaderboardMarkUserListFragmentUtil fragmentUtil;
    @Inject SocialShareHelper socialShareHelper;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject SocialFriendHandlerFacebook socialFriendHandlerFacebook;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fragmentUtil.linkWith(this, LeaderboardType.STOCKS);
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_friends_listview, container, false);
        ButterKnife.inject(this, view);

        if (listView != null)
        {
            listView.setEmptyView(inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false));
        }
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        fragmentUtil.onStart();
        onStopSubscriptions.add(((LeaderboardFriendsSetAdapter) itemViewAdapter).getFollowRequestObservable()
                .subscribe(
                        fragmentUtil,
                        new TimberOnErrorAction("Error on follow requested")));

        onStopSubscriptions.add(((LeaderboardFriendsSetAdapter) itemViewAdapter).getInviteRequestedObservable()
                .flatMap(new Func1<LeaderboardFriendsItemView.UserAction, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(LeaderboardFriendsItemView.UserAction userAction)
                    {
                        return invite(userAction.userFriendsDTO);
                    }
                })
                .retry()
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean success)
                            {
                                if (success)
                                {
                                    THToast.show(R.string.invite_friend_success);
                                }
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to invite friend")));

        onStopSubscriptions.add(((LeaderboardFriendsSetAdapter) itemViewAdapter).getSocialNetworkEnumObservable()
                .flatMap(new Func1<SocialNetworkEnum, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(SocialNetworkEnum socialNetworkEnum)
                    {
                        return socialShareHelper.handleNeedToLink(socialNetworkEnum);
                    }
                })
                .doOnError(new ToastAndLogOnErrorAction("Failed to listen to social network"))
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO userProfileDTO)
                            {
                                setCurrentUserProfileDTO(userProfileDTO);
                                requestDtos();
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to listen to social network")));
        if ((itemViewAdapter != null) && (itemViewAdapter.getCount() == 0))
        {
            requestDtos();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.FriendsLeaderboard_Filter_FoF));
    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        fragmentUtil.onStop();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        itemViewAdapter.clear();
        super.onDestroy();
    }

    @NonNull @Override protected PagedDTOAdapter<FriendLeaderboardUserDTO> createItemViewAdapter()
    {
        return new LeaderboardFriendsSetAdapter(
                getActivity(),
                currentUserId,
                R.layout.lbmu_item_roi_mode,
                R.layout.leaderboard_friends_social_item_view,
                R.layout.leaderboard_friends_call_action);
    }

    @NonNull @Override protected DTOCacheRx<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO> getCache()
    {
        return new ProcessableLeaderboardFriendsCache(
                leaderboardFriendsCache,
                userProfileCache,
                currentUserId,
                ((LeaderboardFriendsSetAdapter) itemViewAdapter).createItemFactory());
    }

    @Override protected void setCurrentUserProfileDTO(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        super.setCurrentUserProfileDTO(currentUserProfileDTO);
        if (itemViewAdapter != null)
        {
            ((LeaderboardFriendsSetAdapter) itemViewAdapter).setCurrentUserProfileDTO(currentUserProfileDTO);
            ((LeaderboardFriendsSetAdapter) itemViewAdapter).notifyDataSetChanged();
        }
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public LeaderboardFriendsKey makePagedDtoKey(int page)
    {
        return new LeaderboardFriendsKey(page);
    }

    @NonNull protected Observable<Boolean> invite(@NonNull UserFriendsDTO userFriendsDTO)
    {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.InviteFriends, userFriendsDTO.getAnalyticsTag()));
        if (userFriendsDTO instanceof UserFriendsLinkedinDTO || userFriendsDTO instanceof UserFriendsTwitterDTO)
        {
            InviteFormUserDTO inviteFriendForm = new InviteFormUserDTO();
            inviteFriendForm.add(userFriendsDTO);
            final ProgressDialog progressDialog = ProgressDialog.show(
                    getActivity(),
                    getString(R.string.loading_loading),
                    getString(R.string.alert_dialog_please_wait),
                    true);
            return userServiceWrapper
                    .inviteFriendsRx(currentUserId.toUserBaseKey(), inviteFriendForm)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                    .map(new Func1<BaseResponseDTO, Boolean>()
                    {
                        @Override public Boolean call(BaseResponseDTO baseResponseDTO)
                        {
                            return true;
                        }
                    });
        }
        else if (userFriendsDTO instanceof UserFriendsFacebookDTO)
        {
            return socialFriendHandlerFacebook
                    .createShareRequestObservable(Arrays.asList((UserFriendsFacebookDTO) userFriendsDTO), null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends Bundle>>()
                    {
                        @Override public Observable<? extends Bundle> call(Throwable e)
                        {
                            if (e instanceof FacebookOperationCanceledException)
                            {
                                THToast.show(R.string.invite_friend_request_cancelled);
                                return Observable.empty();
                            }
                            return Observable.error(e);
                        }
                    })
                    .map(new Func1<Bundle, Boolean>()
                    {
                        @Override public Boolean call(Bundle bundle)
                        {
                            final String requestId = bundle.getString("request");
                            Timber.d("next %s", bundle);
                            if (requestId != null)
                            {
                                THToast.show(R.string.invite_friend_request_sent);
                                return true;
                            }
                            else
                            {
                                THToast.show(R.string.invite_friend_request_cancelled);
                                return false;
                            }
                        }
                    });
        }
        else
        {
            return Observable.empty();
        }
    }

    @Override protected void updateListViewRow(@NonNull UserProfileDTO currentUserProfile, @NonNull final UserBaseKey heroId)
    {
        AdapterViewUtils.updateSingleRowWhere(
                listView,
                FriendLeaderboardMarkedUserDTO.class,
                new Predicate<FriendLeaderboardMarkedUserDTO>()
                {
                    @Override public boolean apply(FriendLeaderboardMarkedUserDTO friendLeaderboardMarkedUserDTO)
                    {
                        return friendLeaderboardMarkedUserDTO.leaderboardUserDTO.getBaseKey().equals(heroId);
                    }
                });
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        super.onItemClick(parent, view, position, id);
        singleExpandingListViewListener.onItemClick(parent, view, position, id);
    }
}