package com.tradehero.th.fragments.social.follower;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
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
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SendMessageFragment extends DashboardFragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    public static final String KEY_DISCUSSION_TYPE =
            SendMessageFragment.class.getName() + ".discussionType";
    public static final String KEY_MESSAGE_TYPE =
            SendMessageFragment.class.getName() + ".messageType";

    private MessageType messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
    private DiscussionType discussionType = DiscussionType.BROADCAST_MESSAGE;
    /** ProgressDialog to show progress when sending message */
    private Dialog progressDialog;
    /** Dialog to change different type of follower */
    private Dialog chooseDialog;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected List<WeakReference<MiddleCallback<DiscussionDTO>>> middleCallbackSendMessages;

    @InjectView(R.id.message_input_edittext) EditText inputText;
    @InjectView(R.id.message_spinner_lifetime) Spinner lifeTimeSpinner;
    @InjectView(R.id.message_spinner_target_user) Spinner targetUserSpinner;
    @InjectView(R.id.message_type_wrapper) View messageTypeWrapperView;
    @InjectView(R.id.message_type) TextView messageTypeView;

    @Inject CurrentUserId currentUserId;
    @Inject MessageCreateFormDTOFactory messageCreateFormDTOFactory;
    @Inject Lazy<MessageServiceWrapper> messageServiceWrapper;
    @Inject Lazy<FollowerSummaryCache> followerSummaryCache;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<MessageHeaderListCache> messageListCache;

    @Inject Lazy<UserProfileCache> userProfileCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        int discussionTypeValue = args.getInt(SendMessageFragment.KEY_DISCUSSION_TYPE,
                DiscussionType.BROADCAST_MESSAGE.value);
        this.discussionType = DiscussionType.fromValue(discussionTypeValue);
        int messageTypeInt = args.getInt(SendMessageFragment.KEY_MESSAGE_TYPE);
        this.messageType = MessageType.fromId(messageTypeInt);
        middleCallbackSendMessages = new ArrayList<>();

        Timber.d("onCreate messageType:%s,discussionType:%s", messageType, discussionType);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(getString(R.string.broadcast_message_title));
        inflater.inflate(R.menu.send_message_menu, menu);
        Timber.d("onCreateOptionsMenu");
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
        Timber.d("onDestroyOptionsMenu");
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

    private void fetchFollowerForBroadcast()
    {
        if (!TextUtils.isEmpty(inputText.getText()))
        {
            progressDialogUtilLazy.get().show(getActivity(), null, getString(R.string.loading_loading));
            detachUserProfileCache();
            userProfileCache.get().invalidate(currentUserId.toUserBaseKey());

            userProfileCacheListener = createUserProfileCacheListener();
            userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
            userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey(), true);
        }
        else
        {
            THToast.show(R.string.broadcast_message_content_length_hint);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_broadcast, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView()
    {
        DeviceUtil.showKeyboardDelayed(inputText);

        messageTypeWrapperView.setOnClickListener(this);
        changeHeroType(messageType);
    }

    @Override public void onDestroyView()
    {
        detachSendMessageCallbacks();
        detachUserProfileCache();
        super.onDestroyView();
    }

    private void detachUserProfileCache()
    {
        if (userProfileCacheListener != null)
        {
            userProfileCache.get().unregister(userProfileCacheListener);
        }
        userProfileCacheListener = null;
    }

    private void detachSendMessageCallbacks()
    {
        for (WeakReference<MiddleCallback<DiscussionDTO>> weakMiddleCallback : middleCallbackSendMessages)
        {
            MiddleCallback<DiscussionDTO> middleCallback = weakMiddleCallback.get();
            if (middleCallback != null)
            {
                middleCallback.setPrimaryCallback(null);
            }
        }
        middleCallbackSendMessages.clear();
    }

    @Override public void onDestroy()
    {
        DeviceUtil.dismissKeyboard(getActivity(), inputText);
        progressDialogUtilLazy.get().dismiss(getActivity());
        super.onDestroy();
    }

    private void changeHeroType(MessageType messageType)
    {
        this.messageType = messageType;
        this.messageTypeView.setText(getString(messageType.titleResource));
        Timber.d("changeHeroType:%s, discussionType:%s", messageType, discussionType);
    }

    private void showHeroTypeDialog()
    {
        ListView listView = new ListView(getActivity());
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        TextView headerView = new TextView(getActivity());
        headerView.setPadding(0, 20, 0, 20);
        headerView.setGravity(Gravity.CENTER);
        headerView.setText(getString(R.string.broadcast_message_change_type_hint)); //TODO
        headerView.setClickable(false);
        headerView.setBackgroundColor(getResources().getColor(android.R.color.white));
        listView.addHeaderView(headerView, null, false);
        listView.setBackgroundColor(getResources().getColor(android.R.color.white));
        listView.setSelector(R.drawable.common_dialog_item_bg);
        listView.setCacheColorHint(android.R.color.transparent);
        listView.setAdapter(createMessageTypeAdapter());
        listView.setOnItemClickListener(createMessageTypeItemClickListener());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.addView(listView);
        this.chooseDialog = THDialog.showUpDialog(getSherlockActivity(), linearLayout, null);
    }

    private ArrayAdapter createMessageTypeAdapter()
    {
        return new ArrayAdapter<MessageType>(
                getActivity(),
                R.layout.common_dialog_item_layout,
                R.id.popup_text,
                MessageType.getShowingTypes())
        {

            @Override public View getView(int position, View convertView, ViewGroup parent)
            {
                View view;
                TextView text;
                if (convertView == null)
                {
                    LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
                    view = mInflater.inflate(R.layout.common_dialog_item_layout, parent, false);
                }
                else
                {
                    view = convertView;
                }
                text = (TextView) view.findViewById(R.id.popup_text);
                MessageType item = getItem(position);
                text.setText(getString(item.titleResource));
                return view;
            }
        };
    }

    private AdapterView.OnItemClickListener createMessageTypeItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object o = parent.getAdapter().getItem(position);
                changeHeroType((MessageType) o);
                dismissDialog(chooseDialog);
            }
        };
    }

    private void sendMessage(int count)
    {
        progressDialogUtilLazy.get().dismiss(getActivity());
        if (count <= 0)
        {
            THToast.show(getString(R.string.broadcast_message_no_follower_hint));
            return;
        }

        String text = inputText.getText().toString();
        if (TextUtils.isEmpty(text))
        {
            THToast.show(getString(R.string.broadcast_message_content_length_hint));
            return;
        }
        this.progressDialog =
                progressDialogUtilLazy.get().show(getActivity(),
                        getString(R.string.broadcast_message_waiting),
                        getString(R.string.broadcast_message_sending_hint));

        middleCallbackSendMessages.add(
                new WeakReference<>(
                        messageServiceWrapper.get().createMessage(
                                createMessageForm(text),
                                createSendMessageDiscussionCallback())));
    }

    private MessageCreateFormDTO createMessageForm(String messageText)
    {
        MessageCreateFormDTO messageCreateFormDTO =
                messageCreateFormDTOFactory.createEmpty(messageType);
        messageCreateFormDTO.message = messageText;
        return messageCreateFormDTO;
    }

    private int getFollowerCountByUserProfile(MessageType messageType)
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
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
                followerSummaryCache.get().get(currentUserId.toUserBaseKey());
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

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override
            public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
            {
                if (value != null)
                {
                    sendMessage(getFollowerCountByUserProfile(messageType));
                }
                else
                {
                    sendMessage(getCountFromCache(messageType));
                }
            }

            @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
            {
                THToast.show(new THException(error));
                sendMessage(getCountFromCache(messageType));
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

    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.message_type_wrapper)
        {
            showHeroTypeDialog();
        }
    }

    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
    }

    @Override public void onNothingSelected(AdapterView<?> parent)
    {
    }

    private void closeMe()
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator().popFragment();
    }

    protected Callback<DiscussionDTO> createSendMessageDiscussionCallback()
    {
        return new SendMessageDiscussionCallback();
    }

    private class SendMessageDiscussionCallback implements Callback<DiscussionDTO>
    {
        @Override public void failure(RetrofitError error)
        {
            dismissDialog(progressDialog);
            THToast.show(getString(R.string.broadcast_error));
        }

        @Override public void success(DiscussionDTO response, Response response2)
        {
            dismissDialog(progressDialog);
            invalidateMessageCache();
            THToast.show(getActivity().getString(R.string.broadcast_success));
            //TODO close me?
            closeMe();
        }
    }
}
