package android.tradehero.Activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener,RequestTaskCompleteListener{

	private TextView mForgotPassword;
	private Button mSignIn;
	private EditText inputEmailName,inputPassword;
	private ProgressDialog mProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_withemail_screen);
		mSignIn = (Button) findViewById(R.id.btn_login);
		mForgotPassword = (TextView) findViewById(R.id.txt_forgotpwd);
		inputEmailName=(EditText)findViewById(R.id.et_emailid_login);
		inputPassword=(EditText)findViewById(R.id.et_pwd_login);

		mProgressDialog= new ProgressDialog(this);
		mProgressDialog.setMessage("Logging In");
		mForgotPassword.setOnClickListener(this);
		mSignIn.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_login:

			String uname=inputEmailName.getText()!=null?inputEmailName.getText().toString():"";
			String pass =inputPassword.getText()!=null?inputPassword.getText().toString():"";
			if(uname.trim().length()>0 && pass.trim().length()>0){
				HttpRequestTask  mRequestTask= new HttpRequestTask(this);
				RequestFactory mRF= new RequestFactory();
				try {
					Request[] lRequests={ mRF.getLoginThroughEmail(uname,pass)};
					mRequestTask.execute(lRequests);
				} catch (JSONException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
				Util.show_toast(LoginActivity.this, "Log In");
			}

			mProgressDialog.show();

			break;
		case R.id.txt_forgotpwd:
			showForgotDIlog();

			break;

		default:
			break;
		}

	}

	private void showForgotDIlog(){

		inputEmailName= new EditText(this);

		AlertDialog.Builder dialog = new Builder(LoginActivity.this);
		dialog.setMessage("Please enter your email address.")
		.setCancelable(false)
		.setView(inputEmailName)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.cancel();

			}
		})
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {

				String email = inputEmailName.getText().toString();

				if(email==null || email.equals(""))
				{

					Util.show_toast(LoginActivity.this, "you must provied email name ? ");
				}
				else
				{
					Util.show_toast(LoginActivity.this, "Thank You ! ");
				}



			}
		});


		AlertDialog alrt = dialog.create();
		alrt.show();
	}

	@Override
	public void onTaskComplete(JSONObject pResponseObject) {
		// TODO Auto-generated method stub
		mProgressDialog.dismiss();
		Util.show_toast(this, "Login SuccessFul");

	}

	@Override
	public void onErrorOccured(int pErrorCode, String pErrorMessage) {
		// TODO Auto-generated method stub

	}

}

