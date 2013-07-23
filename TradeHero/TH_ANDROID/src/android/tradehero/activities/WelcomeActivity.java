package android.tradehero.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tradehero.fragments.WelcomScreenFragment;

public class WelcomeActivity extends FragmentActivity {

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) 
		{
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		_initSetup();
	}

	private void _initSetup()
	{
		setContentView(R.layout.sign_in_up_content);
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		WelcomScreenFragment fragment = new WelcomScreenFragment();
		fragmentTransaction.replace(R.id.sign_in_up_content, fragment,"welcome_screen");
		fragmentTransaction.commit();

	}
}
