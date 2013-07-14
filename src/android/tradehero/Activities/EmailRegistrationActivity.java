package android.tradehero.Activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.tradehero.Http.HttpRequestTask;
import android.tradehero.Http.RequestFactory;
import android.tradehero.Http.RequestTaskCompleteListener;
import android.tradehero.Models.Request;
import android.tradehero.Utills.Util;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EmailRegistrationActivity extends Activity implements OnClickListener,RequestTaskCompleteListener{

	private EditText mEmailId,mPasword,mConfirmPassword,mDisplayName,mFirstName,mLastName;
	private Button mSignUpButton;
    private ProgressDialog mProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initSetup();
	}


	private void initSetup(){
		setContentView(R.layout.user_register);
		mEmailId = (EditText) findViewById(R.id.et_emailid);
		mPasword = (EditText) findViewById(R.id.et_password);
		mConfirmPassword= (EditText) findViewById(R.id.et_confirm_password);
		mDisplayName = (EditText) findViewById(R.id.et_display_name);
		mFirstName = (EditText) findViewById(R.id.et_firstname);
		mLastName= (EditText) findViewById(R.id.et_lasttname);
		mSignUpButton = (Button) findViewById(R.id.btn_register);
		mSignUpButton.setOnClickListener(this);
		mProgressDialog= new ProgressDialog(this);
		mProgressDialog.setMessage("Registering User");
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

	}


	private void _handle_registration() throws JSONException {
			
		HttpRequestTask  mRequestTask= new HttpRequestTask(this);
        RequestFactory mRF= new RequestFactory();
        String lEmail= mEmailId.getText()!=null?mEmailId.getText().toString():"";
        String lDName= mDisplayName.getText()!=null?mDisplayName.getText().toString():"";
        String lFName= mFirstName.getText()!=null?mFirstName.getText().toString():"";
        String lLName= mLastName.getText()!=null?mLastName.getText().toString():"";
        String lPassword= mPasword.getText()!=null?mPasword.getText().toString():"";
        String lConfirmPassword= mConfirmPassword.getText()!=null?mConfirmPassword.getText().toString():"";
        Request[] lRequests ={ mRF.getRegistrationThroughEmailRequest(lEmail, lDName, lFName, lLName,lPassword, lConfirmPassword)};
        mRequestTask.execute(lRequests);
        mProgressDialog.show();
		/*if(!TextUtils.isEmpty(email_id.getText().toString()) || !TextUtils.isEmpty(pwd.getText().toString())
				|| !TextUtils.isEmpty(c_pwd.getText().toString()) || !TextUtils.isEmpty(disp_name.getText().toString())){

			//if(){}else{
				Util.show_toast(this, "Enter a valid email address.");
			//}

		}else{
			Util.show_toast(this, "Required Field should not left blank.");
		} */

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

}
