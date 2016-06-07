package com.androidth.general.fragments.social.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import com.androidth.general.common.utils.THToast;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.share.wechat.WeChatDTO;
import com.androidth.general.api.share.wechat.WeChatMessageType;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.social.UserFriendsDTOFactory;
import com.androidth.general.api.social.UserFriendsDTOList;
import com.androidth.general.api.social.UserFriendsFacebookDTO;
import com.androidth.general.api.social.UserFriendsLinkedinDTO;
import com.androidth.general.api.social.UserFriendsTwitterDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.UserProfileDTOUtil;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.models.share.SocialShareHelper;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.network.share.SocialSharer;
import com.androidth.general.network.share.dto.SocialShareResult;
import com.androidth.general.persistence.prefs.ShowAskForInviteDialog;
import com.androidth.general.persistence.timing.TimingIntervalPreference;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.EmptyAction1;
import dagger.Lazy;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

@Routable({
        "refer-friends",
        "refer-friend/:socialId/:socialUserId",
})
public class FriendsInvitationFragment extends BaseFragment
        implements SocialFriendUserView.OnElementClickListener
{
    @Bind(R.id.social_friend_type_list) ListView socialListView;
    @Bind(R.id.social_friends_list) ListView friendsListView;
    @Bind(R.id.social_search_friends_progressbar) ProgressBar searchProgressBar;
    @Bind(R.id.social_search_friends_none) TextView friendsListEmptyView;
    @Bind(R.id.search_social_friends) EditText filterTextView;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject CurrentUserId currentUserId;
    SocialFriendHandler socialFriendHandler;
    SocialFriendHandlerFacebook socialFriendHandlerFacebook;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;
    @Inject Provider<SocialFriendHandlerFacebook> facebookSocialFriendHandlerProvider;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject SocialShareHelper socialShareHelper;

    @NonNull private UserFriendsDTOList userFriendsDTOs = new UserFriendsDTOList();
    private SocialFriendListItemDTOList socialFriendListItemDTOs;
    private Runnable searchTask;

    private static final String KEY_BUNDLE = "key_bundle";
    private static final String KEY_LIST_TYPE = "key_list_type";
    private static final int LIST_TYPE_SOCIAL_LIST = 1;
    private static final int LIST_TYPE_FRIEND_LIST = 2;

    public static final String ROUTER_SOCIAL_ID = "socialId";
    public static final String ROUTER_SOCIAL_USER_ID = "socialUserId";

    @RouteProperty(ROUTER_SOCIAL_ID) String socialId;
    @RouteProperty(ROUTER_SOCIAL_USER_ID) String socialUserId;

    private Bundle savedState;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        socialFriendHandler = socialFriendHandlerProvider.get();
        socialFriendHandlerFacebook = facebookSocialFriendHandlerProvider.get();
        mShowAskForInviteDialogPreference.pushInFuture(TimingIntervalPreference.YEAR);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(getString(R.string.action_invite));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_invite_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        restoreSavedData(savedInstanceState);
    }

    @Override public void onResume()
    {
        super.onResume();
        if (socialId != null && socialUserId != null)
        {
            createInviteInHomePage();
        }
    }

    @Override public void onPause()
    {
        resetRoutingData();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putBundle(KEY_BUNDLE, savedState != null ? savedState : saveState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView()
    {
        savedState = saveState();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void restoreSavedData(Bundle savedInstanceState)
    {
        if (savedInstanceState != null && savedState == null)
        {
            savedState = savedInstanceState.getBundle(KEY_BUNDLE);
        }
        int listType = LIST_TYPE_SOCIAL_LIST;
        if (savedState != null)
        {
            listType = savedState.getInt(KEY_LIST_TYPE);
        }
        savedState = null;
        if (listType == LIST_TYPE_SOCIAL_LIST)
        {
            bindSocialTypeData();
        }
        else
        {
            bindSearchData();
        }
    }

    private Bundle saveState()
    {
        Bundle state = new Bundle();
        if (friendsListView != null)
        {
            state.putInt(KEY_LIST_TYPE, (friendsListView.getVisibility() == View.VISIBLE) ? LIST_TYPE_FRIEND_LIST : LIST_TYPE_SOCIAL_LIST);
        }
        return state;
    }

    private void resetRoutingData()
    {
        // TODO Routing library should have a way to clear injected data, proposing: THRouter.reset(this)
        Bundle args = getArguments();
        if (args != null)
        {
            args.remove(ROUTER_SOCIAL_ID);
            args.remove(ROUTER_SOCIAL_USER_ID);
        }
        socialId = null;
        socialUserId = null;
    }

    private void bindSocialTypeData()
    {
        List<SocialTypeItem> socialTypeItemList = SocialTypeItemFactory.getSocialTypeList();
        SocialTypeListAdapter adapter = new SocialTypeListAdapter(getActivity(), 0, socialTypeItemList);
        socialListView.setAdapter(adapter);
        showSocialTypeList();
    }

    private void bindSearchData()
    {
        socialFriendListItemDTOs = new SocialFriendListItemDTOList(userFriendsDTOs, null);
        if (friendsListView.getAdapter() == null)
        {
            SocialFriendsAdapter socialFriendsListAdapter =
                    new SocialFriendsAdapter(
                            getActivity(),
                            socialFriendListItemDTOs,
                            R.layout.social_friends_item,
                            R.layout.social_friends_item_header);
            socialFriendsListAdapter.setOnElementClickedListener(this);
            friendsListView.setAdapter(socialFriendsListAdapter);
            friendsListView.setEmptyView(friendsListEmptyView);
        }
        else
        {
            SocialFriendsAdapter socialFriendsListAdapter = (SocialFriendsAdapter) friendsListView.getAdapter();
            socialFriendsListAdapter.clear();
            socialFriendsListAdapter.addAll(socialFriendListItemDTOs);
        }
        showSearchList();
    }

    @OnItemClick(R.id.social_friend_type_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        final SocialTypeItem item = (SocialTypeItem) parent.getItemAtPosition(position);
        if (item.socialNetwork == SocialNetworkEnum.WECHAT)
        {
            inviteWeChatFriends();
            return;
        }

        boolean linked = checkLinkedStatus(item.socialNetwork);
        if (linked)
        {
            pushSocialInvitationFragment(item.socialNetwork);
        }
        else
        {
            onStopSubscriptions.add(socialShareHelper.offerToConnect(item.socialNetwork)
                    .subscribe(
                            new Action1<UserProfileDTO>()
                            {
                                @Override public void call(UserProfileDTO userProfileDTO)
                                {
                                    pushSocialInvitationFragment(item.socialNetwork);
                                }
                            },
                            new EmptyAction1<Throwable>()));
        }
    }

    private void inviteWeChatFriends()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            WeChatDTO weChatDTO = new WeChatDTO();
            weChatDTO.id = 0;
            weChatDTO.type = WeChatMessageType.Invite;
            weChatDTO.title = getString(WeChatMessageType.Invite.getTitleResId(), userProfileDTO.referralCode);
            socialSharerLazy.get().share(weChatDTO)
                    .subscribe(
                            new EmptyAction1<SocialShareResult>(),
                            new EmptyAction1<Throwable>()); // TODO proper callback?
        }
    }

    private void cancelPendingSearchTask()
    {
        View view = getView();
        if (view != null && searchTask != null)
        {
            view.removeCallbacks(searchTask);
        }
    }

    private void showSearchList()
    {
        socialListView.setVisibility(View.GONE);
        friendsListView.setVisibility(View.VISIBLE);
        searchProgressBar.setVisibility(View.GONE);
        //friendsListEmptyView.setVisibility(View.GONE);
    }

    private void pushSocialInvitationFragment(SocialNetworkEnum socialNetwork)
    {
        Class<? extends SocialFriendsFragment> target = SocialNetworkFactory.findProperTargetFragment(socialNetwork);
        Bundle bundle = new Bundle();
        if (navigator != null)
        {
            navigator.get().pushFragment(target, bundle);
        }
    }

    private boolean checkLinkedStatus(SocialNetworkEnum socialNetwork)
    {
        UserProfileDTO updatedUserProfileDTO =
                userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());
        return updatedUserProfileDTO != null &&
                UserProfileDTOUtil.checkLinkedStatus(updatedUserProfileDTO, socialNetwork);
    }

    @Override
    public void onFollowButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        handleFollowUsers(userFriendsDTO);
    }

    @Override
    public void onInviteButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        handleInviteUsers(userFriendsDTO);
    }

    public void onCheckBoxClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onCheckBoxClicked " + userFriendsDTO);
    }

    protected void handleFollowUsers(UserFriendsDTO userToFollow)
    {
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userToFollow);
        RequestObserver<UserProfileDTO> observer = new FollowFriendObserver(usersToFollow);
        observer.onRequestStart();
        socialFriendHandler.followFriends(usersToFollow)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void createInviteInHomePage()
    {
        handleInviteUsers(UserFriendsDTOFactory.createFrom(socialId, socialUserId));
    }

    // TODO via which social network to invite user?
    protected void handleInviteUsers(UserFriendsDTO userToInvite)
    {
        List<UserFriendsDTO> usersToInvite = Arrays.asList(userToInvite);
        RequestObserver<BaseResponseDTO> observer = new InviteFriendObserver(usersToInvite);
        if (userToInvite instanceof UserFriendsLinkedinDTO || userToInvite instanceof UserFriendsTwitterDTO)
        {
            observer.onRequestStart();
            socialFriendHandler.inviteFriends(
                    currentUserId.toUserBaseKey(),
                    usersToInvite)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        else if (userToInvite instanceof UserFriendsFacebookDTO)
        {
            //TODO do invite on the client side.
            observer.onRequestStart();
            socialFriendHandlerFacebook.inviteFriends(
                    currentUserId.toUserBaseKey(),
                    usersToInvite)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        else
        {
            //if all ids are empty or only wbId is not empty, how to do?
        }
    }

    private void handleInviteSuccess(List<UserFriendsDTO> usersToInvite)
    {
        //Invite Success will not disappear the friend in Invite
        THToast.show(R.string.invite_friend_request_sent);
    }

    private void handleFollowSuccess(List<UserFriendsDTO> usersToFollow)
    {
        if (usersToFollow != null)
        {
            for (UserFriendsDTO userFriendsDTO : usersToFollow)
            {
                userFriendsDTOs.remove(userFriendsDTO);
            }
        }
        SocialFriendsAdapter socialFriendsAdapter = (SocialFriendsAdapter) friendsListView.getAdapter();

        socialFriendListItemDTOs = new SocialFriendListItemDTOList(userFriendsDTOs, null);

        socialFriendsAdapter.clear();
        socialFriendsAdapter.addAll(socialFriendListItemDTOs);
    }

    class FollowFriendObserver extends RequestObserver<UserProfileDTO>
    {
        final List<UserFriendsDTO> usersToFollow;

        //<editor-fold desc="Constructors">
        private FollowFriendObserver(List<UserFriendsDTO> usersToFollow)
        {
            super(getActivity());
            this.usersToFollow = usersToFollow;
        }
        //</editor-fold>

        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            super.onNext(userProfileDTO);
            handleFollowSuccess(usersToFollow);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            THToast.show(R.string.follow_friend_request_error);
        }
    }

    class InviteFriendObserver extends RequestObserver<BaseResponseDTO>
    {
        final List<UserFriendsDTO> usersToInvite;

        //<editor-fold desc="Constructors">
        private InviteFriendObserver(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
        }
        //</editor-fold>

        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            super.onNext(baseResponseDTO);
            handleInviteSuccess(usersToInvite);
        }

        @Override
        public void success()
        {
            handleInviteSuccess(usersToInvite);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            // TODO
            THToast.show(R.string.invite_friend_request_error);
        }
    }

    @NonNull private UserFriendsDTOList filterTheDuplicated(@NonNull List<UserFriendsDTO> friendDTOList)
    {
        TreeSet<UserFriendsDTO> hashSet = new TreeSet<>();
        hashSet.addAll(friendDTOList);
        return new UserFriendsDTOList(hashSet);
    }

    class SearchFriendsObserver implements Observer<UserFriendsDTOList>
    {
        @Override public void onNext(UserFriendsDTOList args)
        {
            FriendsInvitationFragment.this.userFriendsDTOs = filterTheDuplicated(userFriendsDTOs);
            bindSearchData();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "SearchFriendsCallback error");
            // TODO need to tell user.
            showSocialTypeList();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnTextChanged(value = R.id.search_social_friends,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable editable)
    {
        cancelPendingSearchTask();
        if (editable != null && editable.toString().trim().length() > 0)
        {
            String query = editable.toString().trim();
            scheduleSearch(query);
        }
        else
        {
            showSocialTypeList();
        }
    }

    private void scheduleSearch(final String query)
    {
        View view = getView();
        if (view != null)
        {
            if (searchTask != null)
            {
                view.removeCallbacks(searchTask);
            }
            searchTask = new Runnable()
            {
                @Override public void run()
                {
                    if (getView() != null)
                    {
                        showSocialTypeListWithProgress();
                        searchSocialFriends(query);
                    }
                }
            };
            view.postDelayed(searchTask, 500L);
        }
    }

    private void showSocialTypeListWithProgress()
    {
        socialListView.setVisibility(View.VISIBLE);
        friendsListView.setVisibility(View.GONE);
        searchProgressBar.setVisibility(View.VISIBLE);
        friendsListEmptyView.setVisibility(View.GONE);
    }

    private void searchSocialFriends(String query)
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userServiceWrapper.searchSocialFriendsRx(
                        currentUserId.toUserBaseKey(), null, query))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SearchFriendsObserver()));
    }

    private void showSocialTypeList()
    {
        socialListView.setVisibility(View.VISIBLE);
        friendsListView.setVisibility(View.GONE);
        searchProgressBar.setVisibility(View.GONE);
        friendsListEmptyView.setVisibility(View.GONE);
    }
}