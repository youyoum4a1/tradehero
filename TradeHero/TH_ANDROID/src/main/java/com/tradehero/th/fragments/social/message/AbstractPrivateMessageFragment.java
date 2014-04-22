package com.tradehero.th.fragments.social.message;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageType;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.user.FollowUserAssistant;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import timber.log.Timber;

abstract public class AbstractPrivateMessageFragment extends AbstractDiscussionFragment
{
    private static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY =
            AbstractPrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";

    @Inject protected MessageHeaderListCache messageHeaderListCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserBaseDTOUtil userBaseDTOUtil;
    @Inject protected Picasso picasso;
    @Inject @ForUserPhoto protected Transformation userPhotoTransformation;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;

    @Inject protected UserProfileCache userProfileCache;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileCacheTask;
    protected UserBaseKey correspondentId;
    protected UserProfileDTO correspondentProfile;

    protected ImageView correspondentImage;
    @InjectView(R.id.private_message_empty) protected TextView emptyHint;
    @InjectView(R.id.post_comment_action_submit) protected TextView buttonSend;
    @InjectView(R.id.post_comment_text) protected EditText messageToSend;

    public static void putCorrespondentUserBaseKey(Bundle args, UserBaseKey correspondentBaseKey)
    {
        args.putBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY, correspondentBaseKey.getArgs());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        correspondentId =
                new UserBaseKey(getArguments().getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
    }

    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new AbstractPrivateMessageFragmentUserProfileListener();
    }

    @Override protected FollowUserAssistant.OnUserFollowedListener createUserFollowedListener()
    {
        return new AbstractPrivateMessageFragmentUserFollowedListener();
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
        display();
        ((PrivateDiscussionView) discussionView).setMessageType(MessageType.PRIVATE);
        ((PrivateDiscussionView) discussionView).setMessageNotAllowedToSendListener(
                new AbstractPrivateMessageFragmentOnMessageNotAllowedToSendListener());
        ((PrivateDiscussionView) discussionView).setRecipient(correspondentId);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.private_message_menu, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setSubtitle(R.string.private_message_subtitle);

        correspondentImage = (ImageView) menu.findItem(R.id.correspondent_picture);
        displayTitle();
        displayCorrespondentImage();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean handled = super.onOptionsItemSelected(item);
        if (!handled)
        {
            switch (item.getItemId())
            {
                case R.id.private_message_refresh_btn:
                    refresh();
                    handled = true;
                    break;
            }
        }
        return handled;
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCorrespondentProfile();
    }

    @Override public void onDestroyOptionsMenu()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        detachUserProfileTask();
        ButterKnife.reset(this);
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

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    protected void refresh()
    {
        if (getDiscussionKey() != null)
        {
            messageHeaderListCache.invalidateKeysThatList(new MessageHeaderId(getDiscussionKey().id));
            linkWith(getDiscussionKey(), true);
        }
    }

    private void fetchCorrespondentProfile()
    {
        Timber.d("fetchCorrespondentProfile");
        detachUserProfileTask();
        userProfileCacheTask =
                userProfileCache.getOrFetch(correspondentId, createUserProfileCacheListener());
        userProfileCacheTask.execute();
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        Timber.d("userProfile %s", userProfileDTO);
        correspondentProfile = userProfileDTO;
        if (andDisplay)
        {
            displayCorrespondentImage();
            displayTitle();
            getSherlockActivity().invalidateOptionsMenu();
        }
    }

    public void display()
    {
        displayCorrespondentImage();
        displayTitle();
    }

    protected void displayCorrespondentImage()
    {
        if (correspondentImage != null)
        {
            RequestCreator picassoRequestCreator;
            if (correspondentProfile != null
                    && correspondentProfile.picture != null
                    && !correspondentProfile.picture.isEmpty())
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

    protected void displayTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (correspondentProfile != null)
        {
            String title =
                    userBaseDTOUtil.getLongDisplayName(getSherlockActivity(), correspondentProfile);
            Timber.d("Display title " + title);
            actionBar.setTitle(title);
        }
        else
        {
            actionBar.setTitle(R.string.loading_loading);
        }
    }

    protected void showNotEnoughMessagesLeftDialog()
    {
        String heroName = userBaseDTOUtil.getLongDisplayName(getActivity(), correspondentProfile);
        heroAlertDialogUtil.popAlertNoMoreMessageFollow(
                getActivity(),
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        showPaidFollow();
                    }
                },
                heroName);
    }

    @OnClick(R.id.private_message_status_container) protected void showPaidFollow()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        super.handleCommentPosted(discussionDTO);
        messageHeaderListCache.invalidateWithRecipient(correspondentId);
    }

    protected class AbstractPrivateMessageFragmentUserProfileListener
            implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e(error, "");
        }
    }

    protected class AbstractPrivateMessageFragmentOnMessageNotAllowedToSendListener
            implements PrivatePostCommentView.OnMessageNotAllowedToSendListener
    {
        @Override public void onMessageNotAllowedToSend()
        {
            showNotEnoughMessagesLeftDialog();
        }
    }

    protected class AbstractPrivateMessageFragmentUserFollowedListener
            extends BasePurchaseManagerUserFollowedListener
    {
        @Override public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            super.onUserFollowSuccess(userFollowed, currentUserProfileDTO);
        }

        @Override public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error)
        {
            super.onUserFollowFailed(userFollowed, error);
        }
    }
}
