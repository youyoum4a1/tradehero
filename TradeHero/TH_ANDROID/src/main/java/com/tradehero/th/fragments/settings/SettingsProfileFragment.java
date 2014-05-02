package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.NetworkUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import dagger.Lazy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.inject.Inject;
import org.json.JSONObject;
import timber.log.Timber;

public class SettingsProfileFragment extends DashboardFragment implements View.OnClickListener, ValidationListener
{
    //java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
    private static final int REQUEST_GALLERY = new Random(new Date().getTime()).nextInt(Short.MAX_VALUE);

    public static final String BUNDLE_KEY_SHOW_BUTTON_BACK = SettingsProfileFragment.class.getName() + ".showButtonBack";

    protected Button updateButton;
    private ProfileInfoView profileView;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    private MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> fetchUserProfileTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_email_sign_up, container, false);

        initSetup(view);
        setHasOptionsMenu(true);

        this.populateCurrentUser();
        return view;
    }

    protected void initSetup(View view)
    {
        FocusableOnTouchListener touchListener = new FocusableOnTouchListener();

        profileView = (ProfileInfoView) view.findViewById(R.id.profile_info);

        profileView.setOnTouchListenerOnFields(touchListener);
        profileView.addValidationListenerOnFields(this);
        profileView.setListener(createProfileViewListener());

        updateButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        updateButton.setText(R.string.update);
        updateButton.setOnClickListener(this);

        //signupButton.setOnTouchListener(this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Bundle args = getArguments();
        boolean showButtonBack = args != null && args.containsKey(BUNDLE_KEY_SHOW_BUTTON_BACK) && args.getBoolean(BUNDLE_KEY_SHOW_BUTTON_BACK);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (showButtonBack)
        {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        }
        else
        {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        }
        actionBar.setDisplayHomeAsUpEnabled(showButtonBack);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (getActivity() instanceof DashboardNavigatorActivity)
                {
                    ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator().popFragment();
                }
                else
                {
                    Timber.e("Activity is not a DashboardNavigatorActivity", new Exception());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView()
    {
        detachMiddleCallbackUpdateUserProfile();
        detachUserProfileFetchTask();
        if (profileView != null)
        {
            profileView.setOnTouchListenerOnFields(null);
            profileView.removeAllListenersOnFields();
            profileView.setNullOnFields();
            profileView.setListener(null);
        }
        profileView = null;
        if (updateButton != null)
        {
            updateButton.setOnClickListener(null);
        }
        updateButton = null;
        super.onDestroyView();
    }

    private void detachMiddleCallbackUpdateUserProfile()
    {
        if (middleCallbackUpdateUserProfile != null)
        {
            middleCallbackUpdateUserProfile.setPrimaryCallback(null);
        }
        middleCallbackUpdateUserProfile = null;
    }

    private void detachUserProfileFetchTask()
    {
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.setListener(null);
        }
        fetchUserProfileTask = null;
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
                updateProfile(view);
                break;
            case R.id.image_optional:
                askImageFromLibrary();
                break;
        }
    }

    protected void forceValidateFields()
    {
        profileView.forceValidateFields();
    }

    public boolean areFieldsValid()
    {
        return profileView.areFieldsValid();
    }

    protected Map<String, Object> getUserFormMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(UserFormFactory.KEY_TYPE, EmailAuthenticationProvider.EMAIL_AUTH_TYPE);
        profileView.populateUserFormMap(map);
        return map;
    }

    public JSONObject getUserFormJSON()
    {
        return new JSONObject(getUserFormMap());
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == REQUEST_GALLERY && data != null)
            {
                try
                {
                    handleDataFromLibrary(data);
                }
                catch (Exception e)
                {
                    Timber.e(e, "Failed to extract image from library");
                }
            }
            else if (requestCode == REQUEST_GALLERY)
            {
                Timber.e(new Exception("Got null data from library"), "");
            }
        }
        else
        {
            Timber.e(new Exception("Failed to get image from libray"), "");
        }
    }

    private void handleDataFromLibrary(Intent data)
    {
        Uri selectedImageUri = data.getData();
        String selectedPath = getPath(selectedImageUri);
        Bitmap imageBmp = BitmapFactory.decodeFile(selectedPath);
        BitmapFactory.Options options;
        if (imageBmp != null)
        {
            if (selectedPath.length() > 1000000)
            {
                options = new BitmapFactory.Options();
                options.inSampleSize = 4;
            }
            else
            {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
            }

            imageBmp = BitmapFactory.decodeFile(
                    selectedPath, options);

            if (profileView != null)
            {
                profileView.setNewImage(imageBmp);
            }
        }
        else
        {
            THToast.show("Please chose picture from appropriate path");
        }
    }

    private void populateCurrentUser()
    {
        detachUserProfileFetchTask();
        fetchUserProfileTask = userProfileCache.get().getOrFetch(currentUserId.toUserBaseKey(), false, createUserProfileCacheListener());
        fetchUserProfileTask.execute();
        this.profileView.populateCredentials(THUser.currentCredentials());
    }

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
            {
                profileView.populate(value);
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THToast.show(new THException(error));
            }
        };
    }

    private void updateProfile(View view)
    {
        DeviceUtil.dismissKeyboard(getActivity(), view);
        forceValidateFields();

        if (!NetworkUtils.isConnected(getActivity()))
        {
            THToast.show(R.string.network_error);
        }
        else if (!areFieldsValid())
        {
            THToast.show(R.string.validation_please_correct);
        }
        else
        {
            profileView.progressDialog = progressDialogUtil.show(
                    getActivity(),
                    R.string.alert_dialog_please_wait,
                    R.string.authentication_connecting_tradehero_only);
            EmailAuthenticationProvider.setCredentials(this.getUserFormJSON());

            UserFormDTO userFormDTO = UserFormFactory.create(getUserFormJSON());
            if (userFormDTO == null)
            {
                return;
            }

            detachMiddleCallbackUpdateUserProfile();
            middleCallbackUpdateUserProfile = userServiceWrapper.get().updateProfile(
                    currentUserId.toUserBaseKey(),
                    userFormDTO,
                    createUpdateUserProfileCallback());
        }
    }

    private THCallback<UserProfileDTO> createUpdateUserProfileCallback()
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
            {
                profileView.progressDialog.hide(); // Before otherwise it is reset
                THToast.show(R.string.settings_update_profile_successful);
                Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                navigator.popFragment();
            }

            @Override protected void failure(THException ex)
            {
                THToast.show(ex.getMessage());
            }
        };
    }

    public String getPath(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    protected void askImageFromLibrary()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpeg");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    @Override public void notifyValidation(ValidationMessage message)
    {
        if (message != null && !message.getStatus() && message.getMessage() != null)
        {
            THToast.show(message.getMessage());
        }
    }

    protected ProfileInfoView.Listener createProfileViewListener()
    {
        return new SettingsProfileViewListener();
    }

    protected class SettingsProfileViewListener implements ProfileInfoView.Listener
    {
        @Override public void onUpdateRequested()
        {
            // TODO
        }

        @Override public void onImageFromLibraryRequested()
        {
            askImageFromLibrary();
        }
    }
}




