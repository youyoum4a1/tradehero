package com.tradehero.th.fragments.updatecenter.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.special.residemenu.ResideMenu;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DirtyNewFirstMessageHeaderDTOComparator;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterTabType;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallbackWeakList;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THRouter;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@Routable("messages")
public class MessagesCenterFragment extends DashboardFragment
        implements
        MessageItemViewWrapper.OnElementClickedListener,
        PullToRefreshBase.OnRefreshListener2<SwipeListView>,
        ResideMenu.OnMenuListener
{
    @Inject Lazy<MessageHeaderListCache> messageListCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject Lazy<DiscussionListCacheNew> discussionListCache;
    @Inject Lazy<DiscussionCache> discussionCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject DiscussionKeyFactory discussionKeyFactory;
    @Inject THRouter thRouter;

    @Nullable private DTOCacheNew.Listener<MessageListKey, ReadablePaginatedMessageHeaderDTO> fetchMessageListListener;
    @Nullable private DTOCacheNew.Listener<MessageListKey, ReadablePaginatedMessageHeaderDTO> fetchMessageRefreshListListener;
    private MessageListKey nextOlderMessageListKey;
    @Nullable private MessageListKey nextMoreRecentMessageListKey;
    @Nullable private MessageHeaderDTOList alreadyFetched;
    private MessagesView messagesView;
    private SwipeListener swipeListener;
    @NotNull private MiddleCallbackWeakList<Response> middleCallbackList;
    @Nullable private MessageListAdapter messageListAdapter;
    @Nullable private MiddleCallback<Response> messageDeletionMiddleCallback;
    private boolean hasMorePage = true;
    @Nullable private BroadcastReceiver broadcastReceiver;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        middleCallbackList = new MiddleCallbackWeakList<>();
        fetchMessageListListener = createMessageHeaderIdListCacheListener();
        fetchMessageRefreshListListener = createRefreshMessageHeaderIdListCacheListener();
        registerMessageReceiver();
        Timber.d("onCreate hasCode %d", this.hashCode());
    }

    @Override public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.update_center_messages_fragment, container, false);
        initViews(view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //if size of items already fetched is 0,then force to reload
        if (alreadyFetched == null || alreadyFetched.size() == 0)
        {
            Timber.d("onViewCreated fetch again");
            displayLoadingView(true);
            getOrFetchMessages();
        }
        else
        {
            Timber.d("onViewCreated don't have to fetch again");
            hideLoadingView();
            appendMessagesList(alreadyFetched);
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        registerMessageReceiver();
        Timber.d("onResume");
    }

    @Override public void onPause()
    {
        Timber.d("onPause");
        unregisterMessageReceiver();
        super.onPause();
    }

    private void registerMessageReceiver()
    {
        if (broadcastReceiver == null)
        {
            broadcastReceiver = new BroadcastReceiver()
            {
                @Override public void onReceive(Context context, @NotNull Intent intent)
                {
                    if (PushConstants.ACTION_MESSAGE_RECEIVED.equals(intent.getAction()))
                    {
                        Timber.d("onReceive message doRefreshContent");
                        if (messagesView != null
                                && messagesView.pullToRefreshSwipeListView.isRefreshing())
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
        //we set a message unread when click the item, so don't remove callback at the moment and do it in onDestroy.
        //unsetMiddleCallback();
        detachFetchMessageTask();
        detachFetchMessageRefreshTask();
        SwipeListView swipeListView = messagesView.getListView();
        swipeListView.setSwipeListViewListener(null);
        swipeListener = null;
        messagesView = null;
        if (messageListAdapter != null)
        {
            messageListAdapter.setElementClickedListener(null);
            messageListAdapter = null;
        }
        Timber.d("onDestroyView");

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        alreadyFetched = null;
        nextMoreRecentMessageListKey = null;
        fetchMessageListListener = null;
        fetchMessageRefreshListListener = null;
        unsetMiddleCallback();
        unregisterMessageReceiver();

        super.onDestroy();
        Timber.d("onDestroy");
    }

    private void createDeleteMessageDialog(final int position)
    {
        THDialog.showCenterDialog(getActivity(), null,
                getResources().getString(R.string.sure_to_delete_message),
                getResources().getString(android.R.string.cancel),
                getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(@NotNull DialogInterface dialog, int which)
            {
                if (which == DialogInterface.BUTTON_POSITIVE)
                {
                    dialog.dismiss();
                    removeMessageIfNecessary(position);
                }
                else if (which == DialogInterface.BUTTON_NEGATIVE)
                {
                    dialog.dismiss();
                }
            }
        }
        );
    }

    @Override public void onUserClicked(@NotNull MessageHeaderDTO messageHeaderDTO)
    {
        pushUserProfileFragment(messageHeaderDTO);
    }

    @Override public void onDeleteClicked(@NotNull MessageHeaderDTO messageHeaderDTO)
    {
        removeMessageOnServer(messageHeaderDTO);
    }

    @Override public void onPullDownToRefresh(PullToRefreshBase<SwipeListView> refreshView)
    {
        doRefreshContent();
    }

    @Override public void onPullUpToRefresh(PullToRefreshBase<SwipeListView> refreshView)
    {
    }

    private void onRefreshCompleted()
    {
        if (messagesView != null && messagesView.pullToRefreshSwipeListView != null)
        {
            messagesView.pullToRefreshSwipeListView.onRefreshComplete();
        }
    }

    @NotNull public UpdateCenterTabType getTabType()
    {
        return UpdateCenterTabType.Messages;
    }

    protected void pushMessageFragment(int position)
    {
        MessageHeaderDTO messageHeaderDTO = getListAdapter().getItem(position);
        Timber.d("pushMessageFragment=%s",messageHeaderDTO);
        //updateReadStatus(messageHeaderDTO);

        if (messageHeaderDTO != null)
        {
            pushMessageFragment(
                    discussionKeyFactory.create(messageHeaderDTO),
                    messageHeaderDTO.getCorrespondentId(currentUserId.toUserBaseKey()));
        }
    }

    private void pushUserProfileFragment(@Nullable MessageHeaderDTO messageHeaderDTO)
    {
        if (messageHeaderDTO != null)
        {
            int currentUser = currentUserId.toUserBaseKey().key;
            Bundle bundle = new Bundle();
            DashboardNavigator navigator =
                    ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
            int targetUser = messageHeaderDTO.recipientUserId;
            if (currentUser == messageHeaderDTO.recipientUserId)
            {
                targetUser = messageHeaderDTO.senderUserId;
            }
            thRouter.save(bundle, new UserBaseKey(targetUser));
            Timber.d("messageHeaderDTO recipientUserId:%s,senderUserId:%s,currentUserId%s",messageHeaderDTO.recipientUserId,messageHeaderDTO.senderUserId,currentUserId.get());
            navigator.pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    protected void pushMessageFragment(@NotNull DiscussionKey discussionKey, @NotNull UserBaseKey correspondentId)
    {
        Bundle args = new Bundle();
        // TODO separate between Private and Broadcast
        ReplyPrivateMessageFragment.putDiscussionKey(args, discussionKey);
        ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(args, correspondentId);
        getDashboardNavigator().pushFragment(ReplyPrivateMessageFragment.class, args);
    }

    private void initViews(View view)
    {
        DaggerUtils.inject(this);
        ButterKnife.inject(this, view);
        this.messagesView = (MessagesView) view;
        SwipeListView listView = messagesView.getListView();
        listView.setOnScrollListener(new OnScrollListener(null));
        SwipeListView swipeListView = listView;

        this.swipeListener = new SwipeListener();
        swipeListView.setSwipeListViewListener(swipeListener);

        messagesView.pullToRefreshSwipeListView.setOnRefreshListener(this);

        if (nextMoreRecentMessageListKey == null)
        {
            nextMoreRecentMessageListKey =
                    new MessageListKey(MessageListKey.FIRST_PAGE);
        }
    }

    private void getOrFetchMessages()
    {
        detachFetchMessageTask();
        messageListCache.get().register(nextMoreRecentMessageListKey, fetchMessageListListener);
        messageListCache.get().getOrFetchAsync(nextMoreRecentMessageListKey, false);
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
        MessageListKey messageListKey =
                new MessageListKey(MessageListKey.FIRST_PAGE);
        Timber.d("refreshContent %s", messageListKey);
        detachFetchMessageRefreshTask();
        messageListCache.get().register(messageListKey, fetchMessageRefreshListListener);
        messageListCache.get().getOrFetchAsync(messageListKey, true);
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

    private void detachFetchMessageTask()
    {
        messageListCache.get().unregister(fetchMessageListListener);
    }

    private void detachFetchMessageRefreshTask()
    {
        messageListCache.get().unregister(fetchMessageRefreshListListener);
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
        messageListAdapter.setElementClickedListener(this);

        messagesView.getListView().setAdapter(messageListAdapter);
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
        MessageHeaderDTO messageHeaderDTO = getListAdapter().getItem(position);
        removeMessageOnServer(messageHeaderDTO);
    }

    private void removeMessageOnServer(@NotNull MessageHeaderDTO messageHeaderDTO)
    {
        unsetDeletionMiddleCallback();
        messageDeletionMiddleCallback = messageServiceWrapper.get().deleteMessage(
                messageHeaderDTO.getDTOKey(),
                messageHeaderDTO.senderUserId,
                messageHeaderDTO.recipientUserId,
                messageHeaderDTO.unread ? currentUserId.toUserBaseKey() : null,
                new MessageDeletionCallback(messageHeaderDTO));
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
    }

    private void resetContent(List<MessageHeaderDTO> value)
    {
        messagesView.showListView();
        resetMessagesList(value);
        resetSavedPage(value);
        Timber.d("resetContent");
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

    @NotNull protected DTOCacheNew.Listener<MessageListKey, ReadablePaginatedMessageHeaderDTO> createMessageHeaderIdListCacheListener()
    {
        return new MessageFetchListener();
    }

    class MessageFetchListener implements DTOCacheNew.HurriedListener<MessageListKey, ReadablePaginatedMessageHeaderDTO>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull MessageListKey key,
                @NotNull ReadablePaginatedMessageHeaderDTO value)
        {
            if (value.getData().size() == 0)
            {
                hasMorePage = false;
            }
            if (getView() == null)
            {
                return;
            }
            displayContent(value.getData());
        }

        @Override
        public void onDTOReceived(
                @NotNull MessageListKey key,
                @NotNull ReadablePaginatedMessageHeaderDTO value)
        {
            if (value.getData().size() == 0)
            {
                hasMorePage = false;
            }
            requestUpdateTabCounter();
            if (getView() == null)
            {
                return;
            }
            displayContent(value.getData());
            //TODO how to invalidate the old data ..
        }

        @Override public void onErrorThrown(
                @NotNull MessageListKey key,
                @NotNull Throwable error)
        {
            hasMorePage = true;
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

    @NotNull protected DTOCacheNew.Listener<MessageListKey, ReadablePaginatedMessageHeaderDTO> createRefreshMessageHeaderIdListCacheListener()
    {
        return new RefreshMessageFetchListener();
    }

    class RefreshMessageFetchListener
            implements DTOCacheNew.Listener<MessageListKey, ReadablePaginatedMessageHeaderDTO>
    {
        @Override
        public void onDTOReceived(@NotNull MessageListKey key, @NotNull ReadablePaginatedMessageHeaderDTO value)
        {
            requestUpdateTabCounter();
            hasMorePage = (value.getData().size() > 0);
            resetPageNumber();
            if (getView() == null)
            {
                return;
            }
            resetContent(value.getData());
            onRefreshCompleted();
            //TODO how to invalidate the old data ..
        }

        @Override public void onErrorThrown(@NotNull MessageListKey key, @NotNull Throwable error)
        {
            hasMorePage = true;
            onRefreshCompleted();
            Timber.d("refresh onErrorThrown");
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

    private void unsetMiddleCallback()
    {
        unsetDeletionMiddleCallback();
        unsetMarkAsReadMiddleCallbacks();
    }

    private void unsetDeletionMiddleCallback()
    {
        if (messageDeletionMiddleCallback != null)
        {
            messageDeletionMiddleCallback.setPrimaryCallback(null);
        }
        messageDeletionMiddleCallback = null;
    }

    private void unsetMarkAsReadMiddleCallbacks()
    {
        middleCallbackList.detach();
    }

    private void updateReadStatus(@Nullable MessageHeaderDTO messageHeaderDTO)
    {
        if (messageHeaderDTO != null && messageHeaderDTO.unread)
        {
            reportMessageRead(messageHeaderDTO);
        }
    }

    private void updateReadStatus(int firstVisibleItem, int visibleItemCount)
    {
        if (messageListAdapter == null)
        {
            return;
        }
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; ++i)
        {
            MessageHeaderDTO messageHeaderDTO = messageListAdapter.getItem(i);
            if (messageHeaderDTO != null && messageHeaderDTO.unread)
            {
                reportMessageRead(messageHeaderDTO);
            }
        }
    }

    private void reportMessageRead(@NotNull MessageHeaderDTO messageHeaderDTO)
    {
        middleCallbackList.add(
                messageServiceWrapper.get().readMessage(
                        messageHeaderDTO.id,
                        messageHeaderDTO.senderUserId,
                        messageHeaderDTO.recipientUserId,
                        messageHeaderDTO.getDTOKey(),
                        currentUserId.toUserBaseKey(),
                        createMessageAsReadCallback(messageHeaderDTO)));
    }

    @NotNull private Callback<Response> createMessageAsReadCallback(MessageHeaderDTO messageHeaderDTO)
    {
        return new MessageMarkAsReadCallback(messageHeaderDTO);
    }

    private class MessageMarkAsReadCallback implements Callback<Response>
    {
        private final MessageHeaderDTO messageHeaderDTO;

        public MessageMarkAsReadCallback(MessageHeaderDTO messageHeaderDTO)
        {
            this.messageHeaderDTO = messageHeaderDTO;
        }

        @Override public void success(@NotNull Response response, Response response2)
        {
            if (response.getStatus() == 200)
            {
                Timber.d("Message %d is reported as read");
                // TODO update title

                // mark it as read in the cache
                if (messageHeaderDTO != null && messageHeaderDTO.unread)
                {
                    messageHeaderDTO.unread = false;

                    requestUpdateTabCounter();
                }
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
        }
    }

    private void requestUpdateTabCounter()
    {
        // TODO remove this hack after refactor messagecenterfragment
        Intent requestUpdateIntent = new Intent(UpdateCenterFragment.REQUEST_UPDATE_UNREAD_COUNTER);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(requestUpdateIntent);
    }

    private class MessageDeletionCallback implements Callback<Response>
    {
        @NotNull private final MessageHeaderDTO messageHeaderDTO;

        //<editor-fold desc="Constructors">
        MessageDeletionCallback(@NotNull MessageHeaderDTO messageHeaderDTO)
        {
            this.messageHeaderDTO = messageHeaderDTO;
        }
        //</editor-fold>

        @Override public void success(Response response, Response response2)
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
                }
            }
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.e(error, "Message is deleted unsuccessfully");
            if (getListAdapter() != null)
            {
                //MessageListAdapter adapter = getListAdapter();
                //adapter.markDeleted(messageId,false);
            }
        }
    }
}
