package android.tradehero.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.tradehero.Utills.Constants;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Registration_User_Activity extends Activity implements OnClickListener{
	
	EditText email_id,pwd,c_pwd,disp_name,first_nm,last_nm;
	Button sign_up;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		_initSetup();
	}
	
	
	private void _initSetup(){
		setContentView(R.layout.user_register);
		email_id = (EditText) findViewById(R.id.et_emailid);
		pwd = (EditText) findViewById(R.id.et_password);
		c_pwd = (EditText) findViewById(R.id.et_confirm_password);
		disp_name = (EditText) findViewById(R.id.et_display_name);
		first_nm = (EditText) findViewById(R.id.et_firstname);
		last_nm = (EditText) findViewById(R.id.et_lasttname);
		sign_up = (Button) findViewById(R.id.btn_register);
		
		sign_up.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		
		
		if(v.getId()== R.id.btn_register){
			_handle_registration();
		}
		
	}


	private void _handle_registration() {
		
		if(!TextUtils.isEmpty(email_id.getText().toString()) || !TextUtils.isEmpty(pwd.getText().toString())
				|| !TextUtils.isEmpty(c_pwd.getText().toString()) || !TextUtils.isEmpty(disp_name.getText().toString())){
			
			if(){}else{
				Constants.show_toast(this, "Enter a valid email address.");
			}
			
		}else{
			Constants.show_toast(this, "Required Field should not left blank.");
		} 
		
	}

}
