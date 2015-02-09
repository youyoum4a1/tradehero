package com.tradehero.th.fragments.social.follower;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTOFactory;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.metrics.events.TypeEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import timber.log.Timber;

public class SendMessageFragment extends DashboardFragment
{
    public static final String KEY_MESSAGE_TYPE = SendMessageFragment.class.getName() + ".messageType";

    @NonNull private MessageType messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
    /** ProgressDialog to show progress when sending message */

    @InjectView(R.id.message_input_edittext) EditText inputText;
    @InjectView(R.id.message_type) TextView messageTypeView;
    @InjectView(R.id.follower_count_switcher) BetterViewAnimator followerCountSwitcher;
    @InjectView(R.id.follower_count_hint) TextView followerCountView;
    @InjectView(R.id.no_follower_hint) TextView noFollowerView;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject Analytics analytics;
    @Inject UserProfileDTOUtil userProfileDTOUtil;

    protected UserProfileDTO currentUserProfileDTO;

    public static void putMessageType(@NonNull Bundle args, @NonNull MessageType messageType)
    {
        args.putInt(KEY_MESSAGE_TYPE, messageType.typeId);
    }

    public static MessageType getMessageType(@NonNull Bundle args)
    {
        return MessageType.fromId(args.getInt(SendMessageFragment.KEY_MESSAGE_TYPE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        messageType = getMessageType(getArguments());
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.MessageComposer_Show));
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
        ButterKnife.inject(this, view);
        DeviceUtil.showKeyboardDelayed(inputText);
        displayMessageTypeView();
        fetchCurrentUserProfile();
    }

    @Override public void onDestroyView()
    {
        DeviceUtil.dismissKeyboard(inputText);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void fetchCurrentUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()).map(new PairGetSecond<>()))
                .subscribe(
                        this::linkWith,
                        this::handleFetchUserProfileFailed));
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

    protected boolean canSendMessage()
    {
        return currentUserProfileDTO != null
                && userProfileDTOUtil.getFollowerCountByUserProfile(messageType, currentUserProfileDTO) > 0
                && inputText != null
                && !TextUtils.isEmpty(inputText.getText());
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnTextChanged(value = R.id.message_input_edittext, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void messageTextChanged(Editable editable)
    {
        getActivity().invalidateOptionsMenu();
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.message_type)
    protected void showHeroTypeDialog(View view)
    {
        Pair<Dialog, Observable<MessageType>> dialogPair = FollowerTypeDialogFactory.showHeroTypeDialog(getActivity());
        dialogPair.second
                .subscribe(
                        messageType -> {
                            linkWith(messageType);
                            dismissDialog(dialogPair.first);
                        },
                        e -> Timber.e(e, "Failed with dialog"));
    }

    private void linkWith(@NonNull MessageType messageType)
    {
        this.messageType = messageType;
        displayMessageTypeView();
        displayFollowerCount();
        getActivity().invalidateOptionsMenu();
    }

    protected void displayMessageTypeView()
    {
        messageTypeView.setText(messageType.titleResource);
    }

    protected void displayFollowerCount()
    {
        if (currentUserProfileDTO == null)
        {
            followerCountSwitcher.setVisibility(View.GONE);
        }
        else
        {
            followerCountSwitcher.setVisibility(View.VISIBLE);
            int count = userProfileDTOUtil.getFollowerCountByUserProfile(messageType, currentUserProfileDTO);
            if (count == 0)
            {
                followerCountSwitcher.setDisplayedChildByLayoutId(R.id.no_follower_hint);
                noFollowerView.setText(getString(R.string.message_center_no_follower, getString(messageType.titleResource)));
            }
            else
            {
                followerCountSwitcher.setDisplayedChildByLayoutId(R.id.follower_count_hint);
                followerCountView.setText(getString(
                        R.string.message_center_follower_count,
                        THSignedNumber.builder(count).build().toString(),
                        getString(messageType.titleResource)));
            }
        }
    }

    private void sendMessage()
    {
        String text = inputText.getText().toString();
        ProgressDialog progressDialog = ProgressDialog.show(
                        getActivity(),
                        getActivity().getString(R.string.broadcast_message_waiting),
                        getActivity().getString(R.string.broadcast_message_sending_hint),
                        true);

        onStopSubscriptions.add(
                AppObservable.bindFragment(
                        this,
                        messageServiceWrapper.get().createMessageRx(
                                createMessageForm(text)))
                        .finallyDo(progressDialog::dismiss)
                        .subscribe(
                                this::handleDiscussionPosted,
                                this::handleDiscussionPostFailed));
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
        analytics.addEvent(new TypeEvent(AnalyticsConstants.MessageComposer_Send, messageType.localyticsResource));
        navigator.get().popFragment();
    }

    protected void handleDiscussionPostFailed(@NonNull Throwable e)
    {
        Timber.e(e, "Error posting message");
        THToast.show(getString(R.string.broadcast_error));
    }

    private void dismissDialog(Dialog dialog)
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }
}
