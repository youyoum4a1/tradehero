package android.tradehero.activities;

import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.tradehero.fragments.InitialSignUpFragment;
import android.tradehero.fragments.WelcomScreenFragment;
import android.tradehero.utills.Constants;
import android.tradehero.utills.Util;
import android.util.Log;

public class WelcomeActivity extends FragmentActivity {

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction ;
	private static SharedPreferences mSharedPreferences;
	private static Twitter twitter; 
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

//		if(!Constants.TWITTER_FLAG)
//		{
			WelcomScreenFragment fragment = new WelcomScreenFragment();
			fragmentTransaction.replace(R.id.sign_in_up_content, fragment,"welcome_screen");
//		}else
//		{
//			InitialSignUpFragment fragment = new InitialSignUpFragment();
//			fragmentTransaction.replace(R.id.sign_in_up_content, fragment,"initial_signup");	
//		}
		fragmentTransaction.commit();

	}







}
