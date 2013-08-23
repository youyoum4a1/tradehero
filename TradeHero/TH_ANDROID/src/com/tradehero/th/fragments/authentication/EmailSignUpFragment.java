package com.tradehero.th.fragments.authentication;

import com.tradehero.th.activities.DashboardActivity;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import com.tradehero.th.R;
import com.tradehero.th.application.App;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class EmailSignUpFragment extends Fragment
        implements OnClickListener, RequestTaskCompleteListener, OnFocusChangeListener
{

    private EditText mEmailId, mPasword,
            mConfirmPassword,
            mDisplayName, mFirstName,
            mLastName;
    private Button mSignUpButton;
    private ProgressDialog mProgressDialog;
    private LayoutInflater mLayoutInflater;
    private ImageView imgValidEMail, imgValidPwd,
            imgValidvConfirmPwd,             imgValidDisplyName;
    private int mWhichEdittext = 0;
    private CharSequence mText;
    private ImageView mOptionalImage;
    private View mView;
    private String selectedPath = null;
    private Bitmap imageBmp;
    private int mImagesize = 0;
    private Context mContext;
    private static final int REQUEST_GALLERY = 111;

    String lEmail;
    String lDName;
    String lFName;
    String lLName;
    String lPassword;
    String lConfirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.authentication_email_sign_up, container, false);
        initSetup(view);
        return view;
    }

    private void initSetup(View view)
    {
        mContext = getActivity();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEmailId = (EditText) view.findViewById(R.id.et_emailid);
        mPasword = (EditText) view.findViewById(R.id.et_password);
        mConfirmPassword = (EditText) view.findViewById(R.id.et_confirm_password);
        mDisplayName = (EditText) view.findViewById(R.id.et_display_name);
        mFirstName = (EditText) view.findViewById(R.id.et_firstname);
        mLastName = (EditText) view.findViewById(R.id.et_lasttname);
        mSignUpButton = (Button) view.findViewById(R.id.btn_register);
        imgValidEMail = (ImageView) view.findViewById(R.id.valid_mail_img);
        imgValidPwd = (ImageView) view.findViewById(R.id.valid_pwd_img);
        imgValidvConfirmPwd = (ImageView) view.findViewById(R.id.valid_cpwd_img);
        imgValidDisplyName = (ImageView) view.findViewById(R.id.valid_nmdisplay_img);

        mSignUpButton.setOnClickListener(this);
        //mSignUpButton.setOnTouchListener(this);
        mProgressDialog = new ProgressDialog(getActivity());
        mEmailId.setOnFocusChangeListener(this);
        mDisplayName.setOnFocusChangeListener(this);
        mPasword.setOnFocusChangeListener(this);
        mConfirmPassword.setOnFocusChangeListener(this);
        mProgressDialog.setMessage("Registering User");
        mOptionalImage = (ImageView) view.findViewById(R.id.image_optional);
        mOptionalImage.setOnClickListener(this);
        //mOptionalImage.setOnTouchListener(this);

        mEmailId.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (TextUtils.isEmpty(s))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }

                mText = s;
                new CheckValidation().execute();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        mConfirmPassword.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (TextUtils.isEmpty(s))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }

                mText = s;
                new CheckValidation().execute();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        mDisplayName.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (TextUtils.isEmpty(s))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }

                mText = s;

                if (NetworkStatus.getInstance().isConnected(getActivity()))
                {
                    new CheckValidation().execute();
                }
                else
                {
                    Toast.makeText(getActivity(), getResources().getString(R.string.network_error), 200).show();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        mPasword.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (TextUtils.isEmpty(s))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                mText = s;
                new CheckValidation().execute();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btn_register:
                Util.dismissKeyBoard(getActivity(), v);

                try
                {

                    if (NetworkStatus.getInstance().isConnected(getActivity()))
                    {
                        _handle_registration();
                    }
                    else
                    {
                        Util.show_toast(getActivity(),
                                getResources().getString(R.string.network_error));
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.image_optional:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpeg");
                startActivityForResult(intent, REQUEST_GALLERY);
                break;
        }
    }

    private void _handle_registration() throws JSONException
    {

        String lEmail = mEmailId.getText() != null ? mEmailId.getText().toString() : "";
        String lDName = mDisplayName.getText() != null ? mDisplayName.getText().toString() : "";
        String lFName = mFirstName.getText() != null ? mFirstName.getText().toString() : "";
        String lLName = mLastName.getText() != null ? mLastName.getText().toString() : "";
        String lPassword = mPasword.getText() != null ? mPasword.getText().toString() : "";
        String lConfirmPassword =
                mConfirmPassword.getText() != null ? mConfirmPassword.getText().toString() : "";
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
        mEmailId.setText("");
        mPasword.setText("");
        mConfirmPassword.setText("");
        mDisplayName.setText("");
        mFirstName.setText("");
        mLastName.setText("");
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

            imgValidEMail.setVisibility(View.VISIBLE);
            //imgInValidEmail.setVisibility(View.INVISIBLE);
        }
        else
        {
            //imgInValidEmail.setVisibility(View.VISIBLE);
            imgValidEMail.setVisibility(View.INVISIBLE);
        }
    }

    private void emailValidationPrework()
    {
        //imgInValidEmail.setVisibility(View.INVISIBLE);
        imgValidEMail.setVisibility(View.INVISIBLE);
    }

    private void nameDisplayPostwork(boolean result)
    {
        if (result)
        {

            //imgInvalidDisplyName.setVisibility(View.VISIBLE);
            imgValidDisplyName.setVisibility(View.INVISIBLE);
        }
        else
        {

            imgValidDisplyName.setVisibility(View.VISIBLE);
            //imgInvalidDisplyName.setVisibility(View.INVISIBLE);
        }
    }

    private void nameDisplayPrework()
    {
        //imgInvalidDisplyName.setVisibility(View.INVISIBLE);
        imgValidDisplyName.setVisibility(View.INVISIBLE);
    }

    private boolean confirmPwdValidationChecker()
    {

        if (mPasword.getText().toString().equals(mConfirmPassword.getText().toString())
                && mPasword.getText().toString().length() > 0)
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

            imgValidvConfirmPwd.setVisibility(View.VISIBLE);
            //imgInValidConfirmPassword.setVisibility(View.INVISIBLE);
        }
        else
        {

            //imgInValidConfirmPassword.setVisibility(View.VISIBLE);
            imgValidvConfirmPwd.setVisibility(View.INVISIBLE);
        }
    }

    private void confirmPwdValidationPrework()
    {
        imgValidvConfirmPwd.setVisibility(View.INVISIBLE);
        //imgInValidConfirmPassword.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1)
    {
        switch (arg0.getId())
        {
            case R.id.et_emailid:
                if (TextUtils.isEmpty(mDisplayName.getText().toString()) || TextUtils.isEmpty(
                        mPasword.getText().toString()) || TextUtils.isEmpty(
                        mConfirmPassword.getText().toString()))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    mSignUpButton.setBackgroundResource(
                            R.drawable.authentication_sign_in_button_xml);
                }
                mWhichEdittext = 1;

                break;
            case R.id.et_confirm_password:
                if (TextUtils.isEmpty(mEmailId.getText().toString()) || TextUtils.isEmpty(
                        mPasword.getText().toString()) || TextUtils.isEmpty(
                        mDisplayName.getText().toString()))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    mSignUpButton.setBackgroundResource(
                            R.drawable.authentication_sign_in_button_xml);
                }
                mWhichEdittext = 2;

                break;

            case R.id.et_password:
                if (TextUtils.isEmpty(mEmailId.getText().toString()) || TextUtils.isEmpty(
                        mDisplayName.getText().toString()) || TextUtils.isEmpty(
                        mConfirmPassword.getText().toString()))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    mSignUpButton.setBackgroundResource(
                            R.drawable.authentication_sign_in_button_xml);
                }
                mWhichEdittext = 2;
                break;

            case R.id.et_display_name:
                mWhichEdittext = 3;

                if (TextUtils.isEmpty(mEmailId.getText().toString()) || TextUtils.isEmpty(
                        mPasword.getText().toString()) || TextUtils.isEmpty(
                        mConfirmPassword.getText().toString()))
                {
                    mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);
                }
                else
                {
                    mSignUpButton.setBackgroundResource(
                            R.drawable.authentication_sign_in_button_xml);
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
				mSignUpButton.setBackgroundResource(R.drawable.rectangle_login);

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
				mSignUpButton.setBackgroundResource(R.drawable.authentication_sign_in_button_normal);

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
}




