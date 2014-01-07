package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.ViewStub;
import android.widget.Button;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.settings.FocusableOnTouchListener;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.NetworkUtils;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class EmailSignUpFragment extends EmailSignInOrUpFragment implements View.OnClickListener
{
    public static final String TAG = EmailSignUpFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_EDIT_CURRENT_USER = EmailSignUpFragment.class.getName() + ".editCurrentUser";
    public static final String BUNDLE_KEY_SHOW_BUTTON_BACK = EmailSignUpFragment.class.getName() + ".showButtonBack";

    private ProfileInfoView profileView;

    private boolean editCurrentUser;
    private boolean showButtonBack;

    private int mWhichEdittext = 0;
    private CharSequence mText;
    private String selectedPath = null;
    private Bitmap imageBmp;
    private int mImagesize = 0;
    private Context mContext;
    private static final int REQUEST_GALLERY = 111;

    @Override public int getDefaultViewId()
    {
        return R.layout.authentication_email_sign_up;
    }

    @Override protected void initSetup(View view)
    {
        FocusableOnTouchListener touchListener = new FocusableOnTouchListener();

        profileView = (ProfileInfoView) view.findViewById(R.id.profile_info);

        profileView.setOnTouchListenerOnFields(touchListener);
        profileView.addValidationListenerOnFields(this);

        signButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        signButton.setOnClickListener(this);

        //signupButton.setOnTouchListener(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        editCurrentUser = args != null && args.containsKey(BUNDLE_KEY_EDIT_CURRENT_USER) && args.getBoolean(BUNDLE_KEY_EDIT_CURRENT_USER);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (editCurrentUser)
        {
            this.populateCurrentUser();
            onClickListener = this;
        }
        return view;
    }

    @Override protected void inflateStubs(View view)
    {
        super.inflateStubs(view);
        ((ViewStub) view.findViewById(R.id.profile_info_stub)).inflate();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Bundle args = getArguments();
        showButtonBack = args != null && args.containsKey(BUNDLE_KEY_SHOW_BUTTON_BACK) && args.getBoolean(BUNDLE_KEY_SHOW_BUTTON_BACK);

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
                    THLog.e(TAG, "Activity is not a DashboardNavigatorActivity", new Exception());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
                if (editCurrentUser)
                {
                    updateProfile(view);
                }
                else
                {
                    handleSignInOrUpButtonClicked(view);
                }
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
        profileView.forceValidateFields();
    }

    @Override public boolean areFieldsValid()
    {
        return profileView.areFieldsValid();
    }

    @Override protected Map<String, Object> getUserFormMap()
    {
        Map<String, Object> map = super.getUserFormMap();
        map.put(UserFormFactory.KEY_EMAIL, profileView.email.getText());
        map.put(UserFormFactory.KEY_PASSWORD, profileView.password.getText());
        map.put(UserFormFactory.KEY_PASSWORD_CONFIRM, profileView.confirmPassword.getText());
        map.put(UserFormFactory.KEY_DISPLAY_NAME, profileView.displayName.getText());
        map.put(UserFormFactory.KEY_FIRST_NAME, profileView.firstName.getText());
        map.put(UserFormFactory.KEY_LAST_NAME, profileView.lastName.getText());
        // TODO add profile picture
        return map;
    }

    @Override public void onDestroyView()
    {
        if (profileView != null)
        {
            profileView.setOnTouchListenerOnFields(null);
            profileView.removeAllListenersOnFields();
            profileView.email = null;
            profileView.password = null;
            profileView.confirmPassword = null;
            profileView.displayName = null;
            profileView.firstName = null;
            profileView.lastName = null;
        }
        profileView = null;
        super.onDestroyView();
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

    private void populateCurrentUser()
    {
        UserBaseDTO currentUserBase = THUser.getCurrentUserBase();

        this.signButton.setText(getResources().getString(R.string.settings_update_profile));
        this.profileView.firstName.setText(currentUserBase.firstName);
        this.profileView.lastName.setText(currentUserBase.lastName);
        this.profileView.displayName.setText(currentUserBase.displayName);
        this.profileView.displayName.setOriginalUsernameValue(currentUserBase.displayName);

        JSONObject credentials = THUser.currentCredentials();
        String emailValue = null, passwordValue = null;
        try
        {
            emailValue = credentials.getString("email");
            passwordValue = credentials.getString("password");
        } catch (JSONException e)
        {
            THLog.e(TAG, "populateCurrentUser", e);
        }
        this.profileView.email.setText(emailValue);
        this.profileView.password.setText(passwordValue);
        this.profileView.confirmPassword.setText(passwordValue);
    }

    private void updateProfile(View view)
    {
        DeviceUtil.dismissKeyBoard(getActivity(), view);
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
            profileView.progressDialog = ProgressDialog.show(
                    getSherlockActivity(),
                    Application.getResourceString(R.string.please_wait),
                    Application.getResourceString(R.string.connecting_tradehero_only),
                    true);
            EmailAuthenticationProvider.setCredentials(this.getUserFormJSON());
            THUser.updateProfile(getUserFormJSON(), new LogInCallback()
            {
                @Override public void done(UserBaseDTO user, THException ex)
                {
                    if (ex == null)
                    {
                        THToast.show(R.string.settings_update_profile_successful);
                        Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                        navigator.popFragment();
                    }
                    else
                    {
                        THToast.show(ex.getMessage());
                    }
                    profileView.progressDialog.hide();
                }

                @Override public void onStart()
                {
                }
            });
        }
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




