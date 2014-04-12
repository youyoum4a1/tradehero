package com.tradehero.th.fragments.updatecenter.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
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
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.updatecenter.OnTitleNumberChangeListener;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterTabType;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
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
        implements AdapterView.OnItemClickListener,MessageListAdapter.MessageOnClickListener
{
    public static final String TAG = "MessagesCenterFragment";
    public static final int DEFAULT_PER_PAGE = 42;

    @Inject Lazy<MessageHeaderListCache> messageListCache;
    @Inject Lazy<MessageHeaderCache> messageHeaderCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;

    DTOCache.Listener<MessageListKey, MessageHeaderIdList> messagesFetchListener;
    DTOCache.GetOrFetchTask<MessageListKey, MessageHeaderIdList> fetchTask;
    MessageListKey messageListKey;
    MessageHeaderIdList alreadyFetched;

    MessagesView messagesView;
    SwipeListener swipeListener;

    private Map<Integer, MiddleCallback<Response>> middleCallbackMap;
    private Map<Integer, Callback<Response>> callbackMap;

    private UpdateCenterTabType tabType;

    @Inject Lazy<MessageEraser> messageEraser;

    private boolean isFirst = true;
    private MessageListAdapter messageListAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        int tabTypeOrdinal = getArguments().getInt(UpdateCenterFragment.KEY_PAGE);

        tabType = UpdateCenterTabType.fromOrdinal(tabTypeOrdinal);
        Timber.d("%s onCreate hasCode %d", TAG, this.hashCode());

    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        Timber.d("%s onCreateOptionsMenu", TAG);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
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
            messagesView.showLoadingView();
            getOrfetchMessages();
        }
        else
        {
            messagesView.showListView();
            setListAdaper(alreadyFetched);
        }


    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Timber.d("%s onSaveInstanceState", TAG);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        detachPreviousTask();
        SwipeListView swipeListView = (SwipeListView) messagesView.getListView();
        swipeListener = null;
        swipeListView.setSwipeListViewListener(null);
        messagesView = null;
        Timber.d("%s onDestroyView", TAG);
    }

    @Override public void onDestroy()
    {
        messagesFetchListener = null;
        alreadyFetched = null;
        messageListKey = null;

        super.onDestroy();
        Timber.d("%s onDestroy", TAG);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Timber.d("onItemClick %d", position);
    }

    @Override public void onMessageClick(int position, int type)
    {
        Timber.d("onMessageClick position:%d,type:%d", position,type);
    }

    private void initViews(View view)
    {
        DaggerUtils.inject(this);
        ButterKnife.inject(this, view);
        messagesView = (MessagesView) view;
        ListView listView = messagesView.getListView();
        listView.setOnScrollListener(new OnScrollListener(null));
        listView.setOnItemClickListener(this);

        if (messageListKey == null)
        {
            messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        }

        SwipeListView swipeListView = (SwipeListView) listView;
        //fixSwipe(swipeListView);
        swipeListener = new SwipeListener();
        swipeListView.setSwipeListViewListener(swipeListener);
    }

    private void initListener()
    {
        if (messagesFetchListener == null)
        {
            messagesFetchListener = new MessageFetchListener();
        }
    }

    private void getOrfetchMessages()
    {
        detachPreviousTask();
        if (fetchTask == null)
        {
            fetchTask =
                    messageListCache.get()
                            .getOrFetch(messageListKey, false, messagesFetchListener);
        }
        fetchTask.execute();
    }

    /**
     * TODO how to fetch latest messages
     */
    private void fetchMessages()
    {

    }

    private void loadNextMessages()
    {
        if (messageListKey == null)
        {
            messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        }
        messageListKey = messageListKey.next();
        getOrfetchMessages();
    }

    private void detachPreviousTask()
    {
        if (fetchTask != null)
        {
            fetchTask.setListener(null);
        }
        fetchTask = null;
    }

    private void setListAdaper(MessageHeaderIdList messageKeys)
    {
        ListView listView = messagesView.getListView();
        if (messageListAdapter == null)
        {
            messageListAdapter = new MessageListAdapter(getActivity(), LayoutInflater.from(getActivity()), R.layout.message_list_item_wrapper);
            listView.setAdapter(messageListAdapter);
        }
        MessageListAdapter messageAdapter = (MessageListAdapter) listView.getAdapter();
        messageAdapter.setMessageOnClickListener(this);
        messageAdapter.appendMore(messageKeys);
    }

    private MessageListAdapter getListAdapter()
    {
        ListView listView = messagesView.getListView();
        MessageListAdapter messageAdapter = (MessageListAdapter) listView.getAdapter();
        return messageAdapter;
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
        Timber.d("messageId:%d,senderUserId:%d,recipientUserId:%d,myId:%d",messageId,senderUserId,recipientUserId,myId);
        if (senderUserId != null && senderUserId == myId)
        {
            THToast.show("You cannot delete the message you sent");
            return;
        }
        messageHeaderId = adapter.markDeleted(position);
        removeMessageSync(messageHeaderId);
    }

    /**
     *
     * @param messageHeaderId
     */
    private void removeMessageSync(MessageHeaderId messageHeaderId)
    {
        messageEraser.get().deleteMessage(messageHeaderId);
    }


    private void saveNewPage(MessageHeaderIdList value)
    {
        if (alreadyFetched == null)
        {
            alreadyFetched = new MessageHeaderIdList();
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

    private void display(MessageHeaderIdList value)
    {
        setListAdaper(value);
        saveNewPage(value);
    }

    class MessageFetchListener implements DTOCache.Listener<MessageListKey, MessageHeaderIdList>
    {
        @Override
        public void onDTOReceived(MessageListKey key, MessageHeaderIdList value, boolean fromCache)
        {
            display(value);
            messagesView.showListView();
            changeTitleNumber(value.size());
            Timber.d("onDTOReceived key:%s,MessageHeaderIdList:%s", key, value);
            if (isFirst)
            {

            }
            isFirst = false;
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            messagesView.showErrorView();
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
            super.raiseFlag();
            loadNextMessages();
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    @Override public void onResume()
    {
        super.onResume();

        initCallbackMap();
    }

    @Override public void onDetach()
    {
        super.onDetach();

        messageListAdapter = null;

        unsetMiddleCallback();
    }

    private void initCallbackMap()
    {
        callbackMap = new HashMap<>();
        middleCallbackMap = new HashMap<>();
    }

    private void unsetMiddleCallback()
    {
        for (MiddleCallback<Response> middleCallback: middleCallbackMap.values())
        {
            middleCallback.setPrimaryCallback(null);
        }

        callbackMap.clear();
        middleCallbackMap.clear();
    }


    private void updateReadStatus(int firstVisibleItem, int visibleItemCount)
    {
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
                MessageHeaderDTO notificationDTO = messageHeaderCache.get().get(messageHeaderId);
                if (notificationDTO != null && notificationDTO.unread)
                {
                    notificationDTO.unread = false;
                    messageHeaderCache.get().put(messageHeaderId, notificationDTO);
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
}
