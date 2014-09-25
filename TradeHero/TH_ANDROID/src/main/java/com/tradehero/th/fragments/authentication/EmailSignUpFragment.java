package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.FocusableOnTouchListener;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Register using email.
 */
public class EmailSignUpFragment extends EmailSignInOrUpFragment
{
    //java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
    private static final int REQUEST_GALLERY = new Random(new Date().getTime()).nextInt(Short.MAX_VALUE);
    private static final int REQUEST_CAMERA = new Random(new Date().getTime() + 1).nextInt(Short.MAX_VALUE);

    @Inject Analytics analytics;
    @Inject DashboardNavigator navigator;

    @InjectView(R.id.profile_info) ProfileInfoView profileView;
    @InjectView(R.id.authentication_sign_up_email) EditText emailEditText;

    @OnClick(R.id.authentication_back_button) void handleBackButtonClicked()
    {
        navigator.popFragment();
    }

    @OnClick(R.id.authentication_sign_up_button) void handleSignUpButtonClicked(View view)
    {
        THUser.clearCurrentUser();
        handleSignInOrUpButtonClicked(view);
    }

    @OnClick(R.id.image_optional) void handleImageOptionClicked()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpeg");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        HierarchyInjector.inject(this);
        analytics.tagScreen(AnalyticsConstants.Register_Form);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.RegisterFormScreen));
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Tap, AnalyticsConstants.Email));
    }

    @Override public int getDefaultViewId()
    {
        return R.layout.authentication_email_sign_up;
    }

    @Override protected void initSetup(View view)
    {
        FocusableOnTouchListener touchListener = new FocusableOnTouchListener();

        this.profileView.setOnTouchListenerOnFields(touchListener);
        this.profileView.addValidationListenerOnFields(this);
        this.profileView.setListener(new EmailSignUpProfileViewListener());
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        DeviceUtil.showKeyboardDelayed(emailEditText);
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
        ButterKnife.reset(this);
        super.onDestroyView();
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

    protected class EmailSignUpProfileViewListener implements ProfileInfoView.Listener
    {
        @Override public void onUpdateRequested()
        {
            // TODO
        }

        @Override public void onImageFromCameraRequested()
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.setType("image/jpeg");
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }

        @Override public void onImageFromLibraryRequested()
        {
            Intent libraryIntent = new Intent(Intent.ACTION_PICK);
            libraryIntent.setType("image/jpeg");
            startActivityForResult(libraryIntent, REQUEST_GALLERY);
        }
    }
}



