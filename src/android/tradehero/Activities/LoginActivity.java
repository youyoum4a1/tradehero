package android.tradehero.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.tradehero.Utills.Util;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener{

	private TextView mForgotPassword;
	private Button mSignIn;
     EditText inputEmailName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_withemail_screen);
		mSignIn = (Button) findViewById(R.id.btn_login);
		mForgotPassword = (TextView) findViewById(R.id.txt_forgotpwd);
		
		mForgotPassword.setOnClickListener(this);
		mSignIn.setOnClickListener(this);
		

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_login:
			Util.show_toast(LoginActivity.this, "Log In");

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

}

