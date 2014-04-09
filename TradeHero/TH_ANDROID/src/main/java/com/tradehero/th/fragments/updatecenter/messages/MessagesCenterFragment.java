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
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.MessageIdList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.persistence.message.MessageListCache;
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

    @Inject Lazy<MessageListCache> messageListCache;
    DTOCache.Listener<MessageListKey, MessageIdList> messagesFetchListener;
    DTOCache.GetOrFetchTask<MessageListKey, MessageIdList> fetchTask;
    MessageListKey messageListKey;
    MessageIdList alreadyFetched;

    MessagesView messagesView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("%s onCreate hasCode %d", TAG, this.hashCode());
        alreadyFetched = new MessageIdList();
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
        if (alreadyFetched == null)
        {
            fetchMessages();
            messagesView.showLoadingView();
        }
        else
        {
            setListAdaper(alreadyFetched);
            messagesView.showListView();
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
    }

    private void initListener()
    {
        Timber.d("%s onAttachedToWindow", TAG);
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

    private void setListAdaper(MessageIdList messageKeys)
    {
        ListView listView = messagesView.getListView();
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null)
        {
            adapter = new MessageListAdapter(getActivity(), LayoutInflater.from(getActivity()),
                    R.layout.message_list_item);
            listView.setAdapter(adapter);
        }
        MessageListAdapter messageAdapter = (MessageListAdapter) listView.getAdapter();
        messageAdapter.appendMore(messageKeys);
    }

    private void saveNewPage(MessageIdList value)
    {
        alreadyFetched.addAll(value);
    }

    private void display(MessageIdList value)
    {
        setListAdaper(value);
        saveNewPage(value);
    }

    class MessageFetchListener implements DTOCache.Listener<MessageListKey, MessageIdList>
    {
        @Override
        public void onDTOReceived(MessageListKey key, MessageIdList value, boolean fromCache)
        {
            display(value);
            messagesView.showListView();
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            messagesView.showErrorView();
        }
    }

    class OnScrollListener extends FlagNearEndScrollListener
    {
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
