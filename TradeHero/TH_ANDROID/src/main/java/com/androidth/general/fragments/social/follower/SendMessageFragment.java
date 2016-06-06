package com.androidth.general.fragments.social.follower;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.androidth.general.common.fragment.HasSelectedItem;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.MessageType;
import com.androidth.general.api.discussion.form.MessageCreateFormDTO;
import com.androidth.general.api.discussion.form.MessageCreateFormDTOFactory;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.UserProfileDTOUtil;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.fragments.discussion.MentionActionButtonsView;
import com.androidth.general.fragments.discussion.MentionTaggedStockHandler;
import com.androidth.general.exception.THException;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.network.service.MessageServiceWrapper;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.rx.view.DismissDialogAction0;
import com.androidth.general.utils.DeviceUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import dagger.Lazy;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class SendMessageFragment extends BaseFragment
{
    public static final String KEY_MESSAGE_TYPE = SendMessageFragment.class.getName() + ".messageType";

    @NonNull private MessageType messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
    /** ProgressDialog to show progress when sending message */

    @Bind(R.id.message_input_edittext) EditText inputText;
    @Bind(R.id.mention_widget) MentionActionButtonsView mentionActionButtonsView;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject UserProfileCacheRx userProfileCache;
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject protected MentionTaggedStockHandler mentionTaggedStockHandler;

    protected UserProfileDTO currentUserProfileDTO;

    public static void putMessageType(@NonNull Bundle args, @NonNull MessageType messageType)
    {
        args.putInt(KEY_MESSAGE_TYPE, messageType.typeId);
    }

    @NonNull public static MessageType getMessageType(@NonNull Bundle args)
    {
        return MessageType.fromId(args.getInt(SendMessageFragment.KEY_MESSAGE_TYPE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        messageType = getMessageType(getArguments());
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.MessageComposer_Show));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.broadcast_message_title);
        inflater.inflate(R.menu.send_message_menu, menu);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_send_message).setEnabled(canSendMessage());
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_send_message:
                sendMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_broadcast, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        DeviceUtil.showKeyboardDelayed(inputText);
        mentionTaggedStockHandler.setDiscussionPostContent(inputText);
        mentionActionButtonsView.setReturnFragmentName(getClass().getName());
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchCurrentUserProfile();
        registerMentions();
    }

    @Override public void onStop()
    {
        setActionBarSubtitle(null);
        super.onStop();
    }

    @Override public void onResume()
    {
        super.onResume();
        mentionTaggedStockHandler.collectSelection();
    }

    @Override public void onDestroyView()
    {
        mentionTaggedStockHandler.setDiscussionPostContent(null);
        DeviceUtil.dismissKeyboard(inputText);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        mentionTaggedStockHandler.setHasSelectedItemFragment(null);
        mentionTaggedStockHandler = null;
        super.onDestroy();
    }

    private void fetchCurrentUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()).map(new PairGetSecond<UserBaseKey, UserProfileDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO profile)
                            {
                                linkWith(profile);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                SendMessageFragment.this.handleFetchUserProfileFailed(error);
                            }
                        }));
    }

    protected void linkWith(@NonNull UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        getActivity().invalidateOptionsMenu();
        displayFollowerCount();
    }

    protected void handleFetchUserProfileFailed(@NonNull Throwable e)
    {
        Timber.e(e, "Error fetching profile");
        THToast.show(new THException(e));
    }

    protected void registerMentions()
    {
        onStopSubscriptions.add(mentionActionButtonsView.getSelectedItemObservable()
                .subscribe(
                        new Action1<HasSelectedItem>()
                        {
                            @Override public void call(HasSelectedItem hasSelectedItem)
                            {
                                mentionTaggedStockHandler.setHasSelectedItemFragment(hasSelectedItem);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    protected boolean canSendMessage()
    {
        return currentUserProfileDTO != null
                && UserProfileDTOUtil.getFollowerCountByUserProfile(messageType, currentUserProfileDTO) > 0
                && inputText != null
                && !TextUtils.isEmpty(inputText.getText());
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnTextChanged(value = R.id.message_input_edittext, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void messageTextChanged(Editable editable)
    {
        getActivity().invalidateOptionsMenu();
    }

    protected void displayFollowerCount()
    {
        if (currentUserProfileDTO != null)
        {
            int count = UserProfileDTOUtil.getFollowerCountByUserProfile(messageType, currentUserProfileDTO);
            String subtitle;
            if (count == 0)
            {
                subtitle = getString(R.string.message_center_no_follower);
            }
            else
            {
                subtitle = getString(
                        R.string.message_center_follower_count,
                        THSignedNumber.builder(count).with000Suffix().useShortSuffix().build().toString());
            }
            setActionBarSubtitle(subtitle);
        }
    }

    private void sendMessage()
    {
        String text = inputText.getText().toString();
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getActivity().getString(R.string.broadcast_message_waiting),
                getActivity().getString(R.string.broadcast_message_sending_hint),
                true);

        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                messageServiceWrapper.get().createMessageRx(
                        createMessageForm(text)))
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new DismissDialogAction0(progressDialog))
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .subscribe(
                        new Action1<DiscussionDTO>()
                        {
                            @Override public void call(DiscussionDTO discussion)
                            {
                                SendMessageFragment.this.handleDiscussionPosted(discussion);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                SendMessageFragment.this.handleDiscussionPostFailed(error);
                            }
                        }));
    }

    private MessageCreateFormDTO createMessageForm(@NonNull String messageText)
    {
        MessageCreateFormDTO messageCreateFormDTO = MessageCreateFormDTOFactory.createEmpty(messageType);
        messageCreateFormDTO.message = messageText;
        return messageCreateFormDTO;
    }

    protected void handleDiscussionPosted(@NonNull DiscussionDTO discussionDTO)
    {
        THToast.show(R.string.broadcast_success);
        //TODO Change Analytics
        //analytics.addEvent(new TypeEvent(AnalyticsConstants.MessageComposer_Send, messageType.localyticsResource));
        navigator.get().popFragment();
    }

    protected void handleDiscussionPostFailed(@NonNull Throwable e)
    {
        Timber.e(e, "Error posting message");
        THToast.show(getString(R.string.broadcast_error));
    }
}
