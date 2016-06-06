package com.androidth.general.fragments.leaderboard;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.rx.PairGetFirst;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.adapters.PagedRecyclerAdapter;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import com.androidth.general.api.leaderboard.position.LeaderboardFriendsKey;
import com.androidth.general.api.social.InviteFormUserDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.social.UserFriendsFacebookDTO;
import com.androidth.general.api.social.UserFriendsLinkedinDTO;
import com.androidth.general.api.social.UserFriendsTwitterDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.social.friend.SocialFriendHandlerFacebook;
import com.androidth.general.models.share.SocialShareHelper;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.persistence.leaderboard.position.LeaderboardFriendsCacheRx;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.AlertDialogRx;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.rx.view.DismissDialogAction0;
import com.androidth.general.widget.list.SingleExpandingListViewListener;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class FriendLeaderboardMarkUserRecyclerFragment extends BaseLeaderboardPagedRecyclerRxFragment<
        LeaderboardFriendsKey,
        LeaderboardItemDisplayDTO,
        LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>,
        ProcessableLeaderboardFriendsDTO>
{
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject SingleExpandingListViewListener singleExpandingListViewListener;
    @Inject LeaderboardFriendsCacheRx leaderboardFriendsCache;
    @Inject LeaderboardMarkUserListFragmentUtil fragmentUtil;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject SocialFriendHandlerFacebook socialFriendHandlerFacebook;
    @Inject SocialShareHelper socialShareHelper;
    private ProcessableLeaderboardFriendsCache processableLeaderboardFriendsCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fragmentUtil.linkWith(this, LeaderboardType.STOCKS);
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_mark_user_recyclerview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        fragmentUtil.onStart();
        onStopSubscriptions.add(((FriendsLeaderboardRecyclerAdapter) itemViewAdapter).getUserActionObservable()
                .subscribe(
                        fragmentUtil,
                        new TimberOnErrorAction1("Error on follow requested")));

        onStopSubscriptions.add(((FriendsLeaderboardRecyclerAdapter) itemViewAdapter).getInviteRequestedObservable()
                .flatMap(new Func1<LeaderboardFriendUserAction, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(LeaderboardFriendUserAction userAction)
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
                        new TimberAndToastOnErrorAction1("Failed to invite friend")));

        onStopSubscriptions.add(((FriendsLeaderboardRecyclerAdapter) itemViewAdapter).getSocialNetworkEnumObservable()
                .flatMap(new Func1<SocialNetworkEnum, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(SocialNetworkEnum socialNetworkEnum)
                    {
                        if (socialNetworkEnum.equals(SocialNetworkEnum.FB))
                        {
                            final ProgressDialog progress = ProgressDialog.show(getActivity(),
                                    getString(R.string.loading_loading),
                                    getString(R.string.alert_dialog_please_wait));
                            return socialFriendHandlerFacebook.createProfileSessionObservable()
                                    .map(new PairGetFirst<UserProfileDTO, Session>())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnUnsubscribe(new DismissDialogAction0(progress));
                        }
                        else
                        {
                            return socialShareHelper.handleNeedToLink(socialNetworkEnum);
                        }
                    }
                })
                .doOnError(new TimberAndToastOnErrorAction1("Failed to listen to social network"))
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<UserProfileDTO, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(final UserProfileDTO userProfileDTO)
                    {
                        setCurrentUserProfileDTO(userProfileDTO);
                        requestDtos();
                        return AlertDialogRx.build(getActivity())
                                .setPositiveButton(R.string.ok)
                                .setTitle(R.string.account_already_linked)
                                .setMessage(R.string.friend_list_update_message)
                                .build()
                                .map(new Func1<OnDialogClickEvent, UserProfileDTO>()
                                {
                                    @Override public UserProfileDTO call(OnDialogClickEvent clickEvent)
                                    {
                                        return userProfileDTO;
                                    }
                                });
                    }
                })
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO userProfileDTO)
                            {
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to listen to social network")));
        if ((itemViewAdapter != null) && (itemViewAdapter.getItemCount() == 0))
        {
            requestDtos();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.FriendsLeaderboard_Filter_FoF));
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
        processableLeaderboardFriendsCache = null;
        super.onDestroy();
    }

    @Override public void updateRow(LeaderboardItemDisplayDTO dto)
    {
        int position = itemViewAdapter.indexOf(dto);
        if(position >= 0 )
        {
            itemViewAdapter.notifyItemChanged(position);
        }
    }

    @NonNull @Override protected PagedRecyclerAdapter<LeaderboardItemDisplayDTO> createItemViewAdapter()
    {
        return new FriendsLeaderboardRecyclerAdapter(
                getActivity(),
                R.layout.lbmu_item_roi_mode,
                R.layout.leaderboard_friends_social_item_view,
                R.layout.leaderboard_friends_call_action,
                new LeaderboardKey(leaderboardDefKey.key));
    }

    @NonNull @Override protected DTOCacheRx<LeaderboardFriendsKey, ProcessableLeaderboardFriendsDTO> getCache()
    {
        if (processableLeaderboardFriendsCache == null)
        {
            processableLeaderboardFriendsCache = new ProcessableLeaderboardFriendsCache(
                    getResources(),
                    leaderboardFriendsCache,
                    userProfileCache,
                    currentUserId);
        }
        return processableLeaderboardFriendsCache;
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
        //TODO Change Analytics
        //analytics.addEvent(new MethodEvent(AnalyticsConstants.InviteFriends, userFriendsDTO.getAnalyticsTag()));
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
}