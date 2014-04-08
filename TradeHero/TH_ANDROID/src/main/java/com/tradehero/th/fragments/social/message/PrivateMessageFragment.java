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
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.UserProfileView;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.user.UserProfileCache;
import timber.log.Timber;

import javax.inject.Inject;

public class PrivateMessageFragment extends DashboardFragment
{
    public static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY = PrivateMessageFragment.class.getName() + ".correspondentUserBaseKey";

    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;

    @Inject UserProfileCache userProfileCache;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileCacheTask;
    private UserBaseKey correspondentId;
    private UserProfileDTO correspondentProfile;

    @InjectView(R.id.message_list_view) ListView messageListView;
    @InjectView(R.id.button_send) View buttonSend;
    @InjectView(R.id.typing_message_content) EditText messageToSend;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        correspondentId = new UserBaseKey(getArguments().getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_screen, container, false);
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
        displayCorrespondentImage();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchCorrespondentProfile();
        // TODO Fetch existing messages
    }

    @Override public void onDestroyView()
    {
        detachUserProfileTask();
        // TODO detach messages tasks
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

    private void fetchCorrespondentProfile()
    {
        detachUserProfileTask();
        userProfileCacheTask = userProfileCache.getOrFetch(correspondentId, userProfileCacheListener);
        userProfileCacheTask.execute();
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

    private void displayCorrespondentImage()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        RequestCreator picassoRequestCreator;
        ImageView correspondentImage = (ImageView) actionBar.getCustomView().findViewById(R.id.correspondent_picture);
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
}
