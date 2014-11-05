package com.tradehero.th.fragments.social.friend;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.social.key.FriendsListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.social.friend.FriendsListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.DeviceUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public abstract class SocialFriendsFragment extends DashboardFragment
        implements SocialFriendUserView.OnElementClickListener, View.OnClickListener
{
    @InjectView(R.id.friends_root_view) SocialFriendsListView friendsRootView;
    @InjectView(R.id.search_social_friends) EditText searchEdit;
    @Inject FriendsListCacheRx friendsListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    protected SocialFriendHandler socialFriendHandler;
    @Nullable protected MiddleCallback<UserProfileDTO> followFriendsMiddleCallback;
    @Nullable protected MiddleCallback<BaseResponseDTO> inviteFriendsMiddleCallback;

    protected EditText edtMessageInvite;
    protected TextView tvMessageCount;
    protected Button btnMessageCancel;
    protected Button btnMessageComfirm;

    private FriendsListKey friendsListKey;
    protected UserFriendsDTOList friendDTOList;
    protected SocialFriendListItemDTOList listedSocialItems;
    @Nullable private Subscription friendsListCacheSubscription;
    protected SocialFriendsAdapter socialFriendsListAdapter;
    private final int MAX_TEXT_LENGTH = 140;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getTitle());

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_social_friends, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override public void onStart()
    {
        super.onStart();
        unsubscribe(friendsListCacheSubscription);
        friendsListCacheSubscription = AndroidObservable.bindFragment(
                this,
                friendsListCache.get(friendsListKey))
                .subscribe(createFriendsFetchObserver());
    }

    @Override public void onStop()
    {
        unsubscribe(friendsListCacheSubscription);
        friendsListCacheSubscription = null;
        detachFollowFriendsMiddleCallback();
        detachInviteFriendsMiddleCallback();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        this.friendsListCacheSubscription = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        friendsRootView.listView.setOnScrollListener(null);
        super.onDestroyView();
    }

    protected void detachFollowFriendsMiddleCallback()
    {
        MiddleCallback<UserProfileDTO> middleCallbackCopy = followFriendsMiddleCallback;
        if (middleCallbackCopy != null)
        {
            middleCallbackCopy.setPrimaryCallback(null);
        }
    }

    protected void detachInviteFriendsMiddleCallback()
    {
        MiddleCallback<BaseResponseDTO> middleCallbackCopy = inviteFriendsMiddleCallback;
        if (middleCallbackCopy != null)
        {
            middleCallbackCopy.setPrimaryCallback(null);
        }
    }

    @Override
    public void onInviteButtonClick(@NotNull UserFriendsDTO userFriendsDTO)
    {
        List<UserFriendsDTO> usersToInvite = Arrays.asList(userFriendsDTO);
        handleInviteUsers(usersToInvite);
        Timber.d("onInviteButtonClick %s", userFriendsDTO);
    }

    @Override
    public void onFollowButtonClick(@NotNull UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onFollowButtonClick %s", userFriendsDTO);
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userFriendsDTO);
        handleFollowUsers(usersToFollow);
    }

    @Override
    public void onCheckBoxClick(@NotNull UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onCheckBoxClicked " + userFriendsDTO);
        setInviteAllViewCountText(getCountOfCheckBoxInvited());
    }

    public void setInviteAllViewCountText(int count)
    {
        if (count > 0)
        {
            friendsRootView.setInviteAllViewText(getString(R.string.invite) + "(" + count + ")");
        }
        else
        {
            friendsRootView.setInviteAllViewText(getString(R.string.invite));
        }
    }

    protected void handleFollowUsers(List<UserFriendsDTO> usersToFollow)
    {
        createFriendHandler();
        detachFollowFriendsMiddleCallback();
        followFriendsMiddleCallback = socialFriendHandler.followFriends(usersToFollow, new FollowFriendCallback(usersToFollow));
    }

    // TODO subclass like FacebookSocialFriendsFragment should override this methos because the logic of inviting friends is finished on the client side
    protected void handleInviteUsers(List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        detachInviteFriendsMiddleCallback();
        inviteFriendsMiddleCallback =
                socialFriendHandler.inviteFriends(currentUserId.toUserBaseKey(), usersToInvite, createInviteCallback(usersToInvite));
    }

    protected String getWeiboInviteMessage()
    {
        if (edtMessageInvite != null)
        {
            return edtMessageInvite.getText().toString();
        }
        return null;
    }

    protected void setMessageTextLength()
    {
        int length = edtMessageInvite.getText().toString().length();
        tvMessageCount.setText(getString(R.string.weibo_message_text_limit, length));
    }

    protected boolean checkMessageLengthLimit()
    {
        return edtMessageInvite.getText().toString().length() > MAX_TEXT_LENGTH ? false : true;
    }

    protected void addMessageTextListener()
    {
        if (edtMessageInvite != null)
        {
            edtMessageInvite.addTextChangedListener(new TextWatcher()
            {
                @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
                {
                }

                @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
                {
                    setMessageTextLength();
                }

                @Override public void afterTextChanged(Editable editable)
                {
                }
            });
        }
    }

    protected String getStrMessageOfAtList(List<UserFriendsDTO> usersToInvite)
    {
        if (usersToInvite == null)
        {
            return "";
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < usersToInvite.size(); i++)
            {
                sb.append(" @" + usersToInvite.get(i).name);
            }
            return sb.toString();
        }
    }

    protected RequestCallback<BaseResponseDTO> createInviteCallback(List<UserFriendsDTO> usersToInvite)
    {
        return new InviteFriendCallback(usersToInvite);
    }

    protected void createFriendHandler()
    {
        if (socialFriendHandler == null)
        {
            socialFriendHandler = socialFriendHandlerProvider.get();
        }
    }

    @Override
    public void onClick(@NotNull View v)
    {
        if (v.getId() == R.id.social_invite_all)
        {
            inviteAll();
        }
        else if (v.getId() == R.id.social_follow_all)
        {
            FollowAll();
        }
    }

    protected void inviteAll()
    {
        List<UserFriendsDTO> usersUnInvited = findAllUsersUnInvited();
        if (usersUnInvited == null || usersUnInvited.size() == 0)
        {
            THToast.show(R.string.social_no_friend_to_invite);
            return;
        }
        handleInviteUsers(usersUnInvited);
    }

    protected void inviteAllSelected()
    {
        SocialFriendListItemDTOList usersCheckBoxInvited = findAllUsersCheckBoxInvited();
        if (usersCheckBoxInvited == null || usersCheckBoxInvited.size() == 0)
        {
            THToast.show(R.string.social_no_friend_to_invite);
            return;
        }
        handleInviteUsers(usersCheckBoxInvited.getUserFriends());
    }

    private void FollowAll()
    {
        List<UserFriendsDTO> usersUnfollowed = findAllUsersUnfollowed();
        if (usersUnfollowed == null || usersUnfollowed.size() == 0)
        {
            THToast.show(R.string.social_no_friend_to_follow);
            return;
        }
        handleFollowUsers(usersUnfollowed);
    }

    @Nullable private List<UserFriendsDTO> findAllUsersUnfollowed()
    {
        if (friendDTOList != null)
        {
            List<UserFriendsDTO> list = new ArrayList<>();
            for (UserFriendsDTO o : friendDTOList)
            {
                if (o.isTradeHeroUser())
                {
                    list.add(o);
                }
            }
            return list;
        }
        return null;
    }

    @Nullable private List<UserFriendsDTO> findAllUsersUnInvited()
    {
        if (friendDTOList != null)
        {
            List<UserFriendsDTO> list = new ArrayList<>();
            for (UserFriendsDTO o : friendDTOList)
            {
                if (!o.isTradeHeroUser())
                {
                    list.add(o);
                }
            }
            return list;
        }
        return null;
    }

    @Nullable protected SocialFriendListItemDTOList findAllUsersCheckBoxInvited()
    {
        if (listedSocialItems != null)
        {
            SocialFriendListItemDTOList list = new SocialFriendListItemDTOList();
            for (SocialFriendListItemDTO o : listedSocialItems)
            {
                if (o instanceof SocialFriendListItemUserDTO &&
                        ((SocialFriendListItemUserDTO) o).isSelected)
                {
                    list.add(o);
                }
            }
            return list;
        }
        return null;
    }

    protected abstract SocialNetworkEnum getSocialNetwork();

    protected abstract String getTitle();

    private void initView()
    {
        searchEdit.addTextChangedListener(new SearchChangeListener());
        friendsRootView.setFollowAllViewVisible(canFollow());
        friendsRootView.setInviteAllViewVisible(canInviteAll());
        friendsRootView.setFollowOrInivteActionClickListener(this);
        friendsRootView.listView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        displayLoadingView();

        if (friendsListKey == null)
        {
            friendsListKey = new FriendsListKey(currentUserId.toUserBaseKey(), getSocialNetwork());
        }
    }

    private void displayErrorView()
    {
        friendsRootView.showErrorView();
    }

    private void displayLoadingView()
    {
        friendsRootView.showLoadingView();
    }

    private void displayContentView()
    {
        if (friendDTOList != null)
        {
            displayContentView(friendDTOList);
        }
    }

    private void displayContentView(@Nullable UserFriendsDTOList value)
    {
        this.friendDTOList = filterTheDuplicated(value);
        checkUserType();
        if (value == null || value.size() == 0)
        {
            friendsRootView.showEmptyView();
        }
        else
        {
            bindData();
            friendsRootView.showContentView();
        }
    }

    @NotNull private UserFriendsDTOList filterTheDuplicated(UserFriendsDTOList friendDTOList)
    {
        TreeSet<UserFriendsDTO> hashSet = new TreeSet<>();
        hashSet.addAll(friendDTOList);
        UserFriendsDTOList list = new UserFriendsDTOList();
        list.addAll(hashSet);
        return list;
    }

    private void checkUserType()
    {
        int size = friendDTOList.size();
        boolean hasUserToFollow = false;
        boolean hasUserToInvite = false;
        for (int i = 0; i < size; i++)
        {
            if (hasUserToFollow && hasUserToInvite)
            {
                break;
            }
            if (friendDTOList.get(i).isTradeHeroUser())
            {
                hasUserToFollow = true;
            }
            else
            {
                hasUserToInvite = true;
            }
        }
        if (!canFollow() || !hasUserToFollow)
        {
            //friendsRootView.setFollowAllViewEnable(false);
            friendsRootView.setFollowAllViewVisible(false);
        }

        if (!canInviteAll() || !hasUserToInvite)
        {
            //friendsRootView.setInviteAllViewEnable(false);
            friendsRootView.setInviteAllViewVisible(false);
        }
    }

    /**
     * Cannot invite Weibo friends, so hide 'invite all' and remove the one that cannot be invited.
     */
    protected boolean canInvite()
    {
        return true;
    }

    /**
     * Invite all friends of facebook is a bit of complex, so just hide 'invite all'.
     */
    protected boolean canInviteAll()
    {
        if (!canInvite())
        {
            return false;
        }
        return true;
    }

    protected boolean canFollow()
    {
        return true;
    }

    protected int getCountOfUnFollowed()
    {
        List list = findAllUsersUnfollowed();
        if (list != null) return list.size();
        return 0;
    }

    protected int getCountOfUnInvited()
    {
        List list = findAllUsersUnInvited();
        if (list != null) return list.size();
        return 0;
    }

    protected int getCountOfCheckBoxInvited()
    {
        List list = findAllUsersCheckBoxInvited();
        if (list != null) return list.size();
        return 0;
    }

    private void bindData()
    {
        listedSocialItems = new SocialFriendListItemDTOList(friendDTOList, (UserFriendsDTO) null);
        bindNormalData();
    }

    protected void bindNormalData()
    {
        //List<SocialFriendListItemDTO> socialItemsCopy = new ArrayList<>(listedSocialItems);
        socialFriendsListAdapter =
                new SocialFriendsAdapter(
                        getActivity(),
                        listedSocialItems,
                        R.layout.social_friends_item,
                        R.layout.social_friends_item_header);
        socialFriendsListAdapter.setOnElementClickedListener(this);
        friendsRootView.listView.setAdapter(socialFriendsListAdapter);
    }

    private boolean hasView()
    {
        if (isDetached())
        {
            return false;
        }

        return getView() != null;
    }

    private boolean hasListData()
    {
        ListView listView = friendsRootView.listView;
        return listView.getAdapter() != null && listView.getAdapter().getCount() > 0;
    }

    @NotNull protected Observer<Pair<FriendsListKey, UserFriendsDTOList>> createFriendsFetchObserver()
    {
        return new FriendFetchObserver();
    }

    class SearchChangeListener implements TextWatcher
    {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void afterTextChanged(@Nullable Editable s)
        {
            if (socialFriendsListAdapter != null)
            {
                if (s != null && s.toString().trim().length() > 0)
                {
                    socialFriendsListAdapter.getFilter().filter(s.toString());
                }
                else
                {
                    socialFriendsListAdapter.getFilter().filter(s.toString());
                }
            }
        }
    }

    protected void handleInviteSuccess(List<UserFriendsDTO> usersToInvite)
    {
        //Invite Success will not disappear the friend in Invie
        //if (friendDTOList != null && usersToInvite != null)
        //{
        //    for (UserFriendsDTO userFriendsDTO:usersToInvite)
        //    {
        //        boolean removed = friendDTOList.remove(userFriendsDTO);
        //        Timber.d("handleInviteSuccess remove: %s, result: %s",userFriendsDTO,removed);
        //    }
        //}
        //
        //socialFriendsListAdapter.clear();
        //socialFriendsListAdapter.addAll(friendDTOList);
        //// TODO
        THToast.show(R.string.invite_friend_request_sent);
        checkUserType();
    }

    private void handleFollowSuccess(@Nullable List<UserFriendsDTO> usersToFollow)
    {
        if (friendDTOList != null && usersToFollow != null)
        {
            for (UserFriendsDTO userFriendsDTO : usersToFollow)
            {
                boolean removed = friendDTOList.remove(userFriendsDTO);
                Timber.d("handleFollowSuccess remove: %s, result: %s", userFriendsDTO, removed);
            }
        }

        notifyChangeData();
        // TODO
        THToast.show("Follow success");

        checkUserType();
    }

    private void notifyChangeData()
    {
        socialFriendsListAdapter.clear();
        bindData();
    }

    protected void handleFollowError()
    {
        // TODO
        THToast.show(R.string.follow_friend_request_error);
    }

    protected void handleInviteError()
    {
        // TODO
        THToast.show(R.string.invite_friend_request_error);
    }

    class FollowFriendCallback extends RequestCallback<UserProfileDTO>
    {
        final List<UserFriendsDTO> usersToFollow;

        private FollowFriendCallback(List<UserFriendsDTO> usersToFollow)
        {
            super(getActivity());
            this.usersToFollow = usersToFollow;
        }

        @Override
        public void success(@NotNull UserProfileDTO userProfileDTO, @NotNull Response response)
        {
            super.success(userProfileDTO, response);
            if (response.getStatus() == 200 || response.getStatus() == 204)
            {
                // TODO
                handleFollowSuccess(usersToFollow);
                userProfileCache.onNext(userProfileDTO.getBaseKey(), userProfileDTO);
                return;
            }
            handleFollowError();
        }

        @Override
        public void failure(RetrofitError retrofitError)
        {
            super.failure(retrofitError);
            handleFollowError();
        }
    }

    class InviteFriendCallback extends RequestCallback<BaseResponseDTO>
    {
        List<UserFriendsDTO> usersToInvite;

        private InviteFriendCallback(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
        }

        @Override
        public void success(BaseResponseDTO data, @NotNull Response response)
        {
            super.success(data, response);
            if (response.getStatus() == 200 || response.getStatus() == 204)
            {
                handleInviteSuccess(usersToInvite);
                return;
            }
            handleInviteError();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            super.failure(retrofitError);
            handleInviteError();
        }
    }

    class FriendFetchObserver implements Observer<Pair<FriendsListKey, UserFriendsDTOList>>
    {
        @Override public void onNext(Pair<FriendsListKey, UserFriendsDTOList> pair)
        {
            displayContentView(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (hasListData())
            {
                //when already fetch the data,do not show error view
                displayContentView();
            }
            else
            {
                displayErrorView();
            }
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                friendsRootView.inviteFollowAllContainer.setTranslationY(y);
            }
        });
    }

    @Override public void onPause()
    {
        super.onPause();
        dashboardTabHost.get().setOnTranslate(null);
        DeviceUtil.dismissKeyboard(getActivity());
    }
}
