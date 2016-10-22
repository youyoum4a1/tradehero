package com.androidth.general.fragments.social.friend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.facebook.FacebookOperationCanceledException;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.common.widget.BetterViewAnimator;
import com.androidth.general.R;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.social.UserFriendsDTOList;
import com.androidth.general.api.social.key.FriendsListKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.persistence.social.friend.FriendsListCacheRx;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.DeviceUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public abstract class SocialFriendsFragment extends BaseFragment
        implements SocialFriendUserView.OnElementClickListener
{
    @Bind(R.id.friends_root_view) BetterViewAnimator friendsRootView;
    @Bind(R.id.social_follow_invite_all_container) View inviteFollowAllContainer;
    @Bind(R.id.social_follow_all) TextView followAllView;
    @Bind(R.id.social_invite_all) TextView inviteAllView;
    @Bind(R.id.social_friends_list) ListView listView;
    @Bind(android.R.id.empty) TextView emptyView;
    @Bind(R.id.search_social_friends) EditText filterTextView;

    @Inject FriendsListCacheRx friendsListCache;
    @Inject CurrentUserId currentUserId;
    @Inject Provider<SocialFriendHandler> socialFriendHandlerProvider;

    protected SocialFriendHandler socialFriendHandler;

    private BehaviorSubject<UserFriendsDTOList> userFriendsSubject;
    private FriendsListKey friendsListKey;
    @Nullable protected UserFriendsDTOList friendDTOList;
    protected List<UserFriendsDTO> followableFriends;
    protected List<UserFriendsDTO> invitableFriends;
    protected List<SocialFriendListItemDTO> listedSocialItems;
    protected SocialFriendsAdapter socialFriendsListAdapter;
    protected String filterText = "";

    BehaviorSubject<Pair<String, List<SocialFriendListItemDTO>>> filterSubject;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        friendsListKey = new FriendsListKey(currentUserId.toUserBaseKey(), getSocialNetwork());
        socialFriendsListAdapter = createSocialFriendsAdapter();
        socialFriendHandler = createFriendHandler();
        userFriendsSubject = BehaviorSubject.create();
//        fetchAllFriends();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_social_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        listView.setEmptyView(emptyView);
        listView.setAdapter(socialFriendsListAdapter);
        if (listedSocialItems != null)
        {
            filterSubject = BehaviorSubject.create(new Pair<>(filterText, listedSocialItems));
        }
        else
        {
            filterSubject = BehaviorSubject.create();
        }
        filterTextView.setText(filterText);
        listenToFilterSubject();
        listenToSubject();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getTitle());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        socialFriendsListAdapter.setOnElementClickedListener(this);
    }

    @Override public void onPause()
    {
        super.onPause();
        socialFriendsListAdapter.setOnElementClickedListener(null);
        DeviceUtil.dismissKeyboard(getActivity());
    }

    @Override public void onDestroyView()
    {
        filterSubject = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        socialFriendsListAdapter = null;
        super.onDestroy();
    }

    protected abstract SocialNetworkEnum getSocialNetwork();

    @NonNull protected SocialFriendsAdapter createSocialFriendsAdapter()
    {
        return new SocialFriendsAdapter(
                getActivity(),
                new ArrayList<SocialFriendListItemDTO>(),
                R.layout.social_friends_item,
                R.layout.social_friends_item_header);
    }

    @NonNull protected SocialFriendHandler createFriendHandler()
    {
        return socialFriendHandlerProvider.get();
    }

    protected abstract String getTitle();

    protected void listenToFilterSubject()
    {
        onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                filterSubject
                        .flatMap(new Func1<Pair<String, List<SocialFriendListItemDTO>>, Observable<? extends List<SocialFriendListItemDTO>>>()
                        {
                            @Override public Observable<? extends List<SocialFriendListItemDTO>> call(
                                    Pair<String, List<SocialFriendListItemDTO>> pair)
                            {
                                filterText = pair.first;
                                listedSocialItems = pair.second;
                                return SocialFriendsFragment.this.getFilteredObservable(pair);
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<SocialFriendListItemDTO>>()
                        {
                            @Override public void call(List<SocialFriendListItemDTO> list)
                            {
                                SocialFriendsFragment.this.displayContentView();
                                socialFriendsListAdapter.setItemsToShow(list);
                            }
                        },
                        new TimberOnErrorAction1("error when filtering")));
    }

    @NonNull protected Observable<List<SocialFriendListItemDTO>> getFilteredObservable(
            @NonNull final Pair<String, List<SocialFriendListItemDTO>> pair)
    {
        if (pair.first.trim().length() > 0)
        {
            return Observable.from(pair.second)
                    .subscribeOn(Schedulers.computation())
                    .filter(new Func1<SocialFriendListItemDTO, Boolean>()
                    {
                        @Override public Boolean call(SocialFriendListItemDTO item)
                        {
                            return item.toString().toLowerCase().contains(pair.first.toLowerCase());
                        }
                    })
                    .toList();
        }
        else
        {
            return Observable.just(pair.second);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnTextChanged(value = R.id.search_social_friends, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void filterTextChanged(Editable editText)
    {
        if (listedSocialItems != null)
        {
            filterSubject.onNext(new Pair<>(editText.toString(), listedSocialItems));
        }
    }

//Jeff no need to fetch all friends
//    protected void fetchAllFriends()
//    {
//        onDestroySubscriptions.add(getFetchAllFriendsObservable()
//                .subscribe(userFriendsSubject));
//    }
//
//    @NonNull protected Observable<UserFriendsDTOList> getFetchAllFriendsObservable()
//    {
//        return friendsListCache.get(friendsListKey)
//                .map(new PairGetSecond<FriendsListKey, UserFriendsDTOList>());
//    }

    protected void listenToSubject()
    {
        onDestroyViewSubscriptions.add(userFriendsSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserFriendsDTOList>()
                        {
                            @Override public void call(UserFriendsDTOList list)
                            {
                                SocialFriendsFragment.this.linkWith(list);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                SocialFriendsFragment.this.handleFriendListError(error);
                            }
                        }));
    }

    protected void linkWith(@NonNull UserFriendsDTOList value)
    {
        this.friendDTOList = new UserFriendsDTOList(new TreeSet<>(value)); // HACK to remove duplicates
        this.followableFriends = value.getTradeHeroUsers();
        this.invitableFriends = value.getNonTradeHeroUsers();
        filterSubject.onNext(new Pair<String, List<SocialFriendListItemDTO>>(filterText, createListedItems(value)));
        displayFollowAll();
        displayInviteAll();
    }

    @NonNull protected SocialFriendListItemDTOList createListedItems(@NonNull UserFriendsDTOList value)
    {
        return new SocialFriendListItemDTOList(value, null);
    }

    protected void handleFriendListError(@NonNull Throwable e)
    {
        Timber.e(e, "Failed to fetch friends");
        //when already fetched the data, do not show error view
        if (listedSocialItems == null)
        {
            displayErrorView();
        }
    }

    private void displayContentView()
    {
        friendsRootView.setDisplayedChildByLayoutId(R.id.content_wrapper);
    }

    protected void displayErrorView()
    {
        friendsRootView.setDisplayedChildByLayoutId(R.id.error);
    }

    private void displayFollowAll()
    {
        if (followAllView != null)
        {
            followAllView.setEnabled(canFollowAll());
        }
    }

    protected boolean canFollowAll()
    {
        return followableFriends != null && followableFriends.size() > 0;
    }

    private void displayInviteAll()
    {
        if (inviteAllView != null)
        {
            inviteAllView.setEnabled(canInviteAll());
        }
    }

    protected boolean canInviteAll()
    {
        return invitableFriends != null && invitableFriends.size() > 0;
    }

    @Override
    public void onInviteButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        handleInviteUsers(Arrays.asList(userFriendsDTO));
        Timber.d("onInviteButtonClick %s", userFriendsDTO);
    }

    @Override
    public void onFollowButtonClick(@NonNull UserFriendsDTO userFriendsDTO)
    {
        Timber.d("onFollowButtonClick %s", userFriendsDTO);
        handleFollowUsers(Arrays.asList(userFriendsDTO));
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
            inviteAllView.setText(getString(R.string.invite_number, THSignedNumber.builder(count).build().toString()));
        }
        else
        {
            inviteAllView.setText(R.string.invite);
        }
    }

    protected void handleFollowUsers(@NonNull List<UserFriendsDTO> usersToFollow)
    {
        createFriendHandler();
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                socialFriendHandler.followFriends(usersToFollow))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FollowFriendObserver(usersToFollow)));
    }

    class FollowFriendObserver extends RequestObserver<UserProfileDTO>
    {
        final List<UserFriendsDTO> usersToFollow;

        private FollowFriendObserver(List<UserFriendsDTO> usersToFollow)
        {
            super(getActivity());
            this.usersToFollow = usersToFollow;
            onRequestStart();
        }

        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            super.onNext(userProfileDTO);
            handleFollowSuccess(usersToFollow);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            handleFollowError();
        }
    }

    private void handleFollowSuccess(@Nullable List<UserFriendsDTO> usersFollowed)
    {
        friendsListCache.get(friendsListKey);
    }

    protected void handleFollowError()
    {
        // TODO
        THToast.show(R.string.follow_friend_request_error);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.social_invite_all)
    protected void inviteAll(View view)
    {
        if (invitableFriends == null)
        {
            throw new IllegalArgumentException("We should not have enabled invite all");
        }
        else
        {
            handleInviteUsers(invitableFriends);
        }
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.social_follow_all)
    protected void followAll(View view)
    {
        if (followableFriends == null)
        {
            throw new IllegalArgumentException("We should not have enabled follow all");
        }
        else
        {
            handleFollowUsers(followableFriends);
        }
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

    protected int getCountOfCheckBoxInvited()
    {
        List list = findAllUsersCheckBoxInvited();
        if (list != null)
        {
            return list.size();
        }
        return 0;
    }

    protected void handleInviteUsers(@NonNull List<UserFriendsDTO> usersToInvite)
    {
        createFriendHandler();
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                socialFriendHandler.inviteFriends(currentUserId.toUserBaseKey(), usersToInvite))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createInviteObserver(usersToInvite)));
    }

    protected RequestObserver<BaseResponseDTO> createInviteObserver(List<UserFriendsDTO> usersToInvite)
    {
        return new InviteFriendObserver(usersToInvite);
    }

    class InviteFriendObserver extends RequestObserver<BaseResponseDTO>
    {
        List<UserFriendsDTO> usersToInvite;

        private InviteFriendObserver(List<UserFriendsDTO> usersToInvite)
        {
            super(getActivity());
            this.usersToInvite = usersToInvite;
            onRequestStart();
        }

        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            super.onNext(baseResponseDTO);
            handleInviteSuccess(usersToInvite);
        }

        @Override public void onError(Throwable e)
        {
            super.onError(e);
            handleInviteError(e);
        }
    }

    protected void handleInviteSuccess(List<UserFriendsDTO> usersToInvite)
    {
        THToast.show(R.string.invite_friend_request_sent);
        friendsListCache.get(friendsListKey);
    }

    @SuppressWarnings("UnusedParameters")
    protected void handleInviteError(@NonNull Throwable e)
    {
        try{
            if (e instanceof FacebookOperationCanceledException)
            {
                THToast.show(R.string.invite_friend_request_cancelled);
            }
        }catch (Exception exception){
            THToast.show(R.string.invite_friend_request_error);
        }

    }
}
