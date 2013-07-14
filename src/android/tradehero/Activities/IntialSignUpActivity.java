package android.tradehero.Activities;

import org.brickred.socialauth.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ProgramVertexFixedFunction.Constants;
import android.tradehero.SocailAuth.DialogListener;
import android.tradehero.SocailAuth.SocialAuthAdapter;
import android.tradehero.SocailAuth.SocialAuthError;
import android.tradehero.SocailAuth.SocialAuthListener;
import android.tradehero.SocailAuth.SocialAuthAdapter.Provider;
import android.tradehero.Utills.Util;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class IntialSignUpActivity extends Activity implements OnClickListener{

	private Button mFaceBookBtn,mTwitterBtn,mLinkedinBtn;
	private TextView mEmailTv,mTerms;
	private SocialAuthAdapter mSocialAuthAdapter;
	private Profile mProfileMap;
	private Context mContext;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		_initSetup();
	}

	private void _initSetup(){
		setContentView(R.layout.sign_in_screen);
		mFaceBookBtn = (Button) findViewById(R.id.btn_fbook);
		mTwitterBtn = (Button) findViewById(R.id.btn_twitter);
		mLinkedinBtn = (Button) findViewById(R.id.btn_linkedin);
		mEmailTv = (TextView) findViewById(R.id.txt_email);
		mTerms = (TextView) findViewById(R.id.terms);
		mSocialAuthAdapter = new SocialAuthAdapter(new ResponseListener());
		mFaceBookBtn.setOnClickListener(this);
		mTwitterBtn.setOnClickListener(this);
		mLinkedinBtn.setOnClickListener(this);
		mTerms.setOnClickListener(this);
		mEmailTv.setOnClickListener(this);
		
		mContext=this;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_fbook:
			mSocialAuthAdapter.addCallBack(Provider.FACEBOOK,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			//startActivity(new Intent(New_user_Activity.this,Login_Activity.class));
			mSocialAuthAdapter.authorize(this, Provider.FACEBOOK);
			break;
		case R.id.btn_twitter:
			//startActivity(new Intent(New_user_Activity.this,New_user_Activity.class));
			mSocialAuthAdapter.addCallBack(Provider.TWITTER,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			mSocialAuthAdapter.authorize(this, Provider.TWITTER);

			break;
		case R.id.btn_linkedin:
			mSocialAuthAdapter.addCallBack(Provider.LINKEDIN,"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
			mSocialAuthAdapter.authorize(this, Provider.LINKEDIN);
			break;
		case R.id.txt_email:
			startActivity(new Intent(IntialSignUpActivity.this,EmailRegistrationActivity.class));
			break;
		case R.id.terms:
			Intent pWebView =new Intent(IntialSignUpActivity.this,WebViewActivity.class);
			pWebView.putExtra(WebViewActivity.SHOW_URL, android.tradehero.Utills.Constants.PRIVACY_TERMS_OF_SERVICE);
			startActivity(pWebView);
		default:
			break;
		}

	}

	private class ResponseListener implements DialogListener{


		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			mSocialAuthAdapter.getUserProfileAsync(new ProfileDataListener());


		}

		@Override
		public void onError(SocialAuthError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBack() {
			// TODO Auto-generated method stub

		}


		// To receive the profile response after authentication
		private final class ProfileDataListener implements SocialAuthListener<Profile> {

			@Override
			public void onExecute(Profile t) {
				mProfileMap = t;

				Util.show_toast(mContext,"profile fetched"+mProfileMap.getDisplayName() );
			}

			@Override
			public void onError(SocialAuthError e) {

			}
		}


	}
}
