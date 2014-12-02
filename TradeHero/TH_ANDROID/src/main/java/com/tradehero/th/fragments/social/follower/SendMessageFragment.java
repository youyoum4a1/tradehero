package com.tradehero.th.fragments.social.follower;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTOFactory;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.metrics.events.TypeEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

public class SendMessageFragment extends DashboardFragment
        implements AdapterView.OnItemSelectedListener
{
    public static final String KEY_DISCUSSION_TYPE =
            SendMessageFragment.class.getName() + ".discussionType";
    public static final String KEY_MESSAGE_TYPE =
            SendMessageFragment.class.getName() + ".messageType";

    private MessageType messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
    private DiscussionType discussionType = DiscussionType.BROADCAST_MESSAGE;
    /** ProgressDialog to show progress when sending message */
    private Dialog progressDialog;
    @NonNull protected SubscriptionList sendMessageSubscriptions;

    @InjectView(R.id.message_input_edittext) EditText inputText;
    @InjectView(R.id.message_type) TextView messageTypeView;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<FollowerSummaryCacheRx> followerSummaryCache;
    @Inject Lazy<MessageHeaderListCacheRx> messageListCache;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject MessageCreateFormDTOFactory messageCreateFormDTOFactory;
    @Inject Analytics analytics;
    @Inject FollowerTypeDialogFactory followerTypeDialogFactory;

    @Nullable Subscription userProfileSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        int discussionTypeValue = args.getInt(SendMessageFragment.KEY_DISCUSSION_TYPE,
                DiscussionType.BROADCAST_MESSAGE.value);
        this.discussionType = DiscussionType.fromValue(discussionTypeValue);
        int messageTypeInt = args.getInt(SendMessageFragment.KEY_MESSAGE_TYPE);
        messageType = MessageType.fromId(messageTypeInt);
        sendMessageSubscriptions = new SubscriptionList();

        Timber.d("onCreate messageType:%s,discussionType:%s", messageType, discussionType);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.MessageComposer_Show));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.broadcast_message_title);
        inflater.inflate(R.menu.send_message_menu, menu);
        Timber.d("onCreateOptionsMenu");
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_send_message:
                fetchFollowerForBroadcast();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_broadcast, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        DeviceUtil.showKeyboardDelayed(inputText);
        changeHeroType(messageType);
    }

    @Override public void onStop()
    {
        unsubscribe(userProfileSubscription);
        userProfileSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
        Timber.d("onDestroyOptionsMenu");
    }

    @Override public void onDestroyView()
    {
        sendMessageSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        DeviceUtil.dismissKeyboard(inputText);
        progressDialogUtilLazy.get().dismiss(getActivity());
        super.onDestroy();
    }

    private void fetchFollowerForBroadcast()
    {
        if (!TextUtils.isEmpty(inputText.getText()))
        {
            progressDialogUtilLazy.get().show(getActivity(), null, getString(R.string.loading_loading));
            unsubscribe(userProfileSubscription);
            userProfileSubscription = AndroidObservable.bindFragment(
                    this,
                    userProfileCache.get().get(currentUserId.toUserBaseKey()))
                    .subscribe(createUserProfileCacheObserver());
        }
        else
        {
            THToast.show(R.string.broadcast_message_content_length_hint);
        }
    }

    private void changeHeroType(MessageType messageType)
    {
        this.messageType = messageType;
        messageTypeView.setText(getString(messageType.titleResource));
        Timber.d("changeHeroType:%s, discussionType:%s", messageType, discussionType);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.message_type)
    protected void showHeroTypeDialog(View view)
    {
        Pair<Dialog, Observable<MessageType>> dialogPair = followerTypeDialogFactory.showHeroTypeDialog(getActivity());
        dialogPair.second
                .subscribe(
                        messageType -> {
                            changeHeroType(messageType);
                            dismissDialog(dialogPair.first);
                        },
                        e -> Timber.e(e, "Failed with dialog"));
    }

    private void sendMessage(int count)
    {
        progressDialogUtilLazy.get().dismiss(getActivity());
        if (count <= 0)
        {
            THToast.show(R.string.broadcast_message_no_follower_hint);
            return;
        }

        String text = inputText.getText().toString();
        if (TextUtils.isEmpty(text))
        {
            THToast.show(R.string.broadcast_message_content_length_hint);
            return;
        }
        this.progressDialog =
                progressDialogUtilLazy.get().show(getActivity(),
                        R.string.broadcast_message_waiting,
                        R.string.broadcast_message_sending_hint);

        sendMessageSubscriptions.add(
                AndroidObservable.bindFragment(
                        this,
                        messageServiceWrapper.get().createMessageRx(
                                createMessageForm(text)))
                        .subscribe(createSendMessageDiscussionObserver()));
    }

    private MessageCreateFormDTO createMessageForm(String messageText)
    {
        MessageCreateFormDTO messageCreateFormDTO =
                messageCreateFormDTOFactory.createEmpty(messageType);
        messageCreateFormDTO.message = messageText;
        return messageCreateFormDTO;
    }

    private int getFollowerCountByUserProfile(@NonNull MessageType messageType, @NonNull UserProfileDTO userProfileDTO)
    {
        int allFollowerCount = userProfileDTO.allFollowerCount;
        int followerCountFree = userProfileDTO.freeFollowerCount;
        int followerCountPaid = userProfileDTO.paidFollowerCount;
        Timber.d("allFollowerCount:%d,followerCountFree:%d,followerCountPaid:%d", allFollowerCount,
                followerCountFree, followerCountPaid);
        int result;
        switch (messageType)
        {
            case BROADCAST_FREE_FOLLOWERS:
                result = followerCountFree;
                break;
            case BROADCAST_PAID_FOLLOWERS:
                result = followerCountPaid;
                break;
            case BROADCAST_ALL_FOLLOWERS:
                result = allFollowerCount;
                break;
            default:
                throw new IllegalStateException("unknown messageType");
        }
        return result;
    }

    private int getCountFromCache(MessageType messageType)
    {
        FollowerSummaryDTO followerSummaryDTO =
                followerSummaryCache.get().getValue(currentUserId.toUserBaseKey());
        if (followerSummaryDTO != null)
        {
            int result;
            switch (messageType)
            {
                case BROADCAST_FREE_FOLLOWERS:
                    result = followerSummaryDTO.getFreeFollowerCount();
                    break;
                case BROADCAST_ALL_FOLLOWERS:
                    result = followerSummaryDTO.getFreeFollowerCount()
                            + followerSummaryDTO.getPaidFollowerCount();
                    break;
                case BROADCAST_PAID_FOLLOWERS:
                    result = followerSummaryDTO.getPaidFollowerCount();
                    break;
                default:
                    throw new IllegalStateException("unknown messageType");
            }
            Timber.d("getFollowerCount %s,paidFollowerCount:%d,freeFollowerCount:%d", messageType,
                    followerSummaryDTO.getPaidFollowerCount(),
                    followerSummaryDTO.getFreeFollowerCount());
            return result;
        }
        return 0;
    }

    private Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new Observer<Pair<UserBaseKey, UserProfileDTO>>()
        {
            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                Timber.e(e, "Error fetching profile");
                THToast.show(new THException(e));
                sendMessage(getCountFromCache(messageType));
            }

            @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
            {
                if (pair.second != null)
                {
                    sendMessage(getFollowerCountByUserProfile(messageType, pair.second));
                }
                else
                {
                    sendMessage(getCountFromCache(messageType));
                }
            }
        };
    }

    private void dismissDialog(Dialog dialog)
    {
        try
        {
            if (dialog != null && dialog.isShowing())
            {
                dialog.dismiss();
            }
        } catch (Exception e)
        {

        }
    }

    private void invalidateMessageCache()
    {
        messageListCache.get().invalidateAll();
    }

    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
    }

    @Override public void onNothingSelected(AdapterView<?> parent)
    {
    }

    private void closeMe()
    {
        navigator.get().popFragment();
    }

    protected Observer<DiscussionDTO> createSendMessageDiscussionObserver()
    {
        return new SendMessageDiscussionObserver();
    }

    private class SendMessageDiscussionObserver implements Observer<DiscussionDTO>
    {
        @Override public void onNext(DiscussionDTO discussionDTO)
        {
            dismissDialog(progressDialog);
            invalidateMessageCache();
            THToast.show(R.string.broadcast_success);
            analytics.addEvent(new TypeEvent(AnalyticsConstants.MessageComposer_Send, messageType.localyticsResource));
            //TODO close me?
            closeMe();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "Error posting message");
            dismissDialog(progressDialog);
            THToast.show(getString(R.string.broadcast_error));
        }
    }
}
