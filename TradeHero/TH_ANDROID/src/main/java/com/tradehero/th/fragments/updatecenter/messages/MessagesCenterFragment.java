package com.tradehero.th.fragments.updatecenter.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewListener;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by wangliang on 14-4-3.
 */
public class MessagesCenterFragment extends DashboardFragment
        implements AdapterView.OnItemClickListener
{
    public static final String TAG = "MessagesCenterFragment";
    public static final int DEFAULT_PER_PAGE = 42;

    @Inject Lazy<MessageHeaderListCache> messageListCache;
    DTOCache.Listener<MessageListKey, MessageHeaderIdList> messagesFetchListener;
    DTOCache.GetOrFetchTask<MessageListKey, MessageHeaderIdList> fetchTask;
    MessageListKey messageListKey;
    MessageHeaderIdList alreadyFetched;

    MessagesView messagesView;
    SwipeListener swipeListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
            fetchMessages();
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
        Timber.d("onItemClick %d",position);
    }

    private void initViews(View view)
    {
        DaggerUtils.inject(this);
        ButterKnife.inject(this, view);
        messagesView = (MessagesView) view;
        ListView listView = messagesView.getListView();
        listView.setOnScrollListener(new OnScrollListener());
        listView.setOnItemClickListener(this);

        if (messageListKey == null)
        {
            messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        }

        SwipeListView swipeListView = (SwipeListView) listView;
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

    private void fetchMessages()
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

    private void loadNextMessages()
    {
        if (messageListKey == null)
        {
            messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE, DEFAULT_PER_PAGE);
        }
        messageListKey = messageListKey.next();
        fetchMessages();
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
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null)
        {
            adapter = new MessageListAdapter(getActivity(), LayoutInflater.from(getActivity()),
                    R.layout.message_list_item_wrapper);
            listView.setAdapter(adapter);
        }
        MessageListAdapter messageAdapter = (MessageListAdapter) listView.getAdapter();
        messageAdapter.appendMore(messageKeys);
    }

    private MessageListAdapter getListAdaper()
    {
        ListView listView = messagesView.getListView();
        MessageListAdapter messageAdapter = (MessageListAdapter) listView.getAdapter();
        return messageAdapter;
    }

    class SwipeListener extends BaseSwipeListViewListener
    {

        @Override public void onClickBackView(int position)
        {
            Timber.d("SwipeListener onClickBackView");
            SwipeListView swipeListView = (SwipeListView) messagesView.getListView();
            swipeListView.dismiss(position);

        }

        @Override public void onDismiss(int[] reverseSortedPositions)
        {
            Timber.d("SwipeListener onDismiss");
            MessageListAdapter adapter = getListAdaper();
            if (adapter != null)
            {
                //adapter.setItems(userWatchlistCache.get().get(currentUserId.toUserBaseKey()));
                //TODO
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void removeMessage(int position)
    {
        //messageListCache.get().get()
    }

    private void saveNewPage(MessageHeaderIdList value)
    {
        if (alreadyFetched == null)
        {
            alreadyFetched = new MessageHeaderIdList();
        }
        alreadyFetched.addAll(value);
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
            Timber.d("onDTOReceived key:%s,MessageHeaderIdList:%s", key, value);
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            messagesView.showErrorView();
        }
    }

    class OnScrollListener extends FlagNearEndScrollListener
    {
        public OnScrollListener()
        {
            activate();
        }

        @Override public void raiseFlag()
        {
            super.raiseFlag();
            loadNextMessages();
        }
    }

    private UpdateCenterFragment.TitleNumberCallback callback;

    public void setTitleNumberCallback(UpdateCenterFragment.TitleNumberCallback callback)
    {
        this.callback = callback;
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
