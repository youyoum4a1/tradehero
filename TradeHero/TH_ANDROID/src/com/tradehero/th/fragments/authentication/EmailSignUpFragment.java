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
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.utills.PostData;
import com.tradehero.th.utills.Util;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import java.util.Map;

public class EmailSignUpFragment extends EmailSignInOrUpFragment
{
    private ServerValidatedEmailText email;
    private ValidatedPasswordText password;
    private MatchingPasswordText confirmPassword;
    private ServerValidatedUsernameText displayName;
    private EditText firstName, lastName;

    private ProgressDialog mProgressDialog;
    private int mWhichEdittext = 0;
    private CharSequence mText;
    private ImageView mOptionalImage;
    private View mView;
    private String selectedPath = null;
    private Bitmap imageBmp;
    private int mImagesize = 0;
    private Context mContext;
    private static final int REQUEST_GALLERY = 111;

    @Override public int getDefaultViewId ()
    {
        return R.layout.authentication_email_sign_up;
    }

    @Override protected void initSetup(View view)
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

        signButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        signButton.setOnClickListener(this);

        //signupButton.setOnTouchListener(this);
        //mOptionalImage = (ImageView) view.findViewById(R.id.image_optional);
        //mOptionalImage.setOnClickListener(this);
        //mOptionalImage.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
                handleSignInOrUpButtonClicked(view);
                break;
            case R.id.image_optional:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpeg");
                startActivityForResult(intent, REQUEST_GALLERY);
                break;
        }
    }

    @Override protected void forceValidateFields ()
    {
        email.forceValidate();
        password.forceValidate();
        confirmPassword.forceValidate();
        displayName.forceValidate();
    }

    @Override public boolean areFieldsValid ()
    {
        return email.isValid() && password.isValid() && confirmPassword.isValid() && displayName.isValid();
    }

    @Override protected Map<String, Object> getUserFormMap ()
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

    @Override
    public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignUp;
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
}




