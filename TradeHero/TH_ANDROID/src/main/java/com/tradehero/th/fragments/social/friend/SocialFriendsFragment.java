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
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created by wangliang on 14-5-26.
 */
public abstract class SocialFriendsFragment extends DashboardFragment implements SocialFriendItemView.OnElementClickListener {

    @InjectView(R.id.friends_root_view) SocialFriendsListView friendsRootView;
    @InjectView(R.id.search_social_friends)EditText searchEdit;
    @Inject FriendsListCache friendsListCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject SocialFriendHandler socialFriendHandler;

    private FriendsListKey friendsListKey;
    private FriendDTOList friendDTOList;
    private SocialFriendsAdapter socialFriendsListAdapter;
    //DTOCache.GetOrFetchTask fetchTask;

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

        //socialFriendHandler.followFriends(getActivity(), Arrays.asList(userFriendsDTO));

        Timber.d("onInviteButtonClick %s", userFriendsDTO);
    }

    @Override
    public void onFollowButtonClick(UserFriendsDTO userFriendsDTO) {
        Timber.d("onFollowButtonClick %s",userFriendsDTO);



    }

    protected abstract SocialNetworkEnum getSocialNetwork();

    protected abstract String getTitle();


    private void initView()
    {
        searchEdit.addTextChangedListener(new SearchChangeListener());

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
        this.friendDTOList = value;
        bindData();
        friendsRootView.showContentView();
    }

    private void bindData()
    {
        socialFriendsListAdapter =
                new SocialFriendsAdapter(
                        getActivity(),
                        friendDTOList,
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
