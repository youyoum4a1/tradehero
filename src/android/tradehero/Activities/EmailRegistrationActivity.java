package android.tradehero.Activities;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
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
import android.tradehero.Http.HttpRequestTask;
import android.tradehero.Http.RequestFactory;
import android.tradehero.Http.RequestTaskCompleteListener;
import android.tradehero.Models.Request;
import android.tradehero.Utills.Util;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EmailRegistrationActivity extends Activity implements OnClickListener,RequestTaskCompleteListener,OnFocusChangeListener{

	private EditText mEmailId,mPasword,mConfirmPassword,mDisplayName,mFirstName,mLastName;
	private Button mSignUpButton;
	private ProgressDialog mProgressDialog;
	private ProgressBar mConfirmPasswordProgressBar,mEMailProgressBar;
	private LayoutInflater mLayoutInflater;
	private ImageView imgValidEMail,imgInValidEmail,imgValidPwd,imgInValidPwd,imgValidvConfirmPwd,imgInValidConfirmPassword;
	private int  mWhichEdittext = 0;
	private CharSequence  mText;
	private ImageView mOptionalImage;
	private View mView;
	private String selectedPath= null;
	private Bitmap imageBmp;
	private int mImagesize = 0;
	private Context mContext;
	private static final int REQUEST_GALLERY = 1000001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initSetup();
	}


	private void initSetup(){
		setContentView(R.layout.user_register);
		mContext=this;
		mLayoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mLayoutInflater.inflate(R.layout.topbar, null);
		TextView txt = (TextView) mView.findViewById(R.id.header_txt);
		txt.setText("Sign Up with Email");
		ViewGroup header = (ViewGroup)findViewById(R.id.wraper);
		header.addView(mView);
		mEmailId = (EditText) findViewById(R.id.et_emailid);
		mPasword = (EditText) findViewById(R.id.et_password);
		mConfirmPassword= (EditText) findViewById(R.id.et_confirm_password);
		mConfirmPasswordProgressBar= (ProgressBar) findViewById(R.id.pdilog_cpwd);
		mDisplayName = (EditText) findViewById(R.id.et_display_name);
		mFirstName = (EditText) findViewById(R.id.et_firstname);
		mLastName= (EditText) findViewById(R.id.et_lasttname);
		mSignUpButton = (Button) findViewById(R.id.btn_register);
		imgValidEMail = (ImageView) findViewById(R.id.valid_mail_img);
		imgInValidEmail= (ImageView) findViewById(R.id.invalid_mail_img);
		imgValidPwd = (ImageView) findViewById(R.id.valid_pwd_img);
		imgInValidPwd= (ImageView) findViewById(R.id.invalid_pwd_img);
		imgValidvConfirmPwd = (ImageView) findViewById(R.id.valid_cpwd_img);
		imgInValidConfirmPassword = (ImageView) findViewById(R.id.invalid_cpwd_img);
		mSignUpButton.setOnClickListener(this);
		mProgressDialog= new ProgressDialog(this);
		mEmailId.setOnFocusChangeListener(this);
		mConfirmPassword.setOnFocusChangeListener(this);
		mProgressDialog.setMessage("Registering User");
		mEMailProgressBar= (ProgressBar) findViewById(R.id.pdilog_mail);
		mOptionalImage = (ImageView) findViewById(R.id.image_optional);
		mOptionalImage.setOnClickListener(this);
		mEmailId.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				mText = s;
				new CheckValidation().execute();


			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {


			}
		});
		mConfirmPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				mText = s;
				new CheckValidation().execute();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}
	private static final Pattern email_valid = Pattern.compile(
			"^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
			);

	private void showDIlog(String mssg){
		AlertDialog.Builder dialog = new Builder(EmailRegistrationActivity.this);
		dialog.setMessage(mssg)
		.setCancelable(false)
		.setIcon(R.id.logo_img)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {

				dialog.cancel();

			}
		});
		AlertDialog alrt = dialog.create();
		alrt.show();
	}


	@Override
	public void onClick(View v) {


		if(v.getId()== R.id.btn_register){
			try {
				_handle_registration();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(v.getId()==  R.id.image_optional){
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_GALLERY);
		}

	}


	private void _handle_registration() throws JSONException {

		String lEmail= mEmailId.getText()!=null?mEmailId.getText().toString():"";
		String lDName= mDisplayName.getText()!=null?mDisplayName.getText().toString():"";
		String lFName= mFirstName.getText()!=null?mFirstName.getText().toString():"";
		String lLName= mLastName.getText()!=null?mLastName.getText().toString():"";
		String lPassword= mPasword.getText()!=null?mPasword.getText().toString():"";
		String lConfirmPassword= mConfirmPassword.getText()!=null?mConfirmPassword.getText().toString():"";
		boolean processRequest=true;
		if(TextUtils.isEmpty(lEmail) || TextUtils.isEmpty(lPassword) || TextUtils.isEmpty(lFName) || TextUtils.isEmpty(lDName)){

			processRequest=false;

			showDIlog("Required Field should not left blank.");

		}else if(!email_valid.matcher(lEmail).matches()){
			processRequest=false;
			showDIlog("Enter a valid email address.");

		}else if(lPassword.length()<6){
			processRequest=false;
			//Constants.show_toast(this, "Password should not be less than six charecter.");
			showDIlog("Password should not be less than six charecter.");

		}else if(!lPassword.equals(mConfirmPassword)){
			processRequest=false;
			showDIlog("Password should be match.");
			//handle registration



		}
		if(processRequest){
			HttpRequestTask  mRequestTask= new HttpRequestTask(this);
			RequestFactory mRF= new RequestFactory();

			Request[] lRequests ={ mRF.getRegistrationThroughEmailRequest(lEmail, lDName, lFName, lLName,lPassword, lConfirmPassword)};
			mRequestTask.execute(lRequests);
			mProgressDialog.show();
		}

	}


	@Override
	public void onTaskComplete(JSONObject pResponseObject) {
		// TODO Auto-generated method stub
		mProgressDialog.dismiss();
		if(pResponseObject!=null){
			Util.show_toast(this, pResponseObject.toString());
		}
	}


	@Override
	public void onErrorOccured(int pErrorCode, String pErrorMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			if (requestCode == REQUEST_GALLERY && data != null)

			{
				try {
					Uri selectedImageUri = data.getData();

					selectedPath = getPath(selectedImageUri);
					System.out.println("image path......."
							+ selectedPath);

					imageBmp = BitmapFactory.decodeFile(selectedPath);
					System.out.println("image size1......."
							+ imageBmp.getByteCount());

					BitmapFactory.Options options;
					if (imageBmp != null) {

						if (selectedPath.length() > 1000000) {
							options = new BitmapFactory.Options();
							options.inSampleSize = 4;

						} else {
							options = new BitmapFactory.Options();
							options.inSampleSize = 2;

						}

						imageBmp = BitmapFactory.decodeFile(
								selectedPath, options);



					} else {
						Util.show_toast(mContext, "Please chose picture from apropriate path");

					}



					mOptionalImage.setImageBitmap(imageBmp);


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };

		Cursor cursor = managedQuery(uri, projection, null, null, null);

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);

	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	private void _resetField() {
		mEmailId.setText("");
		mPasword.setText("");
		mConfirmPassword.setText("");
		mDisplayName.setText("");
		mFirstName.setText("");
		mLastName.setText("");

		/*mail_prg.setVisibility(View.INVISIBLE);
		img_vmail.setVisibility(View.INVISIBLE);
		img_invmail.setVisibility(View.INVISIBLE);


		cpwd_prg.setVisibility(View.INVISIBLE);
		img_vcpwd.setVisibility(View.INVISIBLE);
		img_invcpw.setVisibility(View.INVISIBLE);*/


	}

	private  class CheckValidation extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			if(mWhichEdittext == 1){

				emailValidationPrework();
			}

			else if(mWhichEdittext == 2){

				cpwdValidationPrework();
			}
		}

		@Override
		protected Boolean doInBackground(String... arg0) {

			boolean mReturn = false; 
			// TODO Auto-generated method stub
			if(mWhichEdittext == 1){

				mReturn = emailValidationChecker(mText);
			}else if(mWhichEdittext == 2)
			{
				mReturn = cpwdValidationChecker();
			}


			return mReturn;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if(mWhichEdittext == 1){

				emailValidationPostwork(result);
			}

			else if(mWhichEdittext == 2){

				cpwdValidationPostwork(result);
			}
		}



	}


	private boolean emailValidationChecker(CharSequence text){



		if(email_valid.matcher(text).matches()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}else{	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return false;
		}

	}

	private void emailValidationPostwork(boolean result){

		mEMailProgressBar.setVisibility(View.INVISIBLE);
		if(result){

			imgValidEMail.setVisibility(View.VISIBLE);
			imgInValidEmail.setVisibility(View.INVISIBLE);

		}else{

			imgInValidEmail.setVisibility(View.VISIBLE);
			imgValidEMail.setVisibility(View.INVISIBLE);
		}

	}

	private void emailValidationPrework(){


		mEMailProgressBar.setVisibility(View.VISIBLE);
		imgInValidEmail.setVisibility(View.INVISIBLE);
		imgValidEMail.setVisibility(View.INVISIBLE);

	}


	private boolean cpwdValidationChecker(){


		if(mPasword.getText().toString().equals(mConfirmPassword.getText().toString()) && mPasword.getText().toString().length()>0){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}else{	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return false;
		}

	}

	private void cpwdValidationPostwork(boolean result){
		mConfirmPasswordProgressBar.setVisibility(View.INVISIBLE);

		if(result){

			imgValidvConfirmPwd.setVisibility(View.VISIBLE);
			imgInValidConfirmPassword.setVisibility(View.INVISIBLE);

		}else{

			imgInValidConfirmPassword.setVisibility(View.VISIBLE);
			imgValidvConfirmPwd.setVisibility(View.INVISIBLE);
		}

	}

	private void cpwdValidationPrework(){


		mConfirmPasswordProgressBar.setVisibility(View.VISIBLE);
		imgValidvConfirmPwd.setVisibility(View.INVISIBLE);
		imgValidvConfirmPwd.setVisibility(View.INVISIBLE);

	}
	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.et_emailid:

			mWhichEdittext = 1;

			break;
		case R.id.et_confirm_password:

			mWhichEdittext = 2;

			break;

		case R.id.et_password:

			break;

		default:
			break;
		}

	}


}
