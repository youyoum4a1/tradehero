package android.tradehero.fragments;

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
import android.tradehero.activities.R;
import android.tradehero.activities.TradeHeroTabActivity;
import android.tradehero.application.App;
import android.tradehero.http.HttpRequestTask;
import android.tradehero.http.RequestFactory;
import android.tradehero.http.RequestTaskCompleteListener;
import android.tradehero.models.ProfileDTO;
import android.tradehero.models.Request;
import android.tradehero.networkstatus.NetworkStatus;
import android.tradehero.utills.Constants;
import android.tradehero.utills.PUtills;
import android.tradehero.utills.Util;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EmailRegistrationFragment extends Fragment implements OnClickListener,RequestTaskCompleteListener,OnFocusChangeListener{

	private EditText mEmailId,mPasword,
	mConfirmPassword,
	mDisplayName,mFirstName,
	mLastName;
	private Button mSignUpButton;
	private ProgressDialog mProgressDialog;
	private ProgressBar mConfirmPasswordProgressBar,mEMailProgressBar,
	mPaswordProgressbar,mDisplayNameProgressbar;
	private LayoutInflater mLayoutInflater;
	private ImageView imgValidEMail,imgInValidEmail,
	imgValidPwd,imgInValidPwd,
	imgValidvConfirmPwd,imgInValidConfirmPassword,
	imgValidDisplyName,imgInvalidDisplyName;
	private int  mWhichEdittext = 0;
	private CharSequence  mText;
	private ImageView mOptionalImage;
	private View mView;
	private String selectedPath= null;
	private Bitmap imageBmp;
	private int mImagesize = 0;
	private Context mContext;
	private static final int REQUEST_GALLERY = 111;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		//View view = null;
		View view = inflater.inflate(R.layout.user_register, container, false);
		initSetup(view);
		return view;
	}

	private void initSetup(View view)
	{
		mContext=getActivity();
		mLayoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mLayoutInflater.inflate(R.layout.topbar, null);
		TextView txt = (TextView) mView.findViewById(R.id.header_txt);
		txt.setText("Sign Up with Email");
		ViewGroup header = (ViewGroup)view.findViewById(R.id.wraper);
		header.addView(mView);
		mEmailId = (EditText)view.findViewById(R.id.et_emailid);
		mPasword = (EditText)view.findViewById(R.id.et_password);
		mConfirmPassword= (EditText)view.findViewById(R.id.et_confirm_password);
		mConfirmPasswordProgressBar= (ProgressBar)view.findViewById(R.id.pdilog_cpwd);
		mDisplayName = (EditText)view.findViewById(R.id.et_display_name);
		mFirstName = (EditText)view.findViewById(R.id.et_firstname);
		mLastName= (EditText)view.findViewById(R.id.et_lasttname);
		mSignUpButton = (Button)view.findViewById(R.id.btn_register);
		imgValidEMail = (ImageView)view.findViewById(R.id.valid_mail_img);
		imgInValidEmail= (ImageView)view.findViewById(R.id.invalid_mail_img);
		imgValidPwd = (ImageView)view.findViewById(R.id.valid_pwd_img);
		imgInValidPwd= (ImageView)view.findViewById(R.id.invalid_pwd_img);
		imgValidvConfirmPwd = (ImageView)view.findViewById(R.id.valid_cpwd_img);
		imgInValidConfirmPassword = (ImageView)view.findViewById(R.id.invalid_cpwd_img);
		imgValidDisplyName = (ImageView)view.findViewById(R.id.valid_nmdisplay_img);
		imgInvalidDisplyName = (ImageView)view.findViewById(R.id.invalid_nmdisplay_img);

		mSignUpButton.setOnClickListener(this);
		//mSignUpButton.setOnTouchListener(this);
		mProgressDialog= new ProgressDialog(getActivity());
		mEmailId.setOnFocusChangeListener(this);
		mDisplayName.setOnFocusChangeListener(this);
		mConfirmPassword.setOnFocusChangeListener(this);
		mProgressDialog.setMessage("Registering User");
		mEMailProgressBar= (ProgressBar)view.findViewById(R.id.pdilog_mail);
		mDisplayNameProgressbar= (ProgressBar)view.findViewById(R.id.pdilog_nmdisplay);
		mPaswordProgressbar= (ProgressBar)view.findViewById(R.id.pdilog_pwd);
		mOptionalImage = (ImageView)view.findViewById(R.id.image_optional);
		mOptionalImage.setOnClickListener(this);
		//mOptionalImage.setOnTouchListener(this);

		mEmailId.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mText = s;
				new CheckValidation().execute();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
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

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		mDisplayName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mText = s;

				if(NetworkStatus.getInstance().isConnected(getActivity()))
				{
					new CheckValidation().execute();
				}else{
					Toast.makeText(getActivity(), getResources().getString(R.string.network_error),200).show();

				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}




	@Override
	public void onClick(View v) {


		if(v.getId()== R.id.btn_register){
			try {			
				
				if(NetworkStatus.getInstance().isConnected(getActivity()))
				{
					_handle_registration();
					
				}else
				{
					Util.show_toast(getActivity(), getResources().getString(R.string.network_error));
				}
								
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(v.getId()==  R.id.image_optional)
		{
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

		if(TextUtils.isEmpty(lEmail) || TextUtils.isEmpty(lPassword) || TextUtils.isEmpty(lDName))
		{
			processRequest=false;
			//Util.showDIlog(getActivity(),getResources().getString(R.string.field_not_balnk));
			Util.show_toast(getActivity(),getResources().getString(R.string.field_not_balnk));

		}else if(! Util.email_valid.matcher(lEmail).matches())
		{
			processRequest=false;
			Util.show_toast(getActivity(),getResources().getString(R.string.email_validation_string));

		}else if(lPassword.length()<6)
		{
			processRequest=false;
			Util.show_toast(getActivity(),getResources().getString(R.string.password_validation_string));

		}else if(!lPassword.equals(lConfirmPassword))
		{
			processRequest=false;
			Util.show_toast(getActivity(),getResources().getString(R.string.password_validation_string));
		}

		if(processRequest)
		{
			HttpRequestTask  mRequestTask= new HttpRequestTask(this);
			RequestFactory mRF= new RequestFactory();
			@SuppressWarnings("deprecation")
			Request[] lRequests ={ mRF.getRegistrationThroughEmailRequest(mContext,lEmail,lDName, lFName, lLName,lPassword, lConfirmPassword)};
			mRequestTask.execute(lRequests);
			mProgressDialog.show();
		}

	}


	@Override
	public void onTaskComplete(JSONObject pResponseObject) {
		mProgressDialog.dismiss();
		if(pResponseObject!=null){

			System.out.println("result response----"+pResponseObject.toString());

			try {

				if(pResponseObject.has("Message"))
				{
					String msg = pResponseObject.getString("Message");
					Util.show_toast(getActivity(), msg);					
				}
				else
				{
//					Util.show_toast(getActivity(), pResponseObject.toString());
//					startActivity(new Intent(getActivity(),TradeHeroTabActivity.class).putExtra("DNAME", pResponseObject.optString("displayName")));
					Util.show_toast(getActivity(), pResponseObject.toString());
					
					//	JSONObject obj = pResponseObject.getJSONObject("profileDTO");

						ProfileDTO prof =	new PUtills(getActivity())._parseJson(pResponseObject);

						((App)getActivity().getApplication()).setProfileDTO(prof);
						startActivity(new Intent(getActivity(),TradeHeroTabActivity.class));
                        getActivity().finish();
					
					
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}


	@Override
	public void onErrorOccured(int pErrorCode, String pErrorMessage) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK)
		{
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
					if (imageBmp != null) 
					{
						if (selectedPath.length() > 1000000) 
						{
							options = new BitmapFactory.Options();
							options.inSampleSize = 4;

						} else
						{
							options = new BitmapFactory.Options();
							options.inSampleSize = 2;
						}

						imageBmp = BitmapFactory.decodeFile(
								selectedPath, options);

					} else
					{
						Util.show_toast(mContext, "Please chose picture from apropriate path");

					}

					Bitmap circleBitmap = Util.getRoundedShape(imageBmp);
					mOptionalImage.setImageBitmap(Util.getImagerotation(selectedPath,circleBitmap));

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
	}

	public String getPath(Uri uri) {

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor =getActivity().managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}


	//reset of every field

	private void _resetField() {
		mEmailId.setText("");
		mPasword.setText("");
		mConfirmPassword.setText("");
		mDisplayName.setText("");
		mFirstName.setText("");
		mLastName.setText("");
	}

	private  class CheckValidation extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if(mWhichEdittext == 1)
			{
				emailValidationPrework();
			}
			else if(mWhichEdittext == 2)
			{
				confirmPwdValidationPrework();
			}
			else if(mWhichEdittext == 3)
			{
				nameDisplayPrework();
			}
		}

		@Override
		protected Boolean doInBackground(String... arg0) {

			boolean mReturn = false; 
			if(mWhichEdittext == 1)
			{

				mReturn = emailValidationChecker(mText);

			}else if(mWhichEdittext == 2)
			{
				mReturn = confirmPwdValidationChecker();

			}else if(mWhichEdittext == 3)
			{

				mReturn = displayName_ValidationChecker(mText);

			}
			return mReturn;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if(mWhichEdittext == 1)
			{
				emailValidationPostwork(result);

			}else if(mWhichEdittext == 2)
			{
				confirmPwdValidationPostwork(result);
			}else if(mWhichEdittext == 3)
			{
				nameDisplayPostwork(result);
			}
		}

	}

	private boolean displayName_ValidationChecker(CharSequence text){

		String response = Util.httpGetConnection(Constants.CHECK_NAME_URL+URLEncoder.encode(text.toString()));
		System.out.println("disply name chk url ======"+Constants.CHECK_NAME_URL+text);

		if(response != null)
		{

			try {
				JSONObject jsonObj = new JSONObject(response);
				boolean result = jsonObj.getString("available").equalsIgnoreCase("true");
				if (result)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return true;
				}
				else
				{	
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					return false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return false;

	}


	private boolean emailValidationChecker(CharSequence text){

		if(Util.email_valid.matcher(text).matches())
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		else
		{	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
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



	private void nameDisplayPostwork(boolean result){

		mDisplayNameProgressbar.setVisibility(View.INVISIBLE);
		if(result){

			imgInvalidDisplyName.setVisibility(View.VISIBLE);
			imgValidDisplyName.setVisibility(View.INVISIBLE);

		}else{


			imgValidDisplyName.setVisibility(View.VISIBLE);
			imgInvalidDisplyName.setVisibility(View.INVISIBLE);
		}

	}

	private void nameDisplayPrework(){


		mDisplayNameProgressbar.setVisibility(View.VISIBLE);
		imgInvalidDisplyName.setVisibility(View.INVISIBLE);
		imgValidDisplyName.setVisibility(View.INVISIBLE);

	}

	private boolean confirmPwdValidationChecker(){


		if(mPasword.getText().toString().equals(mConfirmPassword.getText().toString()) && mPasword.getText().toString().length()>0)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}else
		{	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return false;
		}

	}

	private void confirmPwdValidationPostwork(boolean result){
		mConfirmPasswordProgressBar.setVisibility(View.INVISIBLE);

		if(result){

			imgValidvConfirmPwd.setVisibility(View.VISIBLE);
			imgInValidConfirmPassword.setVisibility(View.INVISIBLE);

		}else{

			imgInValidConfirmPassword.setVisibility(View.VISIBLE);
			imgValidvConfirmPwd.setVisibility(View.INVISIBLE);
		}

	}

	private void confirmPwdValidationPrework(){


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

		case R.id.et_display_name:
			mWhichEdittext = 3;

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
				mSignUpButton.setBackgroundResource(R.drawable.roundrectangle_signin);

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

}




