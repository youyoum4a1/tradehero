package com.tradehero.th.fragments.updatecenter.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.fortysevendeg.android.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.fortysevendeg.android.swipelistview.SwipeListViewTouchListener;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.MetaHelper;
import com.tradehero.common.widget.FlagNearEndScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.updatecenter.OnTitleNumberChangeListener;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterTabType;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.Arrays;
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

    private UpdateCenterTabType tabType;

    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;

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
        Timber.d("onItemClick %d", position);
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

        @Override public void onDismiss(int[] reverseSortedPositions)
        {
            Timber.d("SwipeListener onDismiss %s", Arrays.toString(reverseSortedPositions));
            final SwipeListView swipeListView = (SwipeListView) messagesView.getListView();
            for (int position : reverseSortedPositions)
            {
                removeMessage(position);
                swipeListView.closeAnimate(position);
            }
        }
    }

    private void removeMessage(int position)
    {
        //messageListCache.get().get()
        MessageListAdapter adapter = getListAdapter();
        adapter.markDeleted(position);

        ListView listView = messagesView.getListView();
        //listView.setAdapter(adapter);
        removeMessageSync();
    }

    private void removeMessageSync()
    {
        //messageServiceWrapper.get().deleteMessage()
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
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            messagesView.showErrorView();
        }
    }

    private void fixSwipe(SwipeListView swipeListView)
    {
        MySwipeListViewTouchListener mySwipeListViewTouchListener =
                new MySwipeListViewTouchListener(swipeListView, R.id.message_item_front, R.id.message_item_back);
        mySwipeListViewTouchListener.setRightOffset(0);
        mySwipeListViewTouchListener.setLeftOffset((float) (MetaHelper.getScreensize(getActivity())[1] - 120));
        mySwipeListViewTouchListener.setSwipeClosesAllItemsWhenListMoves(true);
        mySwipeListViewTouchListener.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        mySwipeListViewTouchListener.setSwipeOpenOnLongPress(false);
        mySwipeListViewTouchListener.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        mySwipeListViewTouchListener.setSwipeDrawableChecked(R.drawable.ic_info);
        mySwipeListViewTouchListener.setSwipeDrawableUnchecked(R.drawable.ic_info);

        swipeListView.setOnTouchListener(mySwipeListViewTouchListener);
        swipeListView.setOnScrollListener(mySwipeListViewTouchListener.makeScrollListener());
    }

    class MySwipeListViewTouchListener extends SwipeListViewTouchListener
    {

        /**
         * Constructor
         *
         * @param swipeListView SwipeListView
         * @param swipeFrontView front view Identifier
         * @param swipeBackView back view Identifier
         */
        public MySwipeListViewTouchListener(
                SwipeListView swipeListView, int swipeFrontView, int swipeBackView)
        {
            super(swipeListView, swipeFrontView, swipeBackView);
        }

        @Override public AbsListView.OnScrollListener makeScrollListener()
        {
            AbsListView.OnScrollListener originalOnScrollListener = super.makeScrollListener();
            return new OnScrollListener(originalOnScrollListener);
        }

        @Override public void setSwipeDrawableChecked(int swipeDrawableChecked)
        {
            super.setSwipeDrawableChecked(swipeDrawableChecked);
        }

        @Override public void setSwipeDrawableUnchecked(int swipeDrawableUnchecked)
        {
            super.setSwipeDrawableUnchecked(swipeDrawableUnchecked);
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
}
