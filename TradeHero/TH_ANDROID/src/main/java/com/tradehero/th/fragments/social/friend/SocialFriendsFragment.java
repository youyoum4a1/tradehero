package com.tradehero.th.fragments.social.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
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
import com.tradehero.th.persistence.social.friend.FriendsListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.DeviceUtil;
import dagger.Lazy;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public abstract class SocialFriendsFragment extends DashboardFragment
        implements SocialFriendUserView.OnElementClickListener
{
    @InjectView(R.id.friends_root_view) BetterViewAnimator friendsRootView;
    @InjectView(R.id.search_social_friends) EditText searchEdit;
    @InjectView(R.id.social_follow_invite_all_container) View inviteFollowAllContainer;
    @InjectView(R.id.social_follow_all) TextView followAllView;
    @InjectView(R.id.social_invite_all) TextView inviteAllView;
    @InjectView(R.id.social_friends_list) ListView listView;
    @InjectView(android.R.id.empty) TextView emptyView;

    @Inject FriendsListCacheRx friendsListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    protected SocialFriendHandler socialFriendHandler;
    @Nullable protected Subscription followFriendsSubscription;
    @Nullable protected Subscription inviteFriendsSubscription;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_social_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        listView.setEmptyView(emptyView);
        searchEdit.addTextChangedListener(new SearchChangeListener());
        followAllView.setVisibility(canFollow() ? View.VISIBLE : View.GONE);
        inviteAllView.setVisibility(canInviteAll() ? View.VISIBLE : View.GONE);
        listView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        displayLoadingView();

        if (friendsListKey == null)
        {
            friendsListKey = new FriendsListKey(currentUserId.toUserBaseKey(), getSocialNetwork());
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getTitle());
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override public void onResume()
    {
        super.onResume();
        dashboardTabHost.get().setOnTranslate((x, y) -> inviteFollowAllContainer.setTranslationY(y));
    }

    @Override public void onPause()
    {
        super.onPause();
        dashboardTabHost.get().setOnTranslate(null);
        DeviceUtil.dismissKeyboard(getActivity());
    }

    @Override public void onStop()
    {
        unsubscribe(friendsListCacheSubscription);
        friendsListCacheSubscription = null;
        unsubscribe(followFriendsSubscription);
        followFriendsSubscription = null;
        unsubscribe(inviteFriendsSubscription);
        inviteFriendsSubscription = null;
        super.onStop();
    }

    @Override public void onDestroy()
    {
        this.friendsListCacheSubscription = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        listView.setOnScrollListener(null);
        super.onDestroyView();
    }

    @Override
    public void onInviteButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        List<UserFriendsDTO> usersToInvite = Arrays.asList(userFriendsDTO);
        handleInviteUsers(usersToInvite);
        Timber.d("onInviteButtonClick %s", userFriendsDTO);
    }

    @Override
    public void onFollowButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onFollowButtonClick %s", userFriendsDTO);
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userFriendsDTO);
        handleFollowUsers(usersToFollow);
    }

    @Override
    public void onCheckBoxClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onCheckBoxClicked " + userFriendsDTO);
        setInviteAllViewCountText(getCountOfCheckBoxInvited());
    }

    public void setInviteAllViewCountText(int count)
    {
        if (count > 0)
        {
            inviteAllView.setText(getString(R.string.invite) + "(" + count + ")");
        }
        else
        {
            inviteAllView.setText(R.string.invite);
        }
    }

    protected void handleFollowUsers(List<UserFriendsDTO> usersToFollow)
    {
        createFriendHandler();
        unsubscribe(followFriendsSubscription);
        followFriendsSubscription = socialFriendHandler.followFriends(usersToFollow, new FollowFriendObserver(usersToFollow));
    }

    // TODO subclass like FacebookSocialFriendsFragment should override this method because the logic of inviting friends is finished on the client side
    protected void handleInviteUsers(List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        unsubscribe(inviteFriendsSubscription);
        inviteFriendsSubscription =
                socialFriendHandler.inviteFriends(currentUserId.toUserBaseKey(), usersToInvite, createInviteObserver(usersToInvite));
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

    protected RequestObserver<BaseResponseDTO> createInviteObserver(List<UserFriendsDTO> usersToInvite)
    {
        return new InviteFriendObserver(usersToInvite);
    }

    protected void createFriendHandler()
    {
        if (socialFriendHandler == null)
        {
            socialFriendHandler = socialFriendHandlerProvider.get();
        }
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.social_invite_all)
    protected void inviteAll(View view)
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

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.social_follow_all)
    protected void followAll(View view)
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
            return friendDTOList.getTradeHeroUsers();
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

    private void displayErrorView()
    {
        friendsRootView.setDisplayedChildByLayoutId(R.id.error);
    }

    private void displayLoadingView()
    {
        friendsRootView.setDisplayedChildByLayoutId(android.R.id.progress);
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
            friendsRootView.setDisplayedChildByLayoutId(android.R.id.empty);
        }
        else
        {
            bindData();
            friendsRootView.setDisplayedChildByLayoutId(R.id.content_wrapper);
        }
    }

    @NonNull private UserFriendsDTOList filterTheDuplicated(UserFriendsDTOList friendDTOList)
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
            followAllView.setVisibility(View.GONE);
        }

        if (!canInviteAll() || !hasUserToInvite)
        {
            inviteAllView.setVisibility(View.GONE);
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
        List<UserFriendsDTO> list = findAllUsersUnInvited();
        if (list != null)
        {
            return list.size();
        }
        return 0;
    }

    @Nullable private List<UserFriendsDTO> findAllUsersUnInvited()
    {
        if (friendDTOList != null)
        {
            return friendDTOList.getNonTradeHeroUsers();
        }
        return null;
    }

    protected int getCountOfCheckBoxInvited()
    {
        List list = findAllUsersCheckBoxInvited();
        if (list != null)
        {
            return list.size();
        }
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
        listView.setAdapter(socialFriendsListAdapter);
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
        return listView.getAdapter() != null && listView.getAdapter().getCount() > 0;
    }

    @NonNull protected Observer<Pair<FriendsListKey, UserFriendsDTOList>> createFriendsFetchObserver()
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

    class FollowFriendObserver extends RequestObserver<UserProfileDTO>
    {
        final List<UserFriendsDTO> usersToFollow;

        private FollowFriendObserver(List<UserFriendsDTO> usersToFollow)
        {
            super(getActivity());
            this.usersToFollow = usersToFollow;
        }

        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            super.onNext(userProfileDTO);
            handleFollowSuccess(usersToFollow);
            userProfileCache.onNext(userProfileDTO.getBaseKey(), userProfileDTO);
            return;
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            handleFollowError();
        }
    }

    class InviteFriendObserver extends RequestObserver<BaseResponseDTO>
    {
        List<UserFriendsDTO> usersToInvite;

        private InviteFriendObserver(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
        }

        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            super.onNext(baseResponseDTO);
            handleInviteSuccess(usersToInvite);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
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
}
