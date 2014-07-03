package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.settings.FocusableOnTouchListener;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Register using email.
 */
public class EmailSignUpFragment extends EmailSignInOrUpFragment implements View.OnClickListener
{
    //java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
    private static final int REQUEST_GALLERY = new Random(new Date().getTime()).nextInt(Short.MAX_VALUE);
    private static final int REQUEST_CAMERA = new Random(new Date().getTime() + 1).nextInt(Short.MAX_VALUE);

    private ProfileInfoView profileView;
    private EditText emailEditText;
    private ImageView backButton;

    @Inject THLocalyticsSession localyticsSession;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DaggerUtils.inject(this);
        List custom_dimensions = new ArrayList();
        custom_dimensions.add(Constants.TAP_STREAM_TYPE.name());
        localyticsSession.open(custom_dimensions);
        localyticsSession.tagScreen(LocalyticsConstants.Register_Form);
        localyticsSession.tagEvent(LocalyticsConstants.RegisterFormScreen);
        localyticsSession.tagEventMethod(LocalyticsConstants.SignUp_Tap, LocalyticsConstants.Email);
    }

    @Override public int getDefaultViewId()
    {
        return R.layout.authentication_email_sign_up;
    }

    @Override protected void initSetup(View view)
    {
        FocusableOnTouchListener touchListener = new FocusableOnTouchListener();

        this.profileView = (ProfileInfoView) view.findViewById(R.id.profile_info);
        this.emailEditText = (EditText) view.findViewById(R.id.authentication_sign_up_email);

        this.profileView.setOnTouchListenerOnFields(touchListener);
        this.profileView.addValidationListenerOnFields(this);
        this.profileView.setListener(createProfileViewListener());

        this.signButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        this.signButton.setOnClickListener(this);

        backButton = (ImageView) view.findViewById(R.id.authentication_by_sign_up_back_button);
        backButton.setOnClickListener(onClickListener);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        DeviceUtil.showKeyboardDelayed(emailEditText);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (getActivity() instanceof DashboardNavigatorActivity)
                {
                    ((NavigatorActivity) getActivity()).getNavigator().popFragment();
                }
                else
                {
                    Timber.e("Activity is not a DashboardNavigatorActivity", new Exception());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if ((requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY) && data != null)
            {
                try
                {
                    if (profileView != null)
                    {
                        profileView.handleDataFromLibrary(data);
                    }
                }
                catch (OutOfMemoryError e)
                {
                    THToast.show(R.string.error_decode_image_memory);
                }
                catch (Exception e)
                {
                    THToast.show(R.string.error_fetch_image_library);
                    Timber.e(e, "Failed to extract image from library");
                }
            }
            else if (requestCode == REQUEST_GALLERY)
            {
                Timber.e(new Exception("Got null data from library"), "");
            }
        }
        else if (resultCode != Activity.RESULT_CANCELED)
        {
            Timber.e(new Exception("Failed to get image from libray, resultCode: " + resultCode), "");
        }
    }

    @Override public void onDestroyView()
    {
        if (this.profileView != null)
        {
            this.profileView.setOnTouchListenerOnFields(null);
            this.profileView.removeAllListenersOnFields();
            this.profileView.setNullOnFields();
            this.profileView.setListener(null);
        }
        this.profileView = null;

        if (this.signButton != null)
        {
            this.signButton.setOnClickListener(null);
        }
        this.signButton = null;
        if (backButton != null)
        {
            backButton.setOnClickListener(null);
            backButton = null;
        }
        List custom_dimensions = new ArrayList();
        custom_dimensions.add(Constants.TAP_STREAM_TYPE.name());
        localyticsSession.close(custom_dimensions);
        localyticsSession.upload();
        super.onDestroyView();
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
                //clear old user info
                THUser.clearCurrentUser();
                handleSignInOrUpButtonClicked(view);
                break;
            case R.id.image_optional:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpeg");
                startActivityForResult(intent, REQUEST_GALLERY);
                break;
        }
    }

    @Override protected void forceValidateFields()
    {
        this.profileView.forceValidateFields();
    }

    @Override public boolean areFieldsValid()
    {
        return this.profileView.areFieldsValid();
    }

    @Override protected Map<String, Object> getUserFormMap()
    {
        Map<String, Object> map = super.getUserFormMap();
        this.profileView.populateUserFormMap(map);
        return map;
    }

    protected void askImageFromLibrary()
    {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/jpeg");
        startActivityForResult(libraryIntent, REQUEST_GALLERY);
    }

    protected void askImageFromCamera()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.setType("image/jpeg");
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
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

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignUpWithEmail;
    }

    protected ProfileInfoView.Listener createProfileViewListener()
    {
        return new EmailSignUpProfileViewListener();
    }

    protected class EmailSignUpProfileViewListener implements ProfileInfoView.Listener
    {
        @Override public void onUpdateRequested()
        {
            // TODO
        }

        @Override public void onImageFromCameraRequested()
        {
            askImageFromCamera();
        }

        @Override public void onImageFromLibraryRequested()
        {
            askImageFromLibrary();
        }
    }
}



