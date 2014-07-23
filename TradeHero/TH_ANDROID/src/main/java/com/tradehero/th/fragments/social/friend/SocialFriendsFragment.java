package com.tradehero.th.fragments.social.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.social.UserFriendsWeiboDTO;
import com.tradehero.th.api.social.key.FriendsListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.social.friend.FriendsListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
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
import timber.log.Timber;

public abstract class SocialFriendsFragment extends DashboardFragment
        implements SocialFriendItemView.OnElementClickListener, View.OnClickListener
{
    @InjectView(R.id.friends_root_view) SocialFriendsListView friendsRootView;
    @InjectView(R.id.search_social_friends) EditText searchEdit;
    @Inject FriendsListCache friendsListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;

    protected SocialFriendHandler socialFriendHandler;
    private EditText edtMessageInvite;
    private TextView tvMessageCount;
    private Button btnMessageCancel;
    private Button btnMessageComfirm;
    private AlertDialog mWeiboInviteDialog;

    private FriendsListKey friendsListKey;
    private UserFriendsDTOList friendDTOList;
    @Nullable private DTOCacheNew.Listener<FriendsListKey, UserFriendsDTOList> friendsListCacheListener;
    private SocialFriendsAdapter socialFriendsListAdapter;
    private final int MAX_TEXT_LENGTH = 140;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.friendsListCacheListener = createFriendsFetchListener();
    }

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

    @Override public void onStop()
    {
        detachFriendsListCache();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        this.friendsListCacheListener = null;
        super.onDestroy();
    }

    protected void detachFriendsListCache()
    {
        friendsListCache.unregister(friendsListCacheListener);
    }

    @Override
    public void onInviteButtonClick(UserFriendsDTO userFriendsDTO)
    {
        List<UserFriendsDTO> usersToInvite = Arrays.asList(userFriendsDTO);
        handleInviteUsers(usersToInvite);
        Timber.d("onInviteButtonClick %s", userFriendsDTO);
    }

    @Override
    public void onFollowButtonClick(UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onFollowButtonClick %s", userFriendsDTO);
        List<UserFriendsDTO> usersToFollow = Arrays.asList(userFriendsDTO);
        handleFollowUsers(usersToFollow);
    }

    @Override
    public void onCheckBoxClick(UserFriendsDTO userFriendsDTO)
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
        socialFriendHandler.followFriends(usersToFollow, new FollowFriendCallback(usersToFollow));
    }

    // TODO subclass like FaccbookSocialFriendsFragment should override this methos because the logic of inviting friends is finished on the client side
    protected void handleInviteUsers(List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        socialFriendHandler.inviteFriends(currentUserId.toUserBaseKey(), usersToInvite, createInviteCallback(usersToInvite));
    }

    protected void handleWeiboInviteUsers(String msg , List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        socialFriendHandler.inviteWeiboFriends(msg, currentUserId.toUserBaseKey(), usersToInvite, createInviteCallback(usersToInvite));
    }

    protected void handleInviteCheckBoxUsers(List<UserFriendsDTO> usersToInvite)
    {
        //open a dialog for weibo invite message input
        Timber.d("Invite from weibo message input ...");
        showWeiboInviteDialog(usersToInvite);
    }

    private void showWeiboInviteDialog(final List<UserFriendsDTO> usersToInvite)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.weibo_friends_invite_dialog, null);
        edtMessageInvite = (EditText) view.findViewById(R.id.edtInviteMessage);
        tvMessageCount = (TextView) view.findViewById(R.id.tvMessageCount);
        btnMessageCancel = (Button) view.findViewById(R.id.btnCancle);
        btnMessageComfirm = (Button) view.findViewById(R.id.btnComfirm);

        btnMessageCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                Timber.d("WeiboInviteDialog Canceled !");
                mWeiboInviteDialog.dismiss();
            }
        });

        btnMessageComfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                Timber.d("WeiboInviteDialog Comfirmed");
                if(checkMessageLengthLimit())
                {
                    dissmissWeiboInviteDialog();
                    InviteWeiboFriends(getWeiboInviteMessage(),usersToInvite);
                }
                else
                {
                    THToast.show(R.string.weibo_message_length_error);
                }
            }
        });

        edtMessageInvite.setText(getString(R.string.weibo_friends_invite) + getStrMessageOfAtList(usersToInvite));
        setMessageTextLength();
        builder.setView(view);
        builder.setCancelable(true);
        addMessageTextListener();
        mWeiboInviteDialog = builder.create();
        mWeiboInviteDialog.show();
    } 
    private void dissmissWeiboInviteDialog()
    {
        if(mWeiboInviteDialog!=null)
        {
            mWeiboInviteDialog.dismiss();
        }
    }

    private void InviteWeiboFriends(String msg,List<UserFriendsDTO> usersToInvite)
    {
        List<UserFriendsDTO> usersUnInvited = usersToInvite;
        if (usersUnInvited == null || usersUnInvited.size() == 0)
        {
            THToast.show(R.string.social_no_friend_to_invite);
            return;
        }
        handleWeiboInviteUsers(msg, usersUnInvited);
    }

    private String getWeiboInviteMessage()
    {
        if(edtMessageInvite!=null)
        {
            return edtMessageInvite.getText().toString();
        }
        return null;
    }

    private void setMessageTextLength()
    {
        int length = edtMessageInvite.getText().toString().length();
        tvMessageCount.setText(getString(R.string.weibo_message_text_limit,length));
    }

    private boolean checkMessageLengthLimit()
    {
        return edtMessageInvite.getText().toString().length()>140?false:true;
    }

    private void addMessageTextListener()
    {
        if(edtMessageInvite!=null)
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

    private String getStrMessageOfAtList(List<UserFriendsDTO> usersToInvite)
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

    protected SocialFriendHandler.RequestCallback createInviteCallback(List<UserFriendsDTO> usersToInvite)
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
            InviteAll();
        }
        else if (v.getId() == R.id.social_follow_all)
        {
            FollowAll();
        }
    }

    private void InviteAll()
    {
        if (getSocialNetwork() == SocialNetworkEnum.WB)
        {
            List<UserFriendsDTO> usersCheckBoxInvited = findAllUsersCheckBoxInvited();
            if (usersCheckBoxInvited == null || usersCheckBoxInvited.size() == 0)
            {
                THToast.show(R.string.social_no_friend_to_invite);
                return;
            }
            handleInviteCheckBoxUsers(usersCheckBoxInvited);
        }
        else
        {
            List<UserFriendsDTO> usersUnInvited = findAllUsersUnInvited();
            if (usersUnInvited == null || usersUnInvited.size() == 0)
            {
                THToast.show(R.string.social_no_friend_to_invite);
                return;
            }
            handleInviteUsers(usersUnInvited);
        }
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

    @Nullable private List<UserFriendsDTO> findAllUsersCheckBoxInvited()
    {
        if (friendDTOList != null)
        {
            List<UserFriendsDTO> list = new ArrayList<>();
            for (UserFriendsDTO o : friendDTOList)
            {
                if (o.isInviteChecked)
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
            friendsListKey = new FriendsListKey(currentUserId.toUserBaseKey(), getSocialNetwork());
        }
        detachFriendsListCache();
        friendsListCache.register(friendsListKey, friendsListCacheListener);
        friendsListCache.getOrFetchAsync(friendsListKey);
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
        if (getSocialNetwork() == SocialNetworkEnum.WB)
        {
            bindWeiboData();
        }
        else
        {
            bindNormalData();
        }
    }

    private void bindWeiboData()
    {
        //List<UserFriendsDTO> friendsDTOsCopy = new ArrayList<>(friendDTOList);
        List<UserFriendsDTO> friendsDTOsCopy = friendDTOList;

        int countOfUnFollowed = getCountOfUnFollowed();

        int countOfUnInvited = getCountOfUnInvited();

        if (countOfUnInvited != 0)
        {
            friendsDTOsCopy.add(countOfUnFollowed, new UserFriendsWeiboDTO(true, getString(R.string.friends_can_be_invite, countOfUnInvited)));
        }

        if (countOfUnFollowed != 0)
        {
            friendsDTOsCopy.add(0, new UserFriendsWeiboDTO(true, getString(R.string.friends_can_be_follow, countOfUnFollowed)));
        }

        socialFriendsListAdapter =
                new SocialFriendsAdapter(
                        getActivity(),
                        friendsDTOsCopy,
                        R.layout.social_friends_item);
        socialFriendsListAdapter.setOnElementClickedListener(this);
        friendsRootView.listView.setAdapter(socialFriendsListAdapter);
        friendsRootView.setInviteAllViewText(getString(R.string.invite));
    }

    private void bindNormalData()
    {
        List<UserFriendsDTO> friendsDTOsCopy = new ArrayList<>(friendDTOList);
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

    @NotNull protected DTOCacheNew.Listener<FriendsListKey, UserFriendsDTOList> createFriendsFetchListener()
    {
        return new FriendFetchListener();
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
        dissmissWeiboInviteDialog();
        clearWeiboInviteStatus();
        checkUserType();
    }

    private void clearWeiboInviteStatus()
    {
        if(friendDTOList!=null)
        {
            for(UserFriendsDTO userdto:friendDTOList)
            {
                userdto.isInviteChecked = false;
            }
            if(socialFriendsListAdapter!=null)
            {
                socialFriendsListAdapter.notifyDataSetChanged();
            }
        }
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
        if(getSocialNetwork() == SocialNetworkEnum.WB)
        {
            if(friendDTOList!=null)
            {
                for(int i=0;i<friendDTOList.size();i++)
                {
                    UserFriendsDTO user = friendDTOList.get(i);
                    if(user.isTypeHead)
                    {
                        friendDTOList.remove(user);
                        --i;
                    }
                }
            }
            bindData();
        }
        else
        {
            socialFriendsListAdapter.clear();
            socialFriendsListAdapter.addAll(friendDTOList);
        }
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

    class FollowFriendCallback extends SocialFriendHandler.RequestCallback<UserProfileDTO>
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
                userProfileCache.put(userProfileDTO.getBaseKey(), userProfileDTO);
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

    class InviteFriendCallback extends SocialFriendHandler.RequestCallback<Response>
    {
        List<UserFriendsDTO> usersToInvite;

        private InviteFriendCallback(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
        }

        @Override
        public void success(Response data, @NotNull Response response)
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

    class FriendFetchListener implements DTOCacheNew.HurriedListener<FriendsListKey, UserFriendsDTOList>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull FriendsListKey key,
                @NotNull UserFriendsDTOList value)
        {
            onDTOReceived(key, value);
        }

        @Override public void onDTOReceived(
                @NotNull FriendsListKey key,
                @NotNull UserFriendsDTOList value)
        {
            if (!hasView())
            {
                return;
            }
            displayContentView(value);
        }

        @Override public void onErrorThrown(
                @NotNull FriendsListKey key,
                @NotNull Throwable error)
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

    @Override public void onPause()
    {
        super.onPause();
        // TODO test for nullity instead of try-catch
        try
        {
            InputMethodManager inputMethodManager;
            inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e)
        {
            Timber.d("SocialFriendsFragment onPause Error" + e.toString());
        }
    }
}
