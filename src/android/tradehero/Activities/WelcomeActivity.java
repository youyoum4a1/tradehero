package android.tradehero.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity implements OnClickListener{

	private Button mNewUser,mExistingUser;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		_initSetup();
	}

	private void _initSetup(){
		setContentView(R.layout.sign_in_sign_up_screen);
		mNewUser = (Button) findViewById(R.id.btn_newuser);
		mExistingUser = (Button) findViewById(R.id.btn_signin);
		mNewUser.setOnClickListener(this);
		mExistingUser.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_newuser:
			startActivity(new Intent(WelcomeActivity.this,IntialSignUpActivity .class)
			.putExtra("BOTTOM_LINE", "or sign up with an")
			.putExtra("HEADER_LINE", "New User")
			.putExtra("HEADER_LINEBELLOW", "sign up with")
			.putExtra("ACTIVITY_TYPE", IntialSignUpActivity.SIGNUP));
			break;
		case R.id.btn_signin:
			startActivity(new Intent(WelcomeActivity.this,IntialSignUpActivity.class)
			.putExtra("BOTTOM_LINE", "or sign in with an")
			.putExtra("HEADER_LINE", "Sign In")
			.putExtra("HEADER_LINEBELLOW", "sign in with")
			.putExtra("ACTIVITY_TYPE", IntialSignUpActivity.LOGIN));
			break;

		default:
			break;
		}

	}

}
