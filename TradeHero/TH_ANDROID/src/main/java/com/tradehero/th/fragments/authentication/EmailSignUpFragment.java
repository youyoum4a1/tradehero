package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.view.MenuItem;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.settings.FocusableOnTouchListener;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.LocalyticsConstants;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Register using email.
 */
public class EmailSignUpFragment extends EmailSignInOrUpFragment implements View.OnClickListener
{
    private static final int REQUEST_GALLERY = 111;

    private ProfileInfoView profileView;

    private String selectedPath = null;
    private Bitmap imageBmp;

    @Inject LocalyticsSession localyticsSession;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DaggerUtils.inject(this);
    }

    @Override public int getDefaultViewId()
    {
        return R.layout.authentication_email_sign_up;
    }

    @Override protected void initSetup(View view)
    {
        FocusableOnTouchListener touchListener = new FocusableOnTouchListener();

        this.profileView = (ProfileInfoView) view.findViewById(R.id.profile_info);

        this.profileView.setOnTouchListenerOnFields(touchListener);
        this.profileView.addValidationListenerOnFields(this);

        this.signButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        this.signButton.setOnClickListener(this);
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.SignUp_Email);
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
            if (requestCode == REQUEST_GALLERY && data != null)

            {
                try
                {
                    Uri selectedImageUri = data.getData();
                    selectedPath = getPath(selectedImageUri);
                    System.out.println("image path......."
                            + selectedPath);
                    imageBmp = BitmapFactory.decodeFile(selectedPath);
                    System.out.println("image size1......."
                            + imageBmp.getByteCount());
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
                    }
                    else
                    {
                        THToast.show("Please chose picture from appropriate path");
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override public void onDestroyView()
    {
        if (this.profileView != null)
        {
            this.profileView.setOnTouchListenerOnFields(null);
            this.profileView.removeAllListenersOnFields();
            this.profileView.setNullOnFields();
        }
        this.profileView = null;

        if (this.signButton != null)
        {
            this.signButton.setOnClickListener(null);
        }
        this.signButton = null;

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
}




