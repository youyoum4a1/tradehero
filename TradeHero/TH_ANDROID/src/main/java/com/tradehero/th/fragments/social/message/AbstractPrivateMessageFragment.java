package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageHeaderUserId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;
import timber.log.Timber;

abstract public class AbstractPrivateMessageFragment extends AbstractDiscussionFragment
{
    private static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY =
            AbstractPrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";

    @Inject protected MessageHeaderCacheRx messageHeaderCache;
    @Inject protected MessageHeaderListCacheRx messageHeaderListCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected Picasso picasso;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;

    protected UserBaseKey correspondentId;
    protected UserProfileDTO correspondentProfile;

    @InjectView(android.R.id.list) protected ListView discussionList;
    @InjectView(R.id.discussion_comment_widget) protected PrivatePostCommentView postWidget;
    @InjectView(R.id.private_message_empty) protected TextView emptyHint;
    @InjectView(R.id.post_comment_action_submit) protected TextView buttonSend;
    @InjectView(R.id.post_comment_text) protected EditText messageToSend;

    @Nullable private Subscription messageHeaderFetchSubscription;
    @NonNull private SubscriptionList subscriptionList;
    private MessageHeaderId messageHeaderId;

    public static void putCorrespondentUserBaseKey(@NonNull Bundle args, @NonNull UserBaseKey correspondentBaseKey)
    {
        args.putBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY, correspondentBaseKey.getArgs());
    }

    @NonNull private static UserBaseKey collectCorrespondentId(@NonNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        correspondentId = collectCorrespondentId(getArguments());
        subscriptionList = new SubscriptionList();
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new AbstractPrivateMessageFragmentUserProfileObserver();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_private_message, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);

        messageToSend.setHint(R.string.private_message_message_hint);
        buttonSend.setText(R.string.private_message_btn_send);
        if (discussionView != null)
        {
            ((PrivateDiscussionView) discussionView).setMessageType(MessageType.PRIVATE);
            ((PrivateDiscussionView) discussionView).setRecipient(correspondentId);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.private_message_refresh_btn:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCorrespondentProfile();
        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                postWidget.setTranslationY(y);
                int bottomElementsHeight = dashboardTabHost.get().getMeasuredHeight() + postWidget.getMeasuredHeight();
                discussionList.setPadding(0, 0, 0, (int) (bottomElementsHeight - y + getResources().getDimension(R.dimen.margin_small)));
            }
        });
    }

    @Override public void onPause()
    {
        dashboardTabHost.get().setOnTranslate(null);
        super.onPause();
    }

    @Override public void onDetach()
    {
        super.onDetach();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(messageHeaderFetchSubscription);
        messageHeaderFetchSubscription = null;
        subscriptionList.unsubscribe();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        messageHeaderFetchSubscription = null;
        super.onDestroy();
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        super.linkWith(discussionKey, andDisplay);

        linkWith(new MessageHeaderUserId(discussionKey.id, correspondentId), true);
    }

    private void linkWith(MessageHeaderId messageHeaderId, boolean andDisplay)
    {
        this.messageHeaderId = messageHeaderId;
        unsubscribe(messageHeaderFetchSubscription);
        messageHeaderFetchSubscription = AndroidObservable.bindFragment(
                this,
                messageHeaderCache.get(messageHeaderId))
                .subscribe(createMessageHeaderCacheObserver());
    }

    protected void refresh()
    {
        if (getDiscussionKey() != null && discussionView != null)
        {
            discussionView.refresh();

            if (messageHeaderId != null)
            {
                MessageHeaderDTO messageHeaderDTO = messageHeaderCache.getValue(messageHeaderId);
                if (messageHeaderDTO != null)
                {
                    reportMessageRead(messageHeaderDTO);
                }
            }
        }
    }

    private void fetchCorrespondentProfile()
    {
        Timber.d("fetchCorrespondentProfile");
        AndroidObservable.bindFragment(this, userProfileCache.get(correspondentId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileCacheObserver());
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        Timber.d("userProfile %s", userProfileDTO);
        correspondentProfile = userProfileDTO;
        if (andDisplay)
        {
            getActivity().invalidateOptionsMenu();
        }
    }

    //TODO set actionBar with MessageHeaderDTO by alex

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        // TODO Move into DTOProcessor?
        messageHeaderListCache.invalidateWithRecipient(correspondentId);
    }

    protected class AbstractPrivateMessageFragmentUserProfileObserver
            implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "");
        }
    }

    private void reportMessageRead(MessageHeaderDTO messageHeaderDTO)
    {
        messageHeaderCache.setUnread(messageHeaderDTO.getDTOKey(), false);
        subscriptionList.add(messageServiceWrapper.get().readMessageRx(
                messageHeaderDTO.getDTOKey(),
                messageHeaderDTO.getSenderId(),
                messageHeaderDTO.getRecipientId(),
                messageHeaderDTO.getDTOKey(),
                currentUserId.toUserBaseKey())
                .subscribe(createMessageAsReadCallback(messageHeaderDTO.getDTOKey())));
    }

    private Observer<BaseResponseDTO> createMessageAsReadCallback(MessageHeaderId messageHeaderId)
    {
        return new MessageMarkAsReadObserver(messageHeaderId);
    }

    private class MessageMarkAsReadObserver extends EmptyObserver<BaseResponseDTO>
    {
        private final MessageHeaderId messageHeaderId;

        public MessageMarkAsReadObserver(MessageHeaderId messageHeaderId)
        {
            this.messageHeaderId = messageHeaderId;
        }

        @Override public void onError(Throwable e)
        {
            Timber.d("Report failure for Message: %s", messageHeaderId);
        }
    }

    private Observer<Pair<MessageHeaderId, MessageHeaderDTO>> createMessageHeaderCacheObserver()
    {
        return new MessageHeaderFetchObserver();
    }

    private class MessageHeaderFetchObserver
            implements Observer<Pair<MessageHeaderId, MessageHeaderDTO>>
    {
        @Override public void onNext(Pair<MessageHeaderId, MessageHeaderDTO> pair)
        {
            Timber.d("MessageHeaderDTO=%s", pair.second);
            setActionBarTitle(pair.second.title);
            setActionBarSubtitle(pair.second.subTitle);
            if (pair.second.unread)
            {
                reportMessageRead(pair.second);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (e instanceof RetrofitError)
            {
                THToast.show(new THException(e));
            }
        }
    }
}
