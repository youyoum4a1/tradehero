package android.tradehero.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EmailRegistrationActivity extends Activity implements OnClickListener{

	private EditText mEmailId,mPasword,mConfirmPassword,mDisplayName,mFirstName,mLastName;
	private Button mSignUpButton;

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

	}


	@Override
	public void onClick(View v) {


		if(v.getId()== R.id.btn_register){
			_handle_registration();
		}

	}


	private void _handle_registration() {

		/*if(!TextUtils.isEmpty(email_id.getText().toString()) || !TextUtils.isEmpty(pwd.getText().toString())
				|| !TextUtils.isEmpty(c_pwd.getText().toString()) || !TextUtils.isEmpty(disp_name.getText().toString())){

			//if(){}else{
				Util.show_toast(this, "Enter a valid email address.");
			//}

		}else{
			Util.show_toast(this, "Required Field should not left blank.");
		} */

	}

}
