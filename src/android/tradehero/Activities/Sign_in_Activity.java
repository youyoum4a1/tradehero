package android.tradehero.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Sign_in_Activity extends Activity implements OnClickListener{

	Button new_user,existing_user;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		_initSetup();
	}

	private void _initSetup(){
		setContentView(R.layout.sign_in_sign_up_screen);
		new_user = (Button) findViewById(R.id.btn_newuser);
		existing_user = (Button) findViewById(R.id.btn_signin);
		new_user.setOnClickListener(this);
		existing_user.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_newuser:
			startActivity(new Intent(Sign_in_Activity.this,New_user_Activity .class));

			break;
		case R.id.btn_signin:
			startActivity(new Intent(Sign_in_Activity.this,Login_Activity.class));

			break;

		default:
			break;
		}

	}

}
