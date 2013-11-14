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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.settings.FocusableOnTouchListener;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utills.Util;
import com.tradehero.th.utils.NetworkUtils;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class EmailSignUpFragment extends EmailSignInOrUpFragment implements View.OnClickListener
{
    public static final String TAG = EmailSignUpFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_EDIT_CURRENT_USER = EmailSignUpFragment.class.getName() + ".editCurrentUser";
    public static final String BUNDLE_KEY_SHOW_BUTTON_BACK = EmailSignUpFragment.class.getName() + ".showButtonBack";

    private ServerValidatedEmailText email;
    private ValidatedPasswordText password;
    private MatchingPasswordText confirmPassword;
    private ServerValidatedUsernameText displayName;
    private EditText firstName, lastName;
    private ProgressDialog progressDialog;

    private boolean editCurrentUser;
    private boolean showButtonBack;

    private int mWhichEdittext = 0;
    private CharSequence mText;
    private ImageView mOptionalImage;
    private View mView;
    private String selectedPath = null;
    private Bitmap imageBmp;
    private int mImagesize = 0;
    private Context mContext;
    private static final int REQUEST_GALLERY = 111;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public int getDefaultViewId()
    {
        return R.layout.authentication_email_sign_up;
    }

    @Override protected void initSetup(View view)
    {
        FocusableOnTouchListener touchListener = new FocusableOnTouchListener();

        email = (ServerValidatedEmailText) view.findViewById(R.id.authentication_sign_up_email);
        email.addListener(this);
        email.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..

        password = (ValidatedPasswordText) view.findViewById(R.id.authentication_sign_up_password);
        password.addListener(this);
        password.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..

        confirmPassword = (MatchingPasswordText) view.findViewById(R.id.authentication_sign_up_confirm_password);
        confirmPassword.addListener(this);
        confirmPassword.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..

        displayName = (ServerValidatedUsernameText) view.findViewById(R.id.authentication_sign_up_username);
        displayName.addListener(this);
        displayName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..

        firstName = (EditText) view.findViewById(R.id.et_firstname);
        firstName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        lastName = (EditText) view.findViewById(R.id.et_lastname);
        lastName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..

        signButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        signButton.setOnClickListener(this);

        //signupButton.setOnTouchListener(this);
        //mOptionalImage = (ImageView) view.findViewById(R.id.image_optional);
        //mOptionalImage.setOnClickListener(this);
        //mOptionalImage.setOnTouchListener(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        editCurrentUser = args != null && args.containsKey(BUNDLE_KEY_EDIT_CURRENT_USER) && args.getBoolean(BUNDLE_KEY_EDIT_CURRENT_USER);
        showButtonBack = args != null && args.containsKey(BUNDLE_KEY_SHOW_BUTTON_BACK) && args.getBoolean(BUNDLE_KEY_SHOW_BUTTON_BACK);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (editCurrentUser)
        {
            this.populateCurrentUser();
            onClickListener = this;
        }
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
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
        email.forceValidate();
        password.forceValidate();
        confirmPassword.forceValidate();
        displayName.forceValidate();
    }

    @Override public boolean areFieldsValid()
    {
        return email.isValid() && password.isValid() && confirmPassword.isValid() && displayName.isValid();
    }

    @Override protected Map<String, Object> getUserFormMap()
    {
        Map<String, Object> map = super.getUserFormMap();
        map.put(UserFormFactory.KEY_EMAIL, email.getText());
        map.put(UserFormFactory.KEY_PASSWORD, password.getText());
        map.put(UserFormFactory.KEY_PASSWORD_CONFIRM, confirmPassword.getText());
        map.put(UserFormFactory.KEY_DISPLAY_NAME, displayName.getText());
        map.put(UserFormFactory.KEY_FIRST_NAME, firstName.getText());
        map.put(UserFormFactory.KEY_LAST_NAME, lastName.getText());
        // TODO add profile picture
        return map;
    }

    @Override public void onDestroyView()
    {
        if (email != null)
        {
            email.removeAllListeners();
            email.setOnTouchListener(null);
            email = null;
        }
        if (password != null)
        {
            password.removeAllListeners();
            password.setOnTouchListener(null);
            password = null;
        }
        if (confirmPassword != null)
        {
            confirmPassword.removeAllListeners();
            confirmPassword.setOnTouchListener(null);
            confirmPassword = null;
        }
        if (displayName != null)
        {
            displayName.removeAllListeners();
            displayName.setOnTouchListener(null);
            displayName = null;
        }
        if (firstName != null)
        {
            firstName.setOnTouchListener(null);
            firstName = null;
        }
        if (lastName != null)
        {
            lastName.setOnTouchListener(null);
            lastName = null;
        }
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

                    Bitmap circleBitmap = Util.getRoundedShape(imageBmp);
                    mOptionalImage.setImageBitmap(
                            Util.getImagerotation(selectedPath, circleBitmap));
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
        this.firstName.setText(currentUserBase.firstName);
        this.lastName.setText(currentUserBase.lastName);
        this.displayName.setText(currentUserBase.displayName);
        this.displayName.setOriginalUsernameValue(currentUserBase.displayName);

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
        this.email.setText(emailValue);
        this.password.setText(passwordValue);
        this.confirmPassword.setText(passwordValue);
    }

    private void updateProfile(View view)
    {
        Util.dismissKeyBoard(getActivity(), view);
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
            progressDialog = ProgressDialog.show(
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
                    progressDialog.hide();
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




