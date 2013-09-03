package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.application.App;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.http.HttpRequestTask;
import com.tradehero.th.http.RequestFactory;
import com.tradehero.th.http.RequestTaskCompleteListener;
import com.tradehero.th.models.ProfileDTO;
import com.tradehero.th.models.Request;
import com.tradehero.th.networkstatus.NetworkStatus;
import com.tradehero.th.utills.Constants;
import com.tradehero.th.utills.PUtills;
import com.tradehero.th.utills.PostData;
import com.tradehero.th.utills.Util;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;

public class EmailSignUpFragment extends AuthenticationFragment
        implements OnClickListener, RequestTaskCompleteListener, OnFocusChangeListener, ValidationListener
{
    private ServerValidatedEmailText email;
    private ValidatedPasswordText password;
    private MatchingPasswordText confirmPassword;
    private ServerValidatedUsernameText displayName;
    private EditText firstName, lastName;
    private Button signUpButton;

    private ProgressDialog mProgressDialog;
    //private ImageView imgValidPwd,
    //        imgValidvConfirmPwd, imgValidDisplyName;
    private int mWhichEdittext = 0;
    private CharSequence mText;
    private ImageView mOptionalImage;
    private View mView;
    private String selectedPath = null;
    private Bitmap imageBmp;
    private int mImagesize = 0;
    private Context mContext;
    private static final int REQUEST_GALLERY = 111;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_email_sign_up, container, false);

        initSetup(view);

        email.addListener(this);
        displayName.addListener(this);

        return view;

    }

    private void initSetup(View view)
    {
        email = (ServerValidatedEmailText) view.findViewById(R.id.authentication_sign_up_email);
        email.addListener(this);

        password = (ValidatedPasswordText) view.findViewById(R.id.authentication_sign_up_password);
        password.addListener(this);

        confirmPassword = (MatchingPasswordText) view.findViewById(R.id.authentication_sign_up_confirm_password);
        confirmPassword.addListener(this);

        displayName = (ServerValidatedUsernameText) view.findViewById(R.id.authentication_sign_up_username);
        displayName.addListener(this);

        firstName = (EditText) view.findViewById(R.id.et_firstname);
        lastName = (EditText) view.findViewById(R.id.et_lastname);

        signUpButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        signUpButton.setOnClickListener(this);

        //signupButton.setOnTouchListener(this);
        //mOptionalImage = (ImageView) view.findViewById(R.id.image_optional);
        //mOptionalImage.setOnClickListener(this);
        //mOptionalImage.setOnTouchListener(this);
    }

    @Override public void notifyValidation(ValidationMessage message)
    {
        if (message != null && !message.getStatus() && message.getMessage() != null)
        {
            THToast.show(message.getMessage());
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
                handleSignUpButtonClicked(view);
                break;
            case R.id.image_optional:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpeg");
                startActivityForResult(intent, REQUEST_GALLERY);
                break;
        }
    }

    private void handleSignUpButtonClicked (View view)
    {
        Util.dismissKeyBoard(getActivity(), view);

        try
        {
            if (!NetworkStatus.getInstance().isConnected(getActivity()))

            {
                Util.show_toast(getActivity(), getResources().getString(R.string.network_error));
            }
            else if (!areFieldsValid ())
            {
                THToast.show(R.string.validation_please_correct);
            }
            else
            {
                _handle_registration();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public boolean areFieldsValid ()
    {
        return email.isValid() && password.isValid() && confirmPassword.isValid() && displayName.isValid();
    }

    private void _handle_registration() throws JSONException
    {

        String lEmail = email.getText() != null ? email.getText().toString() : "";
        String lDName = displayName.getText() != null ? displayName.getText().toString() : "";
        String lFName = firstName.getText() != null ? firstName.getText().toString() : "";
        String lLName = lastName.getText() != null ? lastName.getText().toString() : "";
        String lPassword = password.getText() != null ? password.getText().toString() : "";
        String lConfirmPassword =
                confirmPassword.getText() != null ? confirmPassword.getText().toString() : "";
        boolean processRequest = true;

        if (TextUtils.isEmpty(lEmail) || TextUtils.isEmpty(lPassword) || TextUtils.isEmpty(lDName))
        {
            processRequest = false;
            //Util.showDIlog(getActivity(),getResources().getString(R.string.field_not_balnk));
            Util.show_toast(getActivity(), getResources().getString(R.string.field_not_balnk));
        }
        else if (!Util.email_valid.matcher(lEmail).matches())
        {
            processRequest = false;
            Util.show_toast(getActivity(),
                    getResources().getString(R.string.email_validation_string));
        }
        else if (lPassword.length() < 6)
        {
            processRequest = false;
            Util.show_toast(getActivity(),
                    getResources().getString(R.string.password_validation_string));
        }
        else if (!lPassword.equals(lConfirmPassword))
        {
            processRequest = false;
            Util.show_toast(getActivity(),
                    getResources().getString(R.string.password_validation_string));
        }

		/*if(processRequest)
        {
			new Imageupload().execute(lDName,lEmail,lFName,lLName,lPassword,lConfirmPassword);
		}
*/

        if (processRequest)
        {
            HttpRequestTask mRequestTask = new HttpRequestTask(this);
            RequestFactory mRF = new RequestFactory();
            @SuppressWarnings("deprecation")
            Request[] lRequests =
                    {mRF.getRegistrationThroughEmailRequest(mContext, lEmail, lDName, lFName,
                            lLName, lPassword, lConfirmPassword)};
            mRequestTask.execute(lRequests);
            mProgressDialog.show();
        }
    }

    @Override
    public void onTaskComplete(JSONObject pResponseObject)
    {
        mProgressDialog.dismiss();
        if (pResponseObject != null)
        {

            System.out.println("result response----" + pResponseObject.toString());

            try
            {

                if (pResponseObject.has("Message"))
                {
                    String msg = pResponseObject.getString("Message");
                    Util.CustomToast(getActivity(), msg);
                }
                else
                {
                    Util.show_toast(getActivity(), pResponseObject.toString());

                    //	JSONObject obj = pResponseObject.getJSONObject("profileDTO");

                    ProfileDTO prof = new PUtills(getActivity())._parseJson(pResponseObject);

                    ((App) getActivity().getApplication()).setProfileDTO(prof);
                    startActivity(new Intent(getActivity(), DashboardActivity.class));
                    getActivity().finish();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onErrorOccured(int pErrorCode, String pErrorMessage)
    {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
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
                        Util.show_toast(mContext, "Please chose picture from apropriate path");
                    }

                    Bitmap circleBitmap = Util.getRoundedShape(imageBmp);
                    mOptionalImage.setImageBitmap(
                            Util.getImagerotation(selectedPath, circleBitmap));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
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

    //reset of every field

    private void _resetField()
    {
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
        displayName.setText("");
        firstName.setText("");
        lastName.setText("");
    }

    @Override
    public AuthenticationMode getAuthenticationMode() {
        return AuthenticationMode.SignUp;
    }

    private class CheckValidation extends AsyncTask<String, Void, Boolean>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (mWhichEdittext == 1)
            {
                emailValidationPrework();
            }
            else if (mWhichEdittext == 2)
            {
                confirmPwdValidationPrework();
            }
            else if (mWhichEdittext == 3)
            {
                nameDisplayPrework();
            }
        }

        @Override
        protected Boolean doInBackground(String... arg0)
        {

            boolean mReturn = false;
            if (mWhichEdittext == 1)
            {

                mReturn = emailValidationChecker(mText);
            }
            else if (mWhichEdittext == 2)
            {
                mReturn = confirmPwdValidationChecker();
            }
            else if (mWhichEdittext == 3)
            {

                mReturn = displayName_ValidationChecker(mText);
            }
            return mReturn;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);

            if (mWhichEdittext == 1)
            {
                emailValidationPostwork(result);
            }
            else if (mWhichEdittext == 2)
            {
                confirmPwdValidationPostwork(result);
            }
            else if (mWhichEdittext == 3)
            {
                nameDisplayPostwork(result);
            }
        }
    }

    private boolean displayName_ValidationChecker(CharSequence text)
    {

        String response = Util.httpGetConnection(
                Constants.CHECK_NAME_URL + URLEncoder.encode(text.toString()));
        System.out.println("disply name chk url ======" + Constants.CHECK_NAME_URL + text);

        if (response != null)
        {

            try
            {
                JSONObject jsonObj = new JSONObject(response);
                boolean result = jsonObj.getString("available").equals("true");
                if (result)
                {
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    return true;
                }
                else
                {
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    return false;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean emailValidationChecker(CharSequence text)
    {

        if (Util.email_valid.matcher(text).matches())
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            return false;
        }
    }
    private void emailValidationPostwork(boolean result)
    {
        if (result)
        {

            //imgValidEMail.setVisibility(View.VISIBLE);
            //imgInValidEmail.setVisibility(View.INVISIBLE);
        }
        else
        {
            //imgInValidEmail.setVisibility(View.VISIBLE);
            //imgValidEMail.setVisibility(View.INVISIBLE);
        }
    }

    private void emailValidationPrework()
    {
        //imgInValidEmail.setVisibility(View.INVISIBLE);
        //imgValidEMail.setVisibility(View.INVISIBLE);
    }

    private void nameDisplayPostwork(boolean result)
    {
        if (result)
        {

            //imgInvalidDisplyName.setVisibility(View.VISIBLE);
            //imgValidDisplyName.setVisibility(View.INVISIBLE);
        }
        else
        {

            //imgValidDisplyName.setVisibility(View.VISIBLE);
            //imgInvalidDisplyName.setVisibility(View.INVISIBLE);
        }
    }

    private void nameDisplayPrework()
    {
        //imgInvalidDisplyName.setVisibility(View.INVISIBLE);
        //imgValidDisplyName.setVisibility(View.INVISIBLE);
    }

    private boolean confirmPwdValidationChecker()
    {

        if (password.getText().toString().equals(confirmPassword.getText().toString())
                && password.getText().toString().length() > 0)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            return false;
        }
    }

    private void confirmPwdValidationPostwork(boolean result)
    {
        if (result)
        {

            //imgValidvConfirmPwd.setVisibility(View.VISIBLE);
            //imgInValidConfirmPassword.setVisibility(View.INVISIBLE);
        }
        else
        {

            //imgInValidConfirmPassword.setVisibility(View.VISIBLE);
            //imgValidvConfirmPwd.setVisibility(View.INVISIBLE);
        }
    }

    private void confirmPwdValidationPrework()
    {
        //imgValidvConfirmPwd.setVisibility(View.INVISIBLE);
        //imgInValidConfirmPassword.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1)
    {
        switch (arg0.getId())
        {
            case R.id.authentication_sign_up_email:
                if (TextUtils.isEmpty(displayName.getText().toString()) || TextUtils.isEmpty(
                        password.getText().toString()) || TextUtils.isEmpty(
                        confirmPassword.getText().toString()))
                {
                    //signupButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    //signupButton.setBackgroundResource(R.drawable.authentication_sign_in_button_xml);
                }
                mWhichEdittext = 1;

                break;
            case R.id.authentication_sign_up_confirm_password:
                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(
                        password.getText().toString()) || TextUtils.isEmpty(
                        displayName.getText().toString()))
                {
                    ////signupButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    ////signupButton.setBackgroundResource(R.drawable.authentication_sign_in_button_xml);
                }
                mWhichEdittext = 2;

                break;

            case R.id.authentication_sign_up_password:
                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(
                        displayName.getText().toString()) || TextUtils.isEmpty(
                        confirmPassword.getText().toString()))
                {
                    ////signupButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    ////signupButton.setBackgroundResource(R.drawable.authentication_sign_in_button_xml);
                }
                mWhichEdittext = 2;
                break;

            case R.id.authentication_sign_up_username:
                mWhichEdittext = 3;

                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(
                        password.getText().toString()) || TextUtils.isEmpty(
                        confirmPassword.getText().toString()))
                {
                    //signupButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    //signupButton.setBackgroundResource(R.drawable.authentication_sign_in_button_xml);
                }

                break;

            default:
                break;
        }
    }

	/*@Override
    public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			switch (v.getId()) {
			case R.id.btn_register:
				//signupButton.setBackgroundResource(R.drawable.rectangle_login);

				break;
			case R.id.image_optional:

				mOptionalImage.setBackgroundResource(R.drawable.optional_imageselector);

				break;

			default:
				break;
			}

			break;

		case MotionEvent.ACTION_UP:


			switch (v.getId()) {
			case R.id.btn_register:
				//signupButton.setBackgroundResource(R.drawable.authentication_sign_in_button_normal);

				break;
			case R.id.image_optional:

				mOptionalImage.setBackgroundResource(R.drawable.layout_oval);

				break;

			default:
				break;
			}

			break;

		default:
			break;
		}


		return false;
	}

	 */

    class Imageupload extends AsyncTask<String, Void, String>
    {

        ProgressDialog dlg;

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            dlg = new ProgressDialog(getActivity());
            dlg.setMessage("Loading...");
            dlg.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            // TODO Auto-generated method stub

            String lDName = params[0];
            String lEmail = params[1];
            String lFName = params[2];
            String lLName = params[3];
            String lPassword = params[4];
            String lConfirmPassword = params[5];
            Bitmap myimg = imageBmp;

            String response = new PostData(getActivity()).httpMultipartCon(
                    "https://www.tradehero.mobi/api/SignupWithEmail", lDName, lEmail, lFName,
                    lLName, lPassword, lConfirmPassword);

            return response;
        }

        @Override
        protected void onPostExecute(String result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Util.show_toast(getActivity(), result);
            System.out.println("response for image upload----------" + result);
            dlg.dismiss();
        }
    }

    private class EmptyValidator implements TextWatcher
    {
        TextView textView = null;

        public EmptyValidator(TextView textView)
        {
            this.textView = textView;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

            if (TextUtils.isEmpty(s))
            {
                //signupButton.setBackgroundResource(R.drawable.rectangle_login);
            }

            mText = s;
            new CheckValidation().execute();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    }
}




