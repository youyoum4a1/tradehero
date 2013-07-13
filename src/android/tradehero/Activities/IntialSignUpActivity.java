package android.tradehero.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class IntialSignUpActivity extends Activity implements OnClickListener{

	Button fbook,twitter,linkedin;
	TextView email;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		_initSetup();
	}

	private void _initSetup(){
		setContentView(R.layout.sign_in_screen);
		fbook = (Button) findViewById(R.id.btn_fbook);
		twitter = (Button) findViewById(R.id.btn_twitter);
		linkedin = (Button) findViewById(R.id.btn_linkedin);
		email = (TextView) findViewById(R.id.txt_email);
		
		fbook.setOnClickListener(this);
		twitter.setOnClickListener(this);
		linkedin.setOnClickListener(this);
		email.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_fbook:
			//startActivity(new Intent(New_user_Activity.this,Login_Activity.class));

			break;
		case R.id.btn_twitter:
			//startActivity(new Intent(New_user_Activity.this,New_user_Activity.class));

			break;
		case R.id.btn_linkedin:
			//startActivity(new Intent(New_user_Activity.this,Login_Activity.class));

			break;
		case R.id.txt_email:
			startActivity(new Intent(IntialSignUpActivity.this,EmailRegistrationActivity.class));

			break;

		default:
			break;
		}

	}

}
