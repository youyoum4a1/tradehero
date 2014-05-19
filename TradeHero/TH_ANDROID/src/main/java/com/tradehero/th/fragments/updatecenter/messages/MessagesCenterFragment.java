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
import com.special.ResideMenu.ResideMenu;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
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
import com.tradehero.th.models.push.baidu.PushMessageHandler;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class MessagesCenterFragment extends DashboardFragment
        implements
        MessageItemViewWrapper.OnElementClickedListener,
        PullToRefreshBase.OnRefreshListener2<InterceptedScrollSwipeListView>,
        ResideMenu.OnMenuListener
{
    @Inject Lazy<MessageHeaderListCache> messageListCache;
    @Inject MessageHeaderCache messageHeaderCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject Lazy<DiscussionListCacheNew> discussionListCache;
    @Inject Lazy<DiscussionCache> discussionCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject DiscussionKeyFactory discussionKeyFactory;

    private DTOCache.GetOrFetchTask<MessageListKey, MessageHeaderIdList> fetchMessageTask;
    private MessageListKey nextOlderMessageListKey;
    private MessageListKey nextMoreRecentMessageListKey;
    private MessageHeaderIdList alreadyFetched;
    private MessagesView messagesView;
    private SwipeListener swipeListener;
    private Map<Integer, MiddleCallback<Response>> middleCallbackMap;
    private MessageListAdapter messageListAdapter;
    private MiddleCallback<Response> messageDeletionMiddleCallback;
    private boolean hasMorePage = true;
    private BroadcastReceiver broadcastReceiver;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        middleCallbackMap = new HashMap<>();
        registerMessageReceiver();
        Timber.d("onCreate hasCode %d", this.hashCode());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
                @Override public void onReceive(Context context, Intent intent)
                {
                    if (PushMessageHandler.BROADCAST_ACTION_MESSAGE_RECEIVED.equals(intent.getAction()))
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
                            new IntentFilter(PushMessageHandler.BROADCAST_ACTION_MESSAGE_RECEIVED));
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
        SwipeListView swipeListView = messagesView.getListView();
        swipeListView.setSwipeListViewListener(null);
        swipeListener = null;
        messagesView = null;
        messageListAdapter.setElementClickedListener(null);
        messageListAdapter = null;
        Timber.d("onDestroyView");

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        alreadyFetched = null;
        nextMoreRecentMessageListKey = null;
        unsetMiddleCallback();
        unregisterMessageReceiver();

        super.onDestroy();
        Timber.d("onDestroy");
    }

    private void createDeleteMessageDialog(final int position) {
        THDialog.showCenterDialog(getActivity(), null,
                getResources().getString(R.string.sure_to_delete_message),
                getResources().getString(android.R.string.cancel),
                getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
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

    @Override public void onUserClicked(MessageHeaderId messageHeaderId)
    {
        pushUserProfileFragment(messageHeaderCache.get(messageHeaderId));
    }

    @Override public void onDeleteClicked(MessageHeaderId messageHeaderId)
    {
        removeMessageOnServer(messageHeaderId);
    }

    @Override public void onPullDownToRefresh(PullToRefreshBase<InterceptedScrollSwipeListView> refreshView)
    {
        doRefreshContent();
    }

    @Override public void onPullUpToRefresh(PullToRefreshBase<InterceptedScrollSwipeListView> refreshView)
    {
    }

    private void onRefreshCompleted()
    {
        if (messagesView != null && messagesView.pullToRefreshSwipeListView != null)
        {
            messagesView.pullToRefreshSwipeListView.onRefreshComplete();
        }
    }

    public UpdateCenterTabType getTabType()
    {
        return UpdateCenterTabType.Messages;
    }

    protected void pushMessageFragment(int position)
    {
        MessageHeaderDTO messageHeaderDTO =
                messageHeaderCache.get(getListAdapter().getItem(position));
        if (messageHeaderDTO != null)
        {
            pushMessageFragment(
                    discussionKeyFactory.create(messageHeaderDTO),
                    messageHeaderDTO.getCorrespondentId(currentUserId.toUserBaseKey()));
        }
    }

    private void pushUserProfileFragment(int position)
    {
        pushUserProfileFragment(messageHeaderCache.get(getListAdapter().getItem(position)));
    }

    private void pushUserProfileFragment(MessageHeaderDTO messageHeaderDTO)
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
            bundle.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, targetUser);
            Timber.d("messageHeaderDTO recipientUserId:%s,senderUserId:%s,currentUserId%s",messageHeaderDTO.recipientUserId,messageHeaderDTO.senderUserId,currentUserId.get());
            navigator.pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    protected void pushMessageFragment(DiscussionKey discussionKey, UserBaseKey correspondentId)
    {
        Bundle args = new Bundle();
        // TODO separate between Private and Broadcast
        ReplyPrivateMessageFragment.putDiscussionKey(args, discussionKey);
        ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(args, correspondentId);
        getNavigator().pushFragment(ReplyPrivateMessageFragment.class, args);
    }

    private void initViews(View view)
    {
        DaggerUtils.inject(this);
        ButterKnife.inject(this, view);
        this.messagesView = (MessagesView) view;
        InterceptedScrollSwipeListView listView = messagesView.getListView();
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
        fetchMessageTask = messageListCache.get()
                .getOrFetch(nextMoreRecentMessageListKey, false,
                        createMessageHeaderIdListCacheListener());
        fetchMessageTask.execute();
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
        fetchMessageTask = messageListCache.get().getOrFetch(messageListKey, true,
                createRefreshMessageHeaderIdListCacheListener());
        fetchMessageTask.execute();
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
        if (fetchMessageTask != null)
        {
            fetchMessageTask.setListener(null);
        }
        fetchMessageTask = null;
    }

    private void appendMessagesList(MessageHeaderIdList messageKeys)
    {
        if (messageListAdapter == null)
        {
            resetMessagesList(messageKeys);
        }
        else
        {
            messageListAdapter.appendTail(messageKeys);
            messageListAdapter.notifyDataSetChanged();
        }
    }

    private void resetMessagesList(MessageHeaderIdList messageKeys)
    {
        messageListAdapter =
                new MessageListAdapter(
                        getActivity(),
                        messageKeys,
                        R.layout.message_list_item_wrapper);
        messageListAdapter.setElementClickedListener(this);

        messagesView.getListView().setAdapter(messageListAdapter);
    }

    private MessageListAdapter getListAdapter()
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
        MessageHeaderId messageHeaderId = getListAdapter().getItem(position);
        removeMessageOnServer(messageHeaderId);
    }

    /**
     *
     * @param messageHeaderId
     */
    private void removeMessageOnServer(MessageHeaderId messageHeaderId)
    {
        unsetDeletionMiddleCallback();
        MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
        messageDeletionMiddleCallback = messageServiceWrapper.get().deleteMessage(
                messageHeaderId,
                messageHeaderDTO.senderUserId,
                messageHeaderDTO.recipientUserId,
                messageHeaderDTO.unread ? currentUserId.toUserBaseKey() : null,
                new MessageDeletionCallback(messageHeaderId));
    }

    private void saveNewPage(MessageHeaderIdList value)
    {
        if (alreadyFetched == null)
        {
            alreadyFetched = new MessageHeaderIdList();
        }
        alreadyFetched.addAll(value);
    }

    private void resetSavedPage(MessageHeaderIdList value)
    {
        if (alreadyFetched == null)
        {
            alreadyFetched = new MessageHeaderIdList();
        }
        else
        {
            alreadyFetched.clear();
        }
        alreadyFetched.addAll(value);
    }

    private void displayContent(MessageHeaderIdList value)
    {
        messagesView.showListView();
        appendMessagesList(value);
        saveNewPage(value);
    }

    private void resetContent(MessageHeaderIdList value)
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

    private void refreshCache(MessageHeaderIdList data)
    {
        MessageListKey messageListKey =
                new MessageListKey(MessageListKey.FIRST_PAGE);
        messageListCache.get().invalidateAll();
        messageListCache.get().put(messageListKey, data);
    }

    protected DTOCache.Listener<MessageListKey, MessageHeaderIdList> createMessageHeaderIdListCacheListener()
    {
        return new MessageFetchListener();
    }

    class MessageFetchListener implements DTOCache.Listener<MessageListKey, MessageHeaderIdList>
    {
        @Override
        public void onDTOReceived(MessageListKey key, MessageHeaderIdList value, boolean fromCache)
        {
            if (value.size() == 0)
            {
                hasMorePage = false;
            }
            if (!fromCache)
            {
                requestUpdateTabCounter();
            }
            if (getView() == null)
            {
                return;
            }
            displayContent(value);
            Timber.d("onDTOReceived key:%s,MessageHeaderIdList:%s,fromCache:%b", key, value,
                    fromCache);
            //TODO how to invalidate the old data ..
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
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

    protected DTOCache.Listener<MessageListKey, MessageHeaderIdList> createRefreshMessageHeaderIdListCacheListener()
    {
        return new RefreshMessageFetchListener();
    }

    class RefreshMessageFetchListener
            implements DTOCache.Listener<MessageListKey, MessageHeaderIdList>
    {
        @Override
        public void onDTOReceived(MessageListKey key, MessageHeaderIdList value, boolean fromCache)
        {
            if (fromCache)
            {
                //force to get news from the server
                return;
            }
            requestUpdateTabCounter();
            hasMorePage = (value.size() > 0);
            refreshCache(value);
            resetPageNumber();
            if (getView() == null)
            {
                return;
            }
            resetContent(value);
            onRefreshCompleted();
            //TODO how to invalidate the old data ..
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
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
        AbsListView.OnScrollListener onScrollListener;

        public OnScrollListener(AbsListView.OnScrollListener onScrollListener)
        {
            activateEnd();
            this.onScrollListener = onScrollListener;
        }

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

    @Override public boolean isTabBarVisible()
    {
        return false;
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
        if (middleCallbackMap != null)
        {
            for (MiddleCallback<Response> middleCallback : middleCallbackMap.values())
            {
                middleCallback.setPrimaryCallback(null);
            }
            middleCallbackMap.clear();
        }
    }

    private void updateReadStatus(int position)
    {
        if (messageListAdapter == null)
        {
            return;
        }
        MessageHeaderId messageHeaderId = messageListAdapter.getItem(position);
        if (messageHeaderId != null)
        {
            MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
            Timber.d("updateReadStatus :%d,unread:%s,title:%s", position, messageHeaderDTO.unread,
                    messageHeaderDTO.title);
            if (messageHeaderDTO != null && messageHeaderDTO.unread)
            {
                reportMessageRead(messageHeaderDTO);
            }
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
            MessageHeaderId messageHeaderId = messageListAdapter.getItem(i);
            if (messageHeaderId != null)
            {
                MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);

                if (messageHeaderDTO != null && messageHeaderDTO.unread)
                {
                    reportMessageRead(messageHeaderDTO);
                }
            }
        }
    }

    private void reportMessageRead(MessageHeaderDTO messageHeaderDTO)
    {
        MiddleCallback<Response> middleCallback = middleCallbackMap.get(messageHeaderDTO.id);
        if (middleCallback != null)
        {
            middleCallback.setPrimaryCallback(null);
        }
        middleCallbackMap.put(
                messageHeaderDTO.id,
                messageServiceWrapper.get().readMessage(
                        messageHeaderDTO.id,
                        messageHeaderDTO.senderUserId,
                        messageHeaderDTO.recipientUserId,
                        messageHeaderDTO.getDTOKey(),
                        currentUserId.toUserBaseKey(),
                        createMessageAsReadCallback(messageHeaderDTO.id)));
    }

    private Callback<Response> createMessageAsReadCallback(int pushId)
    {
        return new MessageMarkAsReadCallback(pushId);
    }

    private class MessageMarkAsReadCallback implements Callback<Response>
    {
        private final int messageId;

        public MessageMarkAsReadCallback(int messageId)
        {
            this.messageId = messageId;
        }

        @Override public void success(Response response, Response response2)
        {
            if (response.getStatus() == 200)
            {
                Timber.d("Message %d is reported as read");
                // TODO update title

                // mark it as read in the cache
                MessageHeaderId messageHeaderId = new MessageHeaderId(messageId);
                MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get(messageHeaderId);
                if (messageHeaderDTO != null && messageHeaderDTO.unread)
                {
                    messageHeaderDTO.unread = false;
                    messageHeaderCache.put(messageHeaderId, messageHeaderDTO);

                    requestUpdateTabCounter();
                }
                middleCallbackMap.remove(messageId);
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            Timber.d("Report failure for Message: %d", messageId);
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
        private MessageHeaderId messageId;

        MessageDeletionCallback(MessageHeaderId messageId)
        {
            this.messageId = messageId;
        }

        @Override public void success(Response response, Response response2)
        {
            // mark message as deleted
            if (getListAdapter() != null)
            {
                if (alreadyFetched != null)
                {
                    alreadyFetched.remove(messageId);
                }

                requestUpdateTabCounter();
                MessageListAdapter adapter = getListAdapter();
                if (adapter != null)
                {
                    adapter.remove(messageId);
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
