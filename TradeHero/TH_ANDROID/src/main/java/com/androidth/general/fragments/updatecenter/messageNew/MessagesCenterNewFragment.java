package com.androidth.general.fragments.updatecenter.messageNew;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.androidth.general.common.widget.FlagNearEdgeScrollListener;
import com.androidth.general.common.widget.swipe.util.Attributes;
import com.tradehero.route.Routable;
import com.androidth.general.R;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.discussion.MessageType;
import com.androidth.general.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.discussion.key.DiscussionKeyFactory;
import com.androidth.general.api.discussion.key.MessageListKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.fragments.social.AllRelationsRecyclerFragment;
import com.androidth.general.fragments.social.follower.SendMessageFragment;
import com.androidth.general.fragments.social.message.ReplyPrivateMessageFragment;
import com.androidth.general.fragments.timeline.MeTimelineFragment;
import com.androidth.general.fragments.timeline.PushableTimelineFragment;
import com.androidth.general.fragments.updatecenter.UpdateCenterFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.graphics.ForUserPhoto;
import com.androidth.general.network.service.MessageServiceWrapper;
import com.androidth.general.persistence.discussion.DiscussionCacheRx;
import com.androidth.general.persistence.discussion.DiscussionListCacheRx;
import com.androidth.general.persistence.message.MessageHeaderListCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.rx.dialog.AlertDialogRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

