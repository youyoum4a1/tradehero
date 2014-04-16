package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.social.message.PrivateMessageFragment;
import com.tradehero.th.fragments.updatecenter.OnTitleNumberChangeListener;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterTabType;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by wangliang on 14-4-3.
 */
public class MessagesCenterFragment extends DashboardFragment
        implements AdapterView.OnItemClickListener, MessageListAdapter.MessageOnClickListener
{
    public static final String TAG = "MessagesCenterFragment";
    public static final int DEFAULT_PER_PAGE = 42;

    @Inject Lazy<MessageHeaderListCache> messageListCache;
    @Inject Lazy<MessageHeaderCache> messageHeaderCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;

    private DTOCache.Listener<MessageListKey, MessageHeaderIdList> messagesFetchListener;
    private DTOCache.Listener<MessageListKey, MessageHeaderIdList> refreshMessagesFetchListener;
    private DTOCache.GetOrFetchTask<MessageListKey, MessageHeaderIdList> fetchMessageTask;
    private MessageListKey messageListKey;
    private MessageHeaderIdList alreadyFetched;
    private MessagesView messagesView;
    private SwipeListener swipeListener;
    private Map<Integer, MiddleCallback<Response>> middleCallbackMap;
    private Map<Integer, Callback<Response>> callbackMap;
    private UpdateCenterTabType tabType;
    private MessageListAdapter messageListAdapter;
    private MiddleCallback<Response> messageDeletionMiddleCallback;
    private boolean hasMorePage = true;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        int tabTypeOrdinal = getArguments().getInt(UpdateCenterFragment.KEY_PAGE);
        tabType = UpdateCenterTabType.fromOrdinal(tabTypeOrdinal);
        Timber.d("%s onCreate hasCode %d", TAG, this.hashCode());
    }

    //https://github.com/JakeWharton/ActionBarSherlock/issues/828
    //https://github.com/purdyk/ActionBarSherlock/commit/30750def631aa4cdd224d4c4550b23e27c245ac4
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.add(0,0,0,"Refresh");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        Timber.d("%s onCreateOptionsMenu", TAG);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("%s onOptionsItemSelected", TAG);
       if (item.getItemId() == 0)
       {
           refreshContent();
           return true;
       }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
        Timber.d("%s onDestroyOptionsMenu", TAG);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("%s onCreateView", TAG);
        View view = inflater.inflate(R.layout.update_center_messages_fragment, container, false);
        initViews(view);
        return view;
    }



    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initListener();
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


    @Override public void onDestroyView()
    {
        detachPreviousTask();
        SwipeListView swipeListView = (SwipeListView) messagesView.getListView();
        swipeListView.setSwipeListViewListener(null);
        swipeListener = null;
        messagesView = null;
        messageListAdapter = null;
        Timber.d("%s onDestroyView", TAG);

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        messagesFetchListener = null;
        alreadyFetched = null;
        messageListKey = null;

        super.onDestroy();
        Timber.d("%s onDestroy", TAG);
    }

    /**
     * item of listview is clicked
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Timber.d("onItemClick %d", position);
    }

    /**
     * subview of item view is clicked.
     * @param position
     * @param type
     */
    @Override public void onMessageClick(int position, int type)
    {
        Timber.d("onMessageClick position:%d,type:%d", position, type);
        pushPrivateMessageFragment(position);
    }

    protected void pushPrivateMessageFragment(int position)
    {
        MessageListAdapter messageListAdapter = getListAdapter();
        MessageHeaderId messageHeaderId = messageListAdapter.getItem(position);

        MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get().get(messageHeaderId);
        Integer messageId = messageHeaderDTO.id;
        Integer senderUserId = messageHeaderDTO.senderUserId;
        Integer recipientUserId = messageHeaderDTO.recipientUserId;
        int myId = currentUserId.toUserBaseKey().key;
        Timber.d("messageId:%d,senderUserId:%d,recipientUserId:%d,myId:%d", messageId, senderUserId, recipientUserId, myId);
        int targerUserId;
        if (senderUserId != null && senderUserId == myId)
        {
            targerUserId = recipientUserId;
        }
        else
        {
            targerUserId = senderUserId;
        }

        Bundle args = new Bundle();
        args.putBundle(PrivateMessageFragment.CORRESPONDENT_USER_BASE_BUNDLE_KEY, new UserBaseKey(targerUserId).getArgs());
        getNavigator().pushFragment(PrivateMessageFragment.class, args);
    }

    private void initViews(View view)
    {
        DaggerUtils.inject(this);
        ButterKnife.inject(this, view);
        messagesView = (MessagesView) view;
        ListView listView = messagesView.getListView();
        listView.setOnScrollListener(new OnScrollListener(null));
        listView.setOnItemClickListener(this);
        SwipeListView swipeListView = (SwipeListView) listView;

        swipeListener = new SwipeListener();
        swipeListView.setSwipeListViewListener(swipeListener);

        if (messageListKey == null)
        {
            messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        }

    }

    private void initListener()
    {
        if (messagesFetchListener == null)
        {
            messagesFetchListener = new MessageFetchListener();
        }
    }

    private void getOrFetchMessages()
    {
        detachPreviousTask();
        if (fetchMessageTask == null)
        {
            fetchMessageTask = messageListCache.get().getOrFetch(messageListKey, false, messagesFetchListener);
        }
        fetchMessageTask.execute();
    }

    /**
     * TODO how to fetch latest messages
     */
    private void fetchMessages()
    {

    }

    private void refreshContent()
    {
        displayLoadingView(false);
        if (refreshMessagesFetchListener == null)
        {
            refreshMessagesFetchListener = new RefershMessageFetchListener();
        }

        MessageListKey messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        Timber.d("refreshContent %s",messageListKey);
        fetchMessageTask = messageListCache.get().getOrFetch(messageListKey, true,
                refreshMessagesFetchListener);
        fetchMessageTask.execute();
    }

    private void loadNextMessages()
    {
        increasePageNumber();
        getOrFetchMessages();
    }

    private void decreasePageNumber()
    {
        if (messageListKey == null)
        {
            return;
        }
        messageListKey = messageListKey.prev();
    }

    private void resetPageNumber()
    {
        messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE,DEFAULT_PER_PAGE);
    }

    private void increasePageNumber()
    {
        if (messageListKey == null)
        {
            messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        }
        messageListKey = messageListKey.next();
    }


    private void detachPreviousTask()
    {
        if (fetchMessageTask != null)
        {
            fetchMessageTask.setListener(null);
        }
        fetchMessageTask = null;
    }

    private void appendMessagesList(MessageHeaderIdList messageKeys)
    {
        ListView listView = messagesView.getListView();

        if (messageListAdapter == null)
        {
            messageListAdapter = new MessageListAdapter(getActivity(), LayoutInflater.from(getActivity()), R.layout.message_list_item_wrapper);
            messageListAdapter.initMarkDeletedIds(messageListCache.get().getDeletedMessageIds());
        }
        if (listView.getAdapter() == null)
        {
            listView.setAdapter(messageListAdapter);
        }
        MessageListAdapter adapter = (MessageListAdapter) listView.getAdapter();
        adapter.setMessageOnClickListener(this);
        adapter.appendMore(messageKeys);
    }

    private void resetMessagesList(MessageHeaderIdList messageKeys)
    {
        ListView listView = messagesView.getListView();

        if (messageListAdapter == null)
        {
            messageListAdapter = new MessageListAdapter(getActivity(), LayoutInflater.from(getActivity()), R.layout.message_list_item_wrapper);
            messageListAdapter.initMarkDeletedIds(messageListCache.get().getDeletedMessageIds());
        }
        else
        {
            messageListAdapter.clear();
        }
        if (listView.getAdapter() == null)
        {
            listView.setAdapter(messageListAdapter);
        }
        else
        {   MessageListAdapter adapter = (MessageListAdapter) listView.getAdapter();
            adapter.clear();
        }
        MessageListAdapter adapter = (MessageListAdapter) listView.getAdapter();
        adapter.setMessageOnClickListener(this);
        adapter.appendMore(messageKeys);
    }

    private MessageListAdapter getListAdapter()
    {
        return messageListAdapter;
    }

    class SwipeListener extends BaseSwipeListViewListener
    {

        @Override public void onClickBackView(final int position)
        {
            Timber.d("SwipeListener onClickBackView %s", position);
            final SwipeListView swipeListView = (SwipeListView) messagesView.getListView();
            //TODO it's quite difficult to use
            swipeListView.dismiss(position);
            swipeListView.closeOpenedItems();
        }

        @Override public void onClickFrontView(int position)
        {
            super.onClickFrontView(position);
            Timber.d("SwipeListener onClickFrontView %s", position);
        }

        @Override public void onDismiss(int[] reverseSortedPositions)
        {
            Timber.d("SwipeListener onDismiss %s", Arrays.toString(reverseSortedPositions));
            final SwipeListView swipeListView = (SwipeListView) messagesView.getListView();
            for (int position : reverseSortedPositions)
            {
                removeMessageIfNecessary(position);
                //swipeListView.closeAnimate(position);
            }
        }
    }

    private void removeMessageIfNecessary(int position)
    {
        MessageListAdapter adapter = getListAdapter();
        MessageHeaderId messageHeaderId = adapter.getItem(position);
        MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get().get(messageHeaderId);
        Integer messageId = messageHeaderDTO.id;
        Integer senderUserId = messageHeaderDTO.senderUserId;
        Integer recipientUserId = messageHeaderDTO.recipientUserId;
        int myId = currentUserId.toUserBaseKey().key;
        Timber.d("messageId:%d,senderUserId:%d,recipientUserId:%d,myId:%d", messageId, senderUserId, recipientUserId, myId);
        if (senderUserId != null && senderUserId == myId)
        {
            THToast.show("You cannot delete the message you sent");
            return;
        }
        adapter.markDeleted(messageHeaderId.key,true);
        removeMessageSync(messageHeaderId);
    }

    /**
     *
     * @param messageHeaderId
     */
    private void removeMessageSync(MessageHeaderId messageHeaderId)
    {
        messageDeletionMiddleCallback = messageServiceWrapper.get().deleteMessage(messageHeaderId.key, messageListCache.get(),new MessageDeletionCallback(messageHeaderId.key));
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

    private void changeTitleNumber(int number)
    {
        OnTitleNumberChangeListener listener =
                FragmentUtils.getParent(this, OnTitleNumberChangeListener.class);
        if (listener != null && !isDetached())
        {
            listener.onTitleNumberChanged(tabType, number);
        }
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
        MessageListKey messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        messageListCache.get().invalidateAll();
        messageListCache.get().put(messageListKey,data);
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
            displayContent(value);
            Timber.d("onDTOReceived key:%s,MessageHeaderIdList:%s,fromCache:%b", key, value,fromCache);
            //TODO how to invalidate the old data ..
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            hasMorePage = true;
            decreasePageNumber();
            if(getListAdapter() != null && getListAdapter().getCount() > 0)
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

    class RefershMessageFetchListener implements DTOCache.Listener<MessageListKey, MessageHeaderIdList>
    {
        @Override
        public void onDTOReceived(MessageListKey key, MessageHeaderIdList value, boolean fromCache)
        {
            if (fromCache)
            {
                //force to get news from the server
                return;
            }
            refreshCache(value);
            resetContent(value);
            resetPageNumber();
            if (value.size() == 0)
            {
                hasMorePage = false;
            }
            else
            {
                hasMorePage = true;
            }
            Timber.d("refresh onDTOReceived key:%s,MessageHeaderIdList:%s,fromCache:%b", key, value,fromCache);
            //TODO how to invalidate the old data ..
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            hasMorePage = true;
            Timber.d("refresh onErrorThrown");
            if(getListAdapter() != null && getListAdapter().getCount() > 0)
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


    class OnScrollListener extends FlagNearEndScrollListener
    {
        AbsListView.OnScrollListener onScrollListener;

        public OnScrollListener(AbsListView.OnScrollListener onScrollListener)
        {
            activate();
            this.onScrollListener = onScrollListener;
        }

        @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (onScrollListener != null)
            {
                onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            Timber.d("onScroll called");
            updateReadStatus(firstVisibleItem, visibleItemCount);

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

        @Override public void raiseFlag()
        {
            Timber.d("raiseFlag");
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

    @Override public void onResume()
    {
        super.onResume();

        Timber.d("onResume");
        initCallbackMap();
    }

    @Override public void onDetach()
    {
        super.onDetach();

        unsetMiddleCallback();
    }

    private void initCallbackMap()
    {
        callbackMap = new HashMap<>();
        middleCallbackMap = new HashMap<>();
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

        if (middleCallbackMap != null)
        {
            callbackMap.clear();
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
                MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get().get(messageHeaderId);

                if (messageHeaderDTO != null && messageHeaderDTO.unread)
                {
                    reportMessageRead(messageHeaderDTO.id);
                }
            }
        }
    }

    private void reportMessageRead(int pushId)
    {
        MiddleCallback<Response> middleCallback = middleCallbackMap.get(pushId);
        if (middleCallback == null)
        {
            middleCallback = messageServiceWrapper.get().readMessage(pushId, getCallback(pushId));
            middleCallbackMap.put(pushId, middleCallback);
        }
    }

    private Callback<Response> getCallback(int pushId)
    {
        Callback<Response> callback = callbackMap.get(pushId);
        if (callback == null)
        {
            callback = new MessageMarkAsReadCallback(pushId);
        }
        return callback;
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
                MessageHeaderDTO messageHeaderDTO = messageHeaderCache.get().get(messageHeaderId);
                if (messageHeaderDTO != null && messageHeaderDTO.unread)
                {
                    messageHeaderDTO.unread = false;
                    messageHeaderCache.get().put(messageHeaderId, messageHeaderDTO);

                    updateUnreadStatusInUserProfileCache();
                }
                middleCallbackMap.remove(messageId);
                callbackMap.remove(messageId);
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            Timber.d("Report failure for Message: %d", messageId);
        }
    }

    private void updateUnreadStatusInUserProfileCache()
    {
        // TODO synchronization problem
        UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO.unreadMessageThreadsCount > 0)
        {
            --userProfileDTO.unreadMessageThreadsCount;
        }
        userProfileCache.put(userBaseKey, userProfileDTO);

        requestUpdateTabCounter();
    }

    private void requestUpdateTabCounter()
    {
        // TODO remove this hack after refactor messagecenterfragment
        Intent requestUpdateIntent = new Intent(UpdateCenterFragment.REQUEST_UPDATE_UNREAD_COUNTER);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(requestUpdateIntent);
    }

    private class MessageDeletionCallback implements Callback<Response>
    {
        int messageId;
        MessageDeletionCallback(int messageId)
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
                //MessageListAdapter adapter = getListAdapter();

            }
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.e("Message is deleted unsuccessfully", error);
            if (getListAdapter() != null)
            {
                //MessageListAdapter adapter = getListAdapter();
                //adapter.markDeleted(messageId,false);

            }
        }
    }
}
