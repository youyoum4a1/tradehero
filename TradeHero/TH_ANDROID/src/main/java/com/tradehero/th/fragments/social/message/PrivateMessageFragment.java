package com.tradehero.th.fragments.social.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCache;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import sun.net.www.MessageHeader;
import timber.log.Timber;

import javax.inject.Inject;

public class PrivateMessageFragment extends DashboardFragment
{
    public static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY = PrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";

    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    ImageView correspondentImage;

    @Inject UserProfileCache userProfileCache;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileCacheTask;
    private UserBaseKey correspondentId;
    private UserProfileDTO correspondentProfile;

    private MessageListKey nextLoadingMessageKey;
    @Inject MessageHeaderCache messageHeaderCache;
    @Inject MessageHeaderListCache messageHeaderListCache;
    private DTOCache.Listener<MessageListKey, MessageHeaderIdList> messageHeaderListCacheListener;
    private DTOCache.GetOrFetchTask<MessageListKey, MessageHeaderIdList> messageHeaderListCacheTask;
    private MessageHeaderDTOList loadedMessages;

    @Inject DiscussionCache discussionCache;
    @Inject DiscussionListCache discussionListCache;
    private DTOCache.Listener<DiscussionListKey, DiscussionKeyList> discussionListCacheListener;
    private DTOCache.GetOrFetchTask<DiscussionListKey, DiscussionKeyList> discussionListCacheTask;

    @InjectView(R.id.message_list_view) ListView messageListView;
    PrivateMessageBubbleAdapter messageBubbleAdapter;
    @InjectView(R.id.button_send) View buttonSend;
    @InjectView(R.id.typing_message_content) EditText messageToSend;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        correspondentId = new UserBaseKey(getArguments().getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
        userProfileCacheListener = new PrivateMessageFragmentUserProfileListener();
        messageHeaderListCacheListener = new PrivateMessageFragmentMessageListListener();
        discussionListCacheListener = new PrivateMessageFragmentDiscussionListListener();
        nextLoadingMessageKey = new MessageListKey(MessageListKey.FIRST_PAGE, 10);
        loadedMessages = new MessageHeaderDTOList();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_private_message, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {

    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        getSherlockActivity().getSupportActionBar().setDisplayOptions(
                (isTabBarVisible() ? 0 : ActionBar.DISPLAY_HOME_AS_UP)
                        | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME
        );
        correspondentImage = (ImageView) menu.findItem(R.id.correspondent_picture);
        displayCorrespondentImage();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCorrespondentProfile();
        //fetchDiscussionList();
    }

    @Override public void onDestroyView()
    {
        detachUserProfileTask();
        detachMessageListTask();
        detachDiscussionListTask();
        super.onDestroyView();
    }

    private void detachUserProfileTask()
    {
        if (userProfileCacheTask != null)
        {
            userProfileCacheTask.setListener(null);
        }
        userProfileCacheTask = null;
    }

    private void detachMessageListTask()
    {
        if (messageHeaderListCacheTask != null)
        {
            messageHeaderListCacheTask.setListener(null);
        }
        messageHeaderListCacheTask = null;
    }

    private void detachDiscussionListTask()
    {
        if (discussionListCacheTask != null)
        {
            discussionListCacheTask.setListener(null);
        }
        discussionListCacheTask = null;
    }

    @Override public void onDestroy()
    {
        userProfileCacheListener = null;
        messageHeaderListCacheListener = null;
        discussionListCacheListener = null;
        super.onDestroy();
    }

    private void fetchCorrespondentProfile()
    {
        detachUserProfileTask();
        userProfileCacheTask = userProfileCache.getOrFetch(correspondentId, userProfileCacheListener);
        userProfileCacheTask.execute();
    }

    private void fetchMessageList()
    {
        detachMessageListTask();
        messageHeaderListCacheTask = messageHeaderListCache.getOrFetch(nextLoadingMessageKey, messageHeaderListCacheListener);
        messageHeaderListCacheTask.execute();
    }

    private void setNextMessageListKey()
    {
        nextLoadingMessageKey = nextLoadingMessageKey.next();
    }

    private void fetchDiscussionList()
    {
        detachDiscussionListTask();
        // TODO
        //discussionListCacheTask = discussionListCache.getOrFetch(new DiscussionListKey(DiscussionType.PRIVATE_MESSAGE, null), discussionListCacheListener);
        //discussionListCacheTask.execute();
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        correspondentProfile = userProfileDTO;
        if (andDisplay)
        {
            displayCorrespondentImage();
            displayTitle();
        }
    }

    public void linkWith(Collection<MessageHeaderDTO> messageHeaders, boolean andDisplay)
    {
        loadedMessages.addAll(messageHeaders);

        // TODO
    }

    public void linkWith(DiscussionKeyList discussionKeys, boolean andDisplay)
    {
        messageBubbleAdapter.addAll(discussionCache.get(discussionKeys));
    }

    private void displayCorrespondentImage()
    {
        if (correspondentImage != null)
        {
            RequestCreator picassoRequestCreator;
            if (correspondentProfile != null && correspondentProfile.picture != null && !correspondentProfile.picture.isEmpty())
            {
                picassoRequestCreator = picasso.load(correspondentProfile.picture);
            }
            else
            {
                picassoRequestCreator = picasso.load(R.drawable.superman_facebook);

            }
            picassoRequestCreator.transform(userPhotoTransformation)
                    .into(correspondentImage);
        }
    }

    private void displayTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (correspondentProfile != null)
        {
            actionBar.setTitle(userBaseDTOUtil.getLongDisplayName(getSherlockActivity(), correspondentProfile));
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected class PrivateMessageFragmentUserProfileListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class PrivateMessageFragmentMessageListListener implements DTOCache.Listener<MessageListKey, MessageHeaderIdList>
    {
        @Override public void onDTOReceived(MessageListKey key, MessageHeaderIdList value, boolean fromCache)
        {
            setNextMessageListKey();
            linkWith(messageHeaderCache.getMessages(value), true);
        }

        @Override public void onErrorThrown(MessageListKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class PrivateMessageFragmentDiscussionListListener implements DTOCache.Listener<DiscussionListKey, DiscussionKeyList>
    {
        @Override public void onDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(DiscussionListKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }
}