@Routable("messages")
public class MessagesCenterNewFragment extends BaseFragment
        implements MessageListViewAdapter.OnMessageItemClicked
{

    @Inject Lazy<MessageHeaderListCacheRx> messageListCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject Lazy<DiscussionListCacheRx> discussionListCache;
    @Inject Lazy<DiscussionCacheRx> discussionCache;
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Nullable private MessageListKey nextMoreRecentMessageListKey;

    @BindView(R.id.layout_listview) RelativeLayout layout_listview;
    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.listview) ListView listView;
    @BindView(android.R.id.progress) ProgressBar progressBar;
    @BindView(android.R.id.empty) TextView emptyView;
    @BindView(R.id.error) View errorView;
    @BindView(R.id.composeLayout) View composeLayout;

    private boolean hasMorePage = true;
    private MessageListViewAdapter listAdapter;
    private Subscription dialogSubscription;

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.update_center_messages_new_fragment, container, false);
        init(view);

        return view;
    }

    @Override public void onDestroy()
    {
        nextMoreRecentMessageListKey = null;
        listAdapter = null;
        super.onDestroy();
    }

    @Override public void onStart()
    {
        super.onStart();
        //if size of items already fetched is 0,then force to reload
        if (listAdapter == null || listAdapter.getCount() == 0)
        {
            Timber.d("onStart fetch again");
            displayLoadingView(true);
            getOrFetchMessages();
        }
        else
        {
            Timber.d("onStart don't have to fetch again");
            hideLoadingView();
            setReadAllLayoutVisible();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        setReadAllLayoutVisible();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        onDestroyViewSubscriptions.add(ViewObservable.clicks(composeLayout).flatMap(
                new Func1<OnClickEvent, Observable<?>>()
                {
                    @Override public Observable<?> call(OnClickEvent onClickEvent)
                    {
                        final String[] composerSelection = {getString(R.string.a_hero_or_friend), getString(R.string.all_followers)};

                        return AlertDialogRx.build(new AlertDialog.Builder(getActivity()).setItems(
                                composerSelection,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override public void onClick(DialogInterface dialog, int pos)
                                    {
                                        switch (pos)
                                        {
                                            case 0:
                                                Bundle bundle = new Bundle();
                                                AllRelationsRecyclerFragment.putPerPage(bundle, AllRelationsRecyclerFragment.PREFERRED_PER_PAGE);
                                                navigator.get().pushFragment(AllRelationsRecyclerFragment.class, bundle);
                                                break;

                                            case 1:
                                                Bundle args = new Bundle();
                                                MessageType messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
                                                SendMessageFragment.putMessageType(args, messageType);
                                                navigator.get().pushFragment(SendMessageFragment.class, args);
                                                break;

                                            default:
                                                break;
                                        }
                                    }
                                })).setTitle(getString(R.string.pick_recipients))
                                .setNegativeButton(getString(R.string.cancel))
                                .build();
                    }
                }
        ).subscribe(
                new EmptyAction1<Object>(),
                new TimberOnErrorAction1("")
        ));
    }

    private void init(View view)
    {
        HierarchyInjector.inject(this);
        ButterKnife.bind(this, view);
        if (listAdapter == null)
        {
            listAdapter = new MessageListViewAdapter(getActivity(), new PrettyTime(), picasso, userPhotoTransformation);
            nextMoreRecentMessageListKey = new MessageListKey(MessageListKey.FIRST_PAGE);
        }

        listAdapter.setMode(Attributes.Mode.Multiple);
        listAdapter.setOnMessageItemClicked(this);
        listView.setAdapter(listAdapter);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("onItemClick", "onItemClick:" + position);
                pushMessageFragment(position);
                setMessageRead(position);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                MessagesCenterNewFragment.this.doRefreshContent();
            }
        });
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
            Timber.d("messageHeaderDTO recipientUserId:%s,senderUserId:%s,currentUserId%s", messageHeaderDTO.recipientUserId,
                    messageHeaderDTO.senderUserId, currentUserId.get());
            if (currentUserId.toUserBaseKey().equals(targetUserKey))
            {
                navigator.get().pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
                PushableTimelineFragment.putUserBaseKey(bundle, targetUserKey);
                navigator.get().pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    protected void pushMessageUser(int position)
    {
        if (listAdapter != null && listAdapter.getItem(position) != null)
        {
            MessageHeaderDTO messageHeaderDTO = listAdapter.getItem(position);
            pushUserProfileFragment(messageHeaderDTO);
        }
    }

    protected void pushMessageFragment(int position)
    {
        if (listAdapter != null && listAdapter.getItem(position) != null)
        {
            MessageHeaderDTO messageHeaderDTO = listAdapter.getItem(position);
            if (messageHeaderDTO != null)
            {
                pushMessageFragment(
                        DiscussionKeyFactory.create(messageHeaderDTO),
                        messageHeaderDTO.getCorrespondentId(currentUserId.toUserBaseKey()));
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

    private void doRefreshContent()
    {
        discussionCache.get().invalidateAll();
        discussionListCache.get().invalidateAll();
        MessageListKey messageListKey = new MessageListKey(MessageListKey.FIRST_PAGE);
        Timber.d("refreshContent %s", messageListKey);
        onStopSubscriptions.add(
                AppObservable.bindSupportFragment(
                        this,
                        messageListCache.get().get(messageListKey))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createMessageHeaderIdListCacheObserver()));
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
            if (pair.first.page == MessageListKey.FIRST_PAGE)
            {
                listAdapter.resetListData();
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

            if (listAdapter != null && listAdapter.getCount() > 0)
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

    private void displayContent(List<MessageHeaderDTO> value)
    {
        showListView();
        appendMessagesList(value);
        setReadAllLayoutVisible();
    }

    private void appendMessagesList(List<MessageHeaderDTO> messageHeaderDTOs)
    {
        if (listAdapter != null)
        {
            listAdapter.closeAllItems();
            if (listAdapter.getCount() == 0)
            {
                listAdapter.setListData(messageHeaderDTOs);
            }
            else
            {
                listAdapter.appendTail(messageHeaderDTOs);
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    private void setReadAllLayoutVisible()
    {
        //boolean haveUnread = false;
        //if (listAdapter == null) return;
        //int itemCount = listAdapter.getCount();
        //for (int i = 0; i < itemCount; i++)
        //{
        //    if (listAdapter.getItem(i).unread)
        //    {
        //        haveUnread = true;
        //        break;
        //    }
        //}

        if (composeLayout != null)
        {
            composeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void decreasePageNumber()
    {
        if (nextMoreRecentMessageListKey == null)
        {
            return;
        }
        nextMoreRecentMessageListKey = nextMoreRecentMessageListKey.prev();
    }

    private void getOrFetchMessages()
    {
        if (nextMoreRecentMessageListKey != null)
        {
            onStopSubscriptions.add(
                    AppObservable.bindSupportFragment(
                            this,
                            messageListCache.get().get(nextMoreRecentMessageListKey))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(createMessageHeaderIdListCacheObserver()));
        }
    }

    private void onRefreshCompleted()
    {
        if (swipeRefreshLayout != null)
        {
            swipeRefreshLayout.setRefreshing(false);
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

    private void loadNextMessages()
    {
        increasePageNumber();
        getOrFetchMessages();
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

    private void resetPageNumber()
    {
        nextMoreRecentMessageListKey =
                new MessageListKey(MessageListKey.FIRST_PAGE);
    }

    private void removeMessageIfNecessary(int position)
    {
        if (listAdapter != null && listAdapter.getItem(position) != null)
        {
            MessageHeaderDTO messageHeaderDTO = listAdapter.getItem(position);
            removeMessageOnServer(messageHeaderDTO);
        }
    }

    private void removeMessageOnServer(@NonNull final MessageHeaderDTO messageHeaderDTO)
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
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
                                MessagesCenterNewFragment.this.onMessageDeleted(messageHeaderDTO);
                            }
                        },
                        new ToastOnErrorAction1()));
        //MessagesCenterNewFragment.this.onMessageDeleted(messageHeaderDTO);
    }

    public void onMessageDeleted(@NonNull MessageHeaderDTO messageHeaderDTO)
    {
        if (listAdapter != null)
        {
            listAdapter.remove(messageHeaderDTO);
            listAdapter.closeAllItems();
            listAdapter.notifyDataSetChanged();
        }
    }

    private void displayLoadingView(boolean onlyShowLoadingView)
    {
        showLoadingView(onlyShowLoadingView);
    }

    private void displayErrorView()
    {
        showErrorView();
    }

    public void showErrorView()
    {
        showOnlyThis(errorView);
    }

    public void showListView()
    {
        showOnlyThis(layout_listview);
    }

    private void hideLoadingView()
    {
        showListView();
    }

    public void showEmptyView()
    {
        showOnlyThis(emptyView);
    }

    public void showLoadingView(boolean onlyShowLoadingView)
    {
        showOnlyThis(progressBar);
        if (!onlyShowLoadingView)
        {
            changeViewVisibility(layout_listview, true);
        }
    }

    private void showOnlyThis(View view)
    {
        changeViewVisibility(layout_listview, view == layout_listview);
        changeViewVisibility(errorView, view == errorView);
        changeViewVisibility(progressBar, view == progressBar);
        changeViewVisibility(emptyView, view == emptyView);
    }

    private void changeViewVisibility(View view, boolean visible)
    {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setMessageRead(int position)
    {
        if (listAdapter != null && position < listAdapter.getCount())
        {
            listAdapter.getItem(position).unread = false;
        }
    }

    private void updateAllAsRead()
    {
        setAllMessageRead();
        setReadAllLayoutVisible();
        requestUpdateTabCounter();
    }

    private void setAllMessageRead()
    {
        if (listAdapter == null) return;
        int itemCount = listAdapter.getCount();
        for (int i = 0; i < itemCount; i++)
        {
            listAdapter.getItem(i).unread = false;
        }
        listAdapter.notifyDataSetChanged();
    }

    private void requestUpdateTabCounter()
    {
        Intent requestUpdateIntent = new Intent(UpdateCenterFragment.REQUEST_UPDATE_UNREAD_COUNTER);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(requestUpdateIntent);
    }

    @Override public void clickedItemUser(int position)
    {
        pushMessageUser(position);
    }

    @Override public void clickedItemDelete(int position)
    {
        removeMessageIfNecessary(position);
    }
}
