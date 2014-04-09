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
import com.tradehero.th.api.messages.MessageKeyList;
import com.tradehero.th.api.messages.PagedTypeMessageKey;
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

    @Inject Lazy<MessageListCache> messageListCache;
    DTOCache.Listener<PagedTypeMessageKey, MessageKeyList> messagesFetchListenr;
    DTOCache.GetOrFetchTask<PagedTypeMessageKey, MessageKeyList> fetchTask;
    PagedTypeMessageKey pagedTypeMessageKey;
    MessageKeyList alreadyFetched;

    MessagesView messagesView;

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
        messagesFetchListenr = null;
        alreadyFetched = null;
        pagedTypeMessageKey = null;

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

        if (pagedTypeMessageKey == null)
        {
            pagedTypeMessageKey = new PagedTypeMessageKey(0);
        }
    }

    private void initListener()
    {
        Timber.d("%s onAttachedToWindow", TAG);
        if (messagesFetchListenr == null)
        {
            messagesFetchListenr = new MessageFetchListener();
        }
    }

    private void fetchMessages()
    {
        detachPreviousTask();
        if (fetchTask == null)
        {
            fetchTask =
                    messageListCache.get()
                            .getOrFetch(pagedTypeMessageKey, false, messagesFetchListenr);
        }
        fetchTask.execute();
    }

    private void loadNextMessages()
    {
        if (pagedTypeMessageKey == null)
        {
            pagedTypeMessageKey = new PagedTypeMessageKey(0);
        }
        pagedTypeMessageKey = pagedTypeMessageKey.next();
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

    private void setListAdaper(MessageKeyList messageKeys)
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

    private void saveNewPage(MessageKeyList value)
    {
        alreadyFetched.addAll(value);
    }

    private void display(MessageKeyList value)
    {
        setListAdaper(value);
        saveNewPage(value);
    }

    class MessageFetchListener implements DTOCache.Listener<PagedTypeMessageKey, MessageKeyList>
    {

        @Override
        public void onDTOReceived(PagedTypeMessageKey key, MessageKeyList value, boolean fromCache)
        {
            display(value);
            messagesView.showListView();
        }

        @Override public void onErrorThrown(PagedTypeMessageKey key, Throwable error)
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
