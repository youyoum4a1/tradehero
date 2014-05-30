package com.tradehero.th.fragments.social.friend;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by wangliang on 14-5-26.
 */
public abstract class SocialFriendsFragment extends DashboardFragment implements SocialFriendItemView.OnElementClickListener, View.OnClickListener {

    @InjectView(R.id.friends_root_view) SocialFriendsListView friendsRootView;
    @InjectView(R.id.search_social_friends)EditText searchEdit;
    @Inject FriendsListCache friendsListCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

    protected SocialFriendHandler socialFriendHandler;

    private FriendsListKey friendsListKey;
    private FriendDTOList friendDTOList;
    private SocialFriendsAdapter socialFriendsListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(getTitle());

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_social_friends, container, false);
        ButterKnife.inject(this,v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onInviteButtonClick(UserFriendsDTO userFriendsDTO) {
        List<UserFriendsDTO> usersToInvite = Arrays.asList(userFriendsDTO);
        handleInviteUsers(usersToInvite);
        Timber.d("onInviteButtonClick %s", userFriendsDTO);
    }

    @Override
    public void onFollowButtonClick(UserFriendsDTO userFriendsDTO) {
        Timber.d("onFollowButtonClick %s",userFriendsDTO);
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userFriendsDTO);
        handleFollowUsers(usersToFollow);
    }

    protected void handleFollowUsers(List<UserFriendsDTO> usersToFollow)
    {
        createFriendHandler();
        socialFriendHandler.followFriends(usersToFollow,new FollowFriendCallback(usersToFollow));
    }

    // TODO subclass like FaccbookSocialFriendsFragment should override this methos because the logic of inviting friends is finished on the client side
    protected void handleInviteUsers(List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        socialFriendHandler.inviteFriends(currentUserId.toUserBaseKey(), usersToInvite, createInviteCallback(usersToInvite));
    }

    protected SocialFriendHandler.RequestCallback createInviteCallback(List<UserFriendsDTO> usersToInvite)
    {
        return new InviteFriendCallback(usersToInvite);
    }

    protected void createFriendHandler()
    {
        if (socialFriendHandler == null)
        {
            socialFriendHandler = new SocialFriendHandler();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.social_invite_all)
        {
            List<UserFriendsDTO> usersUnInvited = findAllUsersUnInvited();
            if (usersUnInvited == null || usersUnInvited.size() == 0)
            {
                THToast.show("No user to Invite");
                return;
            }
            handleInviteUsers(usersUnInvited);
        }
        else if (v.getId() == R.id.social_follow_all)
        {
            List<UserFriendsDTO> usersUnfollowed = findAllUsersUnfollowed();
            if (usersUnfollowed == null || usersUnfollowed.size() == 0)
            {
                THToast.show("No user to follow");
                return;
            }
            handleFollowUsers(usersUnfollowed);
        }
    }

    private List<UserFriendsDTO> findAllUsersUnfollowed()
    {
        if (friendDTOList != null)
        {
            List<UserFriendsDTO> list = new ArrayList<>();
            for (UserFriendsDTO o:friendDTOList)
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

    private List<UserFriendsDTO> findAllUsersUnInvited()
    {
        if (friendDTOList != null)
        {
            List<UserFriendsDTO> list = new ArrayList<>();
            for (UserFriendsDTO o:friendDTOList)
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

    protected abstract SocialNetworkEnum getSocialNetwork();

    protected abstract String getTitle();

    private void initView()
    {
        searchEdit.addTextChangedListener(new SearchChangeListener());
        friendsRootView.setFollowAllViewVisible(canFollow());
        friendsRootView.setInviteAllViewVisible(canInviteAll());
        friendsRootView.setFollowOrInivteActionClickListener(this);
        displayLoadingView();

        if (friendsListKey == null)
        {
            friendsListKey = new FriendsListKey(currentUserId.toUserBaseKey(),getSocialNetwork());
        }
        DTOCache.GetOrFetchTask fetchTask = friendsListCache.getOrFetch(friendsListKey,true,createFriendsFetchListener());
        fetchTask.execute();
        //fetchTask.getStatus();
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

    private void displayContentView(FriendDTOList value)
    {

        this.friendDTOList = filterTheDublicated(value);
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

    private FriendDTOList filterTheDublicated(FriendDTOList friendDTOList)
    {
        TreeSet<UserFriendsDTO> hashSet = new TreeSet<>();
        hashSet.addAll(friendDTOList);
        FriendDTOList list = new FriendDTOList();
        list.addAll(hashSet);
        return list;
    }

    private void checkUserType()
    {
        int size = friendDTOList.size();
        boolean hasUserToFollow = false;
        boolean hasUserToInvite = false;
        for (int i=0;i<size;i++)
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
            friendsRootView.setFollowAllViewEnable(false);
        }

        if (!canInviteAll() || !hasUserToInvite)
        {
            friendsRootView.setInviteAllViewEnable(false);
        }
    }


    /**
     * Cannot invite Weibo friends, so hide 'invite all' and remove the one that cannot be invited.
     * @return
     */
    protected boolean canInvite()
    {
        return true;
    }

    /**
     * Invite all friends of facebook is a bit of complex, so just hide 'invite all'.
     * @return
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

    private void bindData()
    {
        List friendsDTOsCopy = new ArrayList<>(friendDTOList);
        socialFriendsListAdapter =
                new SocialFriendsAdapter(
                        getActivity(),
                        friendsDTOsCopy,
                        R.layout.social_friends_item);
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

    protected DTOCache.Listener<FriendsListKey, FriendDTOList> createFriendsFetchListener()
    {
        return new FriendFetchListener();
    }

    class SearchChangeListener implements TextWatcher {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (socialFriendsListAdapter != null )
                {
                    if (s != null && s.toString().trim().length() > 0) {
                        socialFriendsListAdapter.getFilter().filter(s.toString());
                    } else {
                        socialFriendsListAdapter.getFilter().filter(s.toString());
                    }
                }
            }

    }

    protected void handleInviteSuccess(List<UserFriendsDTO> usersToInvite)
    {
        if (friendDTOList != null && usersToInvite != null)
        {
            for (UserFriendsDTO userFriendsDTO:usersToInvite)
            {
                boolean removed = friendDTOList.remove(userFriendsDTO);
                Timber.d("handleInviteSuccess remove: %s, result: %s",userFriendsDTO,removed);
            }
        }

        socialFriendsListAdapter.clear();
        socialFriendsListAdapter.addAll(friendDTOList);
        // TODO
        THToast.show(R.string.invite_friend_request_sent);

        checkUserType();
    }

    private void handleFollowSuccess(List<UserFriendsDTO> usersToFollow)
    {
        if (friendDTOList != null && usersToFollow != null)
        {
            for (UserFriendsDTO userFriendsDTO:usersToFollow)
            {
                boolean removed = friendDTOList.remove(userFriendsDTO);
                Timber.d("handleFollowSuccess remove: %s, result: %s",userFriendsDTO,removed);
            }

        }
        socialFriendsListAdapter.clear();
        socialFriendsListAdapter.addAll(friendDTOList);
        // TODO
        THToast.show("Follow success");

        checkUserType();
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

    class FollowFriendCallback extends SocialFriendHandler.RequestCallback<UserProfileDTO> {

        List<UserFriendsDTO> usersToFollow;


        private FollowFriendCallback(List<UserFriendsDTO> usersToFollow)
        {
            super(getActivity());
            this.usersToFollow = usersToFollow;
        }

        @Override
        public void success(UserProfileDTO userProfileDTO, Response response) {
            super.success(userProfileDTO,response);
            if (response.getStatus() != 200)
            {
                // TODO
                handleFollowError();
                return;
            }
            handleFollowSuccess(usersToFollow);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            super.failure(retrofitError);
            handleFollowError();

        }
    };

    class InviteFriendCallback extends SocialFriendHandler.RequestCallback<Response> {

        List<UserFriendsDTO> usersToInvite;


        private InviteFriendCallback(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
        }

        @Override
        public void success(Response data, Response response) {
            super.success(data,response);
            if (response.getStatus() != 200)
            {
                handleInviteError();
                return;
            }
            handleInviteSuccess(usersToInvite);

        }

        @Override
        public void failure(RetrofitError retrofitError) {
            super.failure(retrofitError);
            handleInviteError();
        }
    };

    class FriendFetchListener implements DTOCache.Listener<FriendsListKey, FriendDTOList>
    {
        @Override
        public void onDTOReceived(FriendsListKey key, FriendDTOList value, boolean fromCache)
        {
            if (!hasView())
            {
                return;
            }
            displayContentView(value);
            Timber.d("onDTOReceived key:%s,FriendsListKey:%s,fromCache:%b", key, value,
                    fromCache);
        }

        @Override public void onErrorThrown(FriendsListKey key, Throwable error)
        {

            if (!hasView())
            {
                return;
            }
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

    @Override
    public boolean isTabBarVisible() {
        return false;
    }
}
