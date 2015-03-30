package com.tradehero.th.fragments.updatecenter.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.DirtyNewFirstMessageHeaderDTOComparator;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterTabType;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.discussion.DiscussionListCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

//@Routable("messages")
public class MessagesCenterFragment extends DashboardFragment
        implements
        ResideMenu.OnMenuListener
{
    @Inject Lazy<MessageHeaderListCacheRx> messageListCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject Lazy<DiscussionListCacheRx> discussionListCache;
    @Inject Lazy<DiscussionCacheRx> discussionCache;
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;

    @Nullable private MessageListKey nextMoreRecentMessageListKey;
    @Nullable private MessageHeaderDTOList alreadyFetched;
    private MessagesView messagesView;
    private SwipeListener swipeListener;
    @Nullable private MessageListAdapter messageListAdapter;
    private boolean hasMorePage = true;
    @Nullable private BroadcastReceiver broadcastReceiver;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        registerMessageReceiver();
        Timber.d("onCreate hasCode %d", this.hashCode());
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.update_center_messages_fragment, container, false);
        initViews(view);
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        //if size of items already fetched is 0,then force to reload
        if (alreadyFetched == null || alreadyFetched.size() == 0)
        {
            Timber.d("onStart fetch again");
            displayLoadingView(true);
            getOrFetchMessages();
        }
        else
        {
            Timber.d("onStart don't have to fetch again");
            hideLoadingView();
            appendMessagesList(alreadyFetched);
            setReadAllLayoutVisable();
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        registerMessageReceiver();
        if (messagesView != null && messagesView.readAllLayout != null)
        {
            messagesView.readAllLayout.setTranslationY(dashboardTabHost.get().getTranslationY());
        }
        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                if (messagesView != null && messagesView.readAllLayout != null)
                {
                    messagesView.readAllLayout.setTranslationY(y);
                }
            }
        });
    }

    @Override public void onPause()
    {
        dashboardTabHost.get().setOnTranslate(null);
        unregisterMessageReceiver();
        super.onPause();
    }

    private void registerMessageReceiver()
    {
        if (broadcastReceiver == null)
        {
            broadcastReceiver = new BroadcastReceiver()
            {
                @Override public void onReceive(Context context, @NonNull Intent intent)
                {
                    if (PushConstants.ACTION_MESSAGE_RECEIVED.equals(intent.getAction()))
                    {
                        Timber.d("onReceive message doRefreshContent");
                        if (messagesView != null
                                && messagesView.swipeRefreshLayout.isRefreshing())
                        {
                            //the better way is to start service to refresh at the background
                            if (messageListCache != null && messageListCache.get() != null)
                            {
                                messageListCache.get().invalidateAll();
                            }
                            if (messageListCache != null && messageListCache.get() != null)
                            {
                                messageListCache.get().invalidateAll();
                            }
                            return;
                        }

                        doRefreshContent();
                    }
                }
            };

            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(broadcastReceiver,
                            new IntentFilter(PushConstants.ACTION_MESSAGE_RECEIVED));
        }
    }

    private void unregisterMessageReceiver()
    {
        if (broadcastReceiver != null)
        {
            LocalBroadcastManager.getInstance(getActivity())
                    .unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    @Override public void onDestroyView()
    {
        SwipeListView swipeListView = messagesView.getListView();
        swipeListView.setSwipeListViewListener(null);
        swipeListView.setOnScrollListener(null);
        swipeListener = null;
        messagesView = null;
        if (messageListAdapter != null)
        {
            messageListAdapter.onDestroy();
            messageListAdapter = null;
        }
        Timber.d("onDestroyView");

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        alreadyFetched = null;
        nextMoreRecentMessageListKey = null;
        unregisterMessageReceiver();

        super.onDestroy();
    }

    private void onRefreshCompleted()
    {
        if (messagesView != null && messagesView.swipeRefreshLayout != null)
        {
            messagesView.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @NonNull public UpdateCenterTabType getTabType()
    {
        return UpdateCenterTabType.Messages;
    }

    protected void pushMessageFragment(int position)
    {
        MessageListAdapter adapter = getListAdapter();
        if (adapter != null)
        {
            MessageHeaderDTO messageHeaderDTO = adapter.getItem(position);
            if (messageHeaderDTO != null)
            {
                pushMessageFragment(
                        DiscussionKeyFactory.create(messageHeaderDTO),
                        messageHeaderDTO.getCorrespondentId(currentUserId.toUserBaseKey()));
            }
        }
    }

    private void pushUserProfileFragment(@Nullable MessageHeaderDTO messageHeaderDTO)
    {
        if (messageHeaderDTO != null)
        {
            int currentUser = currentUserId.toUserBaseKey().key;
            Bundle bundle = new Bundle();
            int targetUser = messageHeaderDTO.recipientUserId;
            if (currentUser == messageHeaderDTO.recipientUserId)
            {
                targetUser = messageHeaderDTO.senderUserId;
            }
            UserBaseKey targetUserKey = new UserBaseKey(targetUser);
            thRouter.save(bundle, targetUserKey);
            Timber.d("messageHeaderDTO recipientUserId:%s,senderUserId:%s,currentUserId%s", messageHeaderDTO.recipientUserId,
                    messageHeaderDTO.senderUserId, currentUserId.get());
            if (currentUserId.toUserBaseKey().equals(targetUserKey))
            {
                navigator.get().pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
                navigator.get().pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    protected void pushMessageFragment(@NonNull DiscussionKey discussionKey, @NonNull UserBaseKey correspondentId)
    {
        Bundle args = new Bundle();
        // TODO separate between Private and Broadcast
        ReplyPrivateMessageFragment.putDiscussionKey(args, discussionKey);
        ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(args, correspondentId);
        navigator.get().pushFragment(ReplyPrivateMessageFragment.class, args);
    }

    private void initViews(View view)
    {
        HierarchyInjector.inject(this);
        ButterKnife.inject(this, view);
        this.messagesView = (MessagesView) view;
        SwipeListView listView = messagesView.getListView();
        listView.setOnScrollListener(new MultiScrollListener(new OnScrollListener(null), dashboardBottomTabsListViewScrollListener.get()));

        this.swipeListener = new SwipeListener();
        listView.setSwipeListViewListener(swipeListener);

        messagesView.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                MessagesCenterFragment.this.doRefreshContent();
            }
        });

        if (nextMoreRecentMessageListKey == null)
        {
            nextMoreRecentMessageListKey =
                    new MessageListKey(MessageListKey.FIRST_PAGE);
        }
        setReadAllLayoutClickListener();
    }

    private void getOrFetchMessages()
    {
        if (nextMoreRecentMessageListKey != null)
        {
            onStopSubscriptions.add(
                    AppObservable.bindFragment(
                            this,
                            messageListCache.get().get(nextMoreRecentMessageListKey))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(createMessageHeaderIdListCacheObserver()));
        }
    }

    private void refreshContent()
    {
        displayLoadingView(false);
        doRefreshContent();
    }

    private void doRefreshContent()
    {
        discussionCache.get().invalidateAll();
        discussionListCache.get().invalidateAll();
        MessageListKey messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE);
        Timber.d("refreshContent %s", messageListKey);
        onStopSubscriptions.add(
                AppObservable.bindFragment(
                        this,
                        messageListCache.get().get(messageListKey))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createMessageHeaderIdListCacheObserver()));
    }

    private void loadNextMessages()
    {
        increasePageNumber();
        getOrFetchMessages();
    }

    private void decreasePageNumber()
    {
        if (nextMoreRecentMessageListKey == null)
        {
            return;
        }
        nextMoreRecentMessageListKey = nextMoreRecentMessageListKey.prev();
    }

    private void resetPageNumber()
    {
        nextMoreRecentMessageListKey =
                new MessageListKey(MessageListKey.FIRST_PAGE);
    }

    private void increasePageNumber()
    {
        if (nextMoreRecentMessageListKey == null)
        {
            resetPageNumber();
        }
        else
        {
            nextMoreRecentMessageListKey = nextMoreRecentMessageListKey.next();
        }
    }

    private void appendMessagesList(List<MessageHeaderDTO> messageHeaderDTOs)
    {
        if (messageListAdapter == null)
        {
            resetMessagesList(messageHeaderDTOs);
        }
        else
        {
            messageListAdapter.appendTail(messageHeaderDTOs);
            messageListAdapter.notifyDataSetChanged();
        }
    }

    private void resetMessagesList(List<MessageHeaderDTO> messageHeaderDTOs)
    {
        messageListAdapter =
                new MessageListAdapter(
                        getActivity(),
                        messageHeaderDTOs,
                        R.layout.message_list_item_wrapper,
                        new DirtyNewFirstMessageHeaderDTOComparator());
        onStopSubscriptions.add(messageListAdapter.getUserActionObservable()
                .subscribe(
                        new Action1<MessageItemView.UserAction>()
                        {
                            @Override public void call(MessageItemView.UserAction userAction)
                            {
                                MessagesCenterFragment.this.handleUserAction(userAction);
                            }
                        },
                        new ToastOnErrorAction()));

        messagesView.getListView().setAdapter(messageListAdapter);
    }

    protected void handleUserAction(MessageItemView.UserAction userAction)
    {
        if (userAction instanceof MessageItemView.UserActionUserClicked)
        {
            pushUserProfileFragment(userAction.messageHeaderDTO);
        }
        else
        {
            Timber.e(new Exception("Unhandled userAction"), "%s", userAction);
        }
    }

    @Nullable private MessageListAdapter getListAdapter()
    {
        return messageListAdapter;
    }

    @Override public void openMenu()
    {
    }

    @Override public void closeMenu()
    {
        if (messagesView != null)
        {
            SwipeListView swipeListView = messagesView.getListView();
            if (swipeListView != null)
            {
                swipeListView.closeOpenedItems();
                swipeListView.resetScrolling();
            }
        }
    }

    class SwipeListener extends BaseSwipeListViewListener
    {
        @Override public void onClickFrontView(int position)
        {
            super.onClickFrontView(position);
            pushMessageFragment(position);
        }

        @Override public void onClickBackView(int position)
        {
            super.onClickBackView(position);
            removeMessageIfNecessary(position);
        }
    }

    private void removeMessageIfNecessary(int position)
    {
        MessageListAdapter adapter = getListAdapter();
        if (adapter != null)
        {
            MessageHeaderDTO messageHeaderDTO = adapter.getItem(position);
            removeMessageOnServer(messageHeaderDTO);
        }
    }

    private void removeMessageOnServer(@NonNull final MessageHeaderDTO messageHeaderDTO)
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                messageServiceWrapper.get().deleteMessageRx(
                        messageHeaderDTO.getDTOKey(),
                        messageHeaderDTO.getSenderId(),
                        messageHeaderDTO.getRecipientId(),
                        currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<BaseResponseDTO>()
                        {
                            @Override public void call(BaseResponseDTO response)
                            {
                                MessagesCenterFragment.this.onMessageDeleted(messageHeaderDTO);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    public void onMessageDeleted(@NonNull MessageHeaderDTO messageHeaderDTO)
    {
        // mark message as deleted
        if (getListAdapter() != null)
        {
            if (alreadyFetched != null)
            {
                alreadyFetched.remove(messageHeaderDTO);
            }

            requestUpdateTabCounter();
            MessageListAdapter adapter = getListAdapter();
            if (adapter != null)
            {
                adapter.remove(messageHeaderDTO);
                adapter.notifyDataSetChanged();
                messagesView.getListView().closeOpenedItems();
            }
        }
    }

    private void saveNewPage(List<MessageHeaderDTO> value)
    {
        if (alreadyFetched == null)
        {
            alreadyFetched = new MessageHeaderDTOList();
        }
        alreadyFetched.addAll(value);
    }

    private void resetSavedPage(List<MessageHeaderDTO> value)
    {
        if (alreadyFetched == null)
        {
            alreadyFetched = new MessageHeaderDTOList();
        }
        else
        {
            alreadyFetched.clear();
        }
        alreadyFetched.addAll(value);
    }

    private void displayContent(List<MessageHeaderDTO> value)
    {
        messagesView.showListView();
        appendMessagesList(value);
        saveNewPage(value);
        setReadAllLayoutVisable();
    }

    private void displayErrorView()
    {
        messagesView.showErrorView();
    }

    private void hideLoadingView()
    {
        messagesView.showListView();
    }

    private void displayLoadingView(boolean onlyShowLoadingView)
    {
        messagesView.showLoadingView(onlyShowLoadingView);
    }

    @NonNull protected Observer<Pair<MessageListKey, ReadablePaginatedMessageHeaderDTO>> createMessageHeaderIdListCacheObserver()
    {
        return new MessageFetchObserver();
    }

    class MessageFetchObserver implements Observer<Pair<MessageListKey, ReadablePaginatedMessageHeaderDTO>>
    {
        @Override public void onNext(Pair<MessageListKey, ReadablePaginatedMessageHeaderDTO> pair)
        {
            if (pair.second.getData().size() == 0)
            {
                hasMorePage = false;
            }
            requestUpdateTabCounter();
            if (getView() == null)
            {
                return;
            }
            displayContent(pair.second.getData());
            onRefreshCompleted();
        }

        @Override public void onCompleted()
        {
            onRefreshCompleted();
        }

        @Override public void onError(Throwable e)
        {
            hasMorePage = true;
            onRefreshCompleted();
            decreasePageNumber();

            if (getView() == null)
            {
                return;
            }
            if (getListAdapter() != null && getListAdapter().getCount() > 0)
            {
                //when already fetch the data,do not show error view
                hideLoadingView();
            }
            else
            {
                displayErrorView();
            }
        }
    }

    class OnScrollListener extends FlagNearEdgeScrollListener
    {
        final AbsListView.OnScrollListener onScrollListener;

        //<editor-fold desc="Constructors">
        public OnScrollListener(AbsListView.OnScrollListener onScrollListener)
        {
            activateEnd();
            this.onScrollListener = onScrollListener;
        }
        //</editor-fold>

        @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                int totalItemCount)
        {
            if (onScrollListener != null)
            {
                onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            Timber.d("onScroll called");
            // if the count of messages is too fewer，onScroll may not be called
            //updateReadStatus(firstVisibleItem, visibleItemCount);

            super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        @Override public void onScrollStateChanged(AbsListView view, int state)
        {
            if (onScrollListener != null)
            {
                onScrollListener.onScrollStateChanged(view, state);
            }
            super.onScrollStateChanged(view, state);
        }

        @Override public void raiseEndFlag()
        {
            Timber.d("raiseEndFlag");
            if (hasMorePage)
            {
                loadNextMessages();
            }
        }
    }

    private void reportMessageAllRead()
    {
        Timber.d("reportMessageAllRead...");
        onStopSubscriptions.add(
                AppObservable.bindFragment(
                        this,
                        messageServiceWrapper.get().readAllMessageRx(
                                currentUserId.toUserBaseKey()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<BaseResponseDTO>()
                                {
                                    @Override public void call(BaseResponseDTO args)
                                    {
                                        MessagesCenterFragment.this.updateAllAsRead();
                                    }
                                },
                                new ToastOnErrorAction()
                        ));

        //Mark this locally as read, makes the user feels it's marked instantly for better experience
        updateAllAsRead();
    }

    private void updateAllAsRead()
    {
        setAllMessageRead();
        setReadAllLayoutVisable();
        requestUpdateTabCounter();
    }

    private void requestUpdateTabCounter()
    {
        // TODO remove this hack after refactor messagecenterfragment
        Intent requestUpdateIntent = new Intent(UpdateCenterFragment.REQUEST_UPDATE_UNREAD_COUNTER);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(requestUpdateIntent);
    }

    private void setReadAllLayoutClickListener()
    {
        if (messagesView != null && messagesView.readAllLayout != null)
        {
            messagesView.readAllLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    MessagesCenterFragment.this.reportMessageAllRead();
                }
            });
        }
    }

    private void setReadAllLayoutVisable()
    {
        boolean haveUnread = false;
        if (getListAdapter() == null) return;
        int itemCount = getListAdapter().getCount();
        for (int i = 0; i < itemCount; i++)
        {
            if (getListAdapter().getItem(i).unread)
            {
                haveUnread = true;
                break;
            }
        }
        if (messagesView.readAllLayout != null)
        {
            messagesView.readAllLayout.setVisibility(haveUnread ? View.VISIBLE : View.GONE);
        }
    }

    private void setAllMessageRead()
    {
        if (getListAdapter() == null) return;
        int itemCount = getListAdapter().getCount();
        for (int i = 0; i < itemCount; i++)
        {
            getListAdapter().getItem(i).unread = false;
        }
        getListAdapter().notifyDataSetChanged();
    }
}
